/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.algo;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wci.umls.server.algo.Algorithm;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.jpa.algo.AbstractLoaderAlgorithm;
import com.wci.umls.server.jpa.algo.RrfFileSorter;
import com.wci.umls.server.jpa.algo.RrfReaders;
import com.wci.umls.server.jpa.content.AtomJpa;
import com.wci.umls.server.jpa.content.AttributeJpa;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.meta.RootTerminologyJpa;
import com.wci.umls.server.jpa.meta.TerminologyJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.AtomClass;
import com.wci.umls.server.model.content.Attribute;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.meta.IdType;
import com.wci.umls.server.model.meta.Language;
import com.wci.umls.server.model.meta.RootTerminology;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.services.RootService;
import com.wci.umls.server.services.helpers.ProgressEvent;
import com.wci.umls.server.services.helpers.ProgressListener;
import com.wci.umls.server.services.helpers.PushBackReader;

import gnu.trove.strategy.HashingStrategy;

/**
 * Implementation of an algorithm to import NDC/RXNORM data.
 */
public class NdcLoaderAlgorithm extends AbstractLoaderAlgorithm
    implements Algorithm {

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The terminology. */
  private String terminology;

  /** The terminology version. */
  private String version;

  /** The release version. */
  private String releaseVersion;
  
  /** The input dir. */
  private String inputDir;

  /** The release version date. */
  private Date releaseVersionDate;

  /** The readers. */
  private RrfReaders readers;

  /** The loader. */
  private final String loader = "loader";

  /** The published. */
  private final String published = "PUBLISHED";

  /** The loaded terminologies. */
  private Map<String, Terminology> loadedTerminologies = new HashMap<>();

  /** The loaded root terminologies. */
  private Map<String, RootTerminology> loadedRootTerminologies =
      new HashMap<>();

  /** The loaded languages. */
  private Map<String, Language> loadedLanguages = new HashMap<>();


  /** The term id type map. */
  private Map<String, IdType> termIdTypeMap = new HashMap<>();


  /** The atom map. */
  private Map<String, Long> atomIdMap = new HashMap<>(10000);


  /** The lat code map. */
  private static Map<String, String> latCodeMap = new HashMap<>();

  static {

    // from http://www.nationsonline.org/oneworld/country_code_list.htm
    latCodeMap.put("BAQ", "eu");
    latCodeMap.put("CZE", "cz");
    latCodeMap.put("DAN", "dk");
    latCodeMap.put("DUT", "nl");
    latCodeMap.put("ENG", "en");
    latCodeMap.put("FIN", "fi");
    latCodeMap.put("FRE", "fr");
    latCodeMap.put("GER", "de");
    latCodeMap.put("HEB", "he");
    latCodeMap.put("HUN", "hu");
    latCodeMap.put("ITA", "it");
    latCodeMap.put("JPN", "ja");
    latCodeMap.put("KOR", "ko");
    latCodeMap.put("LAV", "lv");
    latCodeMap.put("NOR", "nn");
    latCodeMap.put("POL", "pl");
    latCodeMap.put("POR", "pt");
    latCodeMap.put("RUS", "ru");
    latCodeMap.put("SCR", "sc");
    latCodeMap.put("SPA", "es");
    latCodeMap.put("SWE", "sv");
    latCodeMap.put("CHI", "zh");
    latCodeMap.put("TUR", "tr");
    latCodeMap.put("EST", "et");
    latCodeMap.put("GRE", "el");
  }

  /**
   * Instantiates an empty {@link NdcLoaderAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public NdcLoaderAlgorithm() throws Exception {
    super();
  }

  /**
   * Sets the terminology.
   *
   * @param terminology the terminology
   */
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  /**
   * Gets the terminology.
   *
   * @return the terminology
   */
  public String getTerminology() {
    return terminology;
  }

  /**
   * Sets the terminology version.
   *
   * @param version the terminology version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Sets the input dir.
   *
   * @param inputDir the new input dir
   */
  public void setInputDir(String inputDir) {
    this.inputDir = inputDir;
  }
  
  /**
   * Gets the version.
   *
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the release version.
   *
   * @param releaseVersion the rlease version
   */
  public void setReleaseVersion(String releaseVersion) {
    this.releaseVersion = releaseVersion;
  }


  /**
   * Sets the readers.
   *
   * @param readers the readers
   */
  public void setReaders(RrfReaders readers) {
    this.readers = readers;
  }


  /**
   * Compute.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void compute() throws Exception {
    try {
      Logger.getLogger(getClass()).info("Start loading RRF");
      Logger.getLogger(getClass()).info("  terminology = " + terminology);
      Logger.getLogger(getClass()).info("  version = " + version);
      Logger.getLogger(getClass()).info("  releaseVersion = " + releaseVersion);
      
      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Sort files - not really needed because files are already sorted
      Logger.getLogger(getClass()).info("  Sort RRF Files");
      final RrfFileSorter sorter = new RrfFileSorter();
      // Be flexible about missing files for RXNORM
      sorter
          .setRequireAllFiles(false);
      // File outputDir = new File(inputDirFile, "/RRF-sorted-temp/");
      // sorter.sortFiles(inputDirFile, outputDir);
      String releaseVersion = sorter.getFileVersion(inputDirFile);
      if (releaseVersion == null) {
        releaseVersion = version;
      }
      Logger.getLogger(getClass()).info("  releaseVersion = " + releaseVersion);

      // Open readers - just open original RRF
      final RrfReaders readers = new RrfReaders(inputDirFile);
      // Use default prefix if not specified
      readers.openOriginalReaders("RXN");

      
      releaseVersionDate = ConfigUtility.DATE_FORMAT
          .parse(releaseVersion.substring(0, 4) + "0101");

      // Track system level information
      long startTimeOrig = System.nanoTime();

      // control transaction scope
      setTransactionPerOperation(false);
      // Turn of ID computation when loading a terminology
      setAssignIdentifiersFlag(false);
      // Let loader set last modified flags.
      setLastModifiedFlag(false);

      // faster performance.
      beginTransaction();

      // make terminology
      final Terminology term = new TerminologyJpa();
      term.setAssertsRelDirection(false);
      term.setCurrent(true);
      term.setOrganizingClassType(IdType.CONCEPT);
      term.setPreferredName(terminology);
      term.setTimestamp(releaseVersionDate);
      term.setLastModified(releaseVersionDate);
      term.setLastModifiedBy(loader);
      term.setTerminology(terminology);
      term.setVersion(version);
      term.setDescriptionLogicTerminology(false);
      term.setMetathesaurus(true);

      RootTerminology root = loadedRootTerminologies.get(terminology);
      root = new RootTerminologyJpa();
        root.setFamily(terminology);
        root.setPreferredName(terminology);
        root.setRestrictionLevel(0);
        root.setTerminology(terminology);
        root.setTimestamp(releaseVersionDate);
        root.setLastModified(releaseVersionDate);
        root.setLastModifiedBy(loader);
        root.setLanguage(loadedLanguages.get("ENG"));
        
      // Load the content
      loadMrconso();

      // Attributes
      loadMrsat();

      // Final logging messages
      Logger.getLogger(getClass()).info("      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("Done ...");

      // clear and commit
      commit();
      clear();

    } catch (Exception e) {
      Logger.getLogger(getClass()).error(e.getMessage());
      throw e;
    }
  }




  /**
   * Load MRSAT. This is responsible for loading {@link Attribute}s.
   *
   * @throws Exception the exception
   */
  private void loadMrsat() throws Exception {
    Logger.getLogger(getClass()).info("  Load MRSAT data");
    String line = null;

    int objectCt = 0;
    final PushBackReader reader = readers.getReader(RrfReaders.Keys.MRSAT);
    // make set of all atoms that got an additional attribute
    
    final String fields[] = new String[13];
    while ((line = reader.readLine()) != null) {
      line = line.replace("\r", "");
      FieldedStringTokenizer.split(line, "|", 13, fields);

      
      // Only create attributes if ATN=NDC
      if (!fields[8].equals("NDC")) {
        continue;
      }
      
      if (!fields[9].equals("RXNORM")) {
        continue;
      }

      // Field Description
      // 0 CUI
      // 1 LUI
      // 2 SUI
      // 3 METAUI
      // 4 STYPE
      // 5 CODE
      // 6 ATUI
      // 7 SATUI
      // 8 ATN
      // 9 SAB
      // 10 ATV
      // 11 SUPPRESS
      // 12 CVF
      //
      // e.g.
      // C0001175|L0001175|S0010339|A0019180|SDUI|D000163|AT38209082||FX|MSH|D015492|N||
      // C0001175|L0001175|S0354232|A2922342|AUI|62479008|AT24600515||DESCRIPTIONSTATUS|SNOMEDCT|0|N||
      // C0001175|L0001842|S0011877|A15662389|CODE|T1|AT100434486||URL|MEDLINEPLUS|http://www.nlm.nih.gov/medlineplus/aids.html|N||
      // C0001175|||R54775538|RUI||AT63713072||CHARACTERISTICTYPE|SNOMEDCT|0|N||
      // C0001175|||R54775538|RUI||AT69142126||REFINABILITY|SNOMEDCT|1|N||
      final Attribute att = new AttributeJpa();

      att.setTimestamp(releaseVersionDate);
      att.setLastModified(releaseVersionDate);
      att.setLastModifiedBy(loader);
      att.setObsolete(fields[11].equals("O"));
      att.setSuppressible(!fields[11].equals("N"));
      att.setPublished(true);
      att.setPublishable(true);
      // fields[5] CODE not used - redundant
      att.setTerminologyId(fields[7]);
      att.setTerminology(fields[9].intern());
      if (loadedTerminologies.get(fields[9]) == null) {
        throw new Exception(
            "Attribute references terminology that does not exist: "
                + fields[9]);
      } else {
        att.setVersion(loadedTerminologies.get(fields[9]).getVersion());
      }
      att.setName(fields[8]);
      att.setValue(fields[10]);

      if (fields[4].equals("AUI")) {
        // Get the concept for the AUI
        Atom atom = getAtom(atomIdMap.get(fields[3]));
        atom.addAttribute(att);
        addAttribute(att, atom);
      } 


      // log and commit
      logAndCommit(objectCt, RootService.logCt, RootService.commitCt);

      //
      // NOTE: there are no subset attributes in RRF
      //

    } // end while loop


    // commit
    commitClearBegin();

  }








  /**
   * Load MRCONSO.RRF. This is responsible for loading {@link Atom}s and
   * {@link AtomClass}es.
   *
   * @throws Exception the exception
   */
  private void loadMrconso() throws Exception {
    Logger.getLogger(getClass()).info("  Load MRCONSO");
    Logger.getLogger(getClass()).info("  Insert atoms and concepts ");

    // Set up maps
    String line = null;

    int objectCt = 0;
    final PushBackReader reader = readers.getReader(RrfReaders.Keys.MRCONSO);
    final String fields[] = new String[18];
    String prevCui = null;
    Concept cui = null;
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      FieldedStringTokenizer.split(line, "|", 18, fields);

    
      // Only create atoms if SAB=RXNORM
      if (!fields[11].equals("RXNORM")) {
        continue;
      }

      // Field Description
      // 0 CUI
      // 1 LAT
      // 2 TS
      // 3 LUI
      // 4 STT
      // 5 SUI
      // 6 ISPREF
      // 7 AUI
      // 8 SAUI
      // 9 SCUI
      // 10 SDUI
      // 11 SAB
      // 12 TTY
      // 13 CODE
      // 14 STR
      // 15 SRL
      // 16 SUPPRESS
      // 17 CVF
      //
      // e.g.
      // C0000005|ENG|P|L0000005|PF|S0007492|Y|A7755565||M0019694|D012711|MSH|PEN|D012711|(131)I-Macroaggregated
      // Albumin|0|N|256|

      // set the root terminology language
      loadedRootTerminologies.get(fields[11])
          .setLanguage(loadedLanguages.get(fields[1]));

      final Atom atom = new AtomJpa();
      atom.setLanguage(fields[1].intern());
      atom.setTimestamp(releaseVersionDate);
      atom.setLastModified(releaseVersionDate);
      atom.setLastModifiedBy(loader);
      atom.setObsolete(fields[16].equals("O"));
      atom.setSuppressible(!fields[16].equals("N"));
      atom.setPublished(true);
      atom.setPublishable(true);
      atom.setName(fields[14]);
      atom.setTerminology(fields[11].intern());
      if (loadedTerminologies.get(fields[11]) == null) {
        throw new Exception(
            "Atom references terminology that does not exist: " + fields[11]);
      }
      atom.setVersion(
          loadedTerminologies.get(fields[11]).getVersion().intern());
      // skip in single mode

      atom.setTerminologyId(fields[8]);
      atom.setTermType(fields[12].intern());
      atom.setWorkflowStatus(published);


      atom.setConceptId(fields[9]);

      atom.setStringClassId(fields[5]);
      atom.setLexicalClassId(fields[3]);



      termIdTypeMap.put(atom.getTerminology(), IdType.CONCEPT);
      

      // Add atoms and commit periodically
      addAtom(atom);
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
      atomIdMap.put(fields[7], atom.getId());
      // Add concept
      if (prevCui == null || !fields[0].equals(prevCui)) {
        if (prevCui != null) {
          cui.setName(getComputedPreferredName(cui));
          addConcept(cui);
          
          logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
        }
        cui = new ConceptJpa();
        cui.setTimestamp(releaseVersionDate);
        cui.setLastModified(releaseVersionDate);
        cui.setLastModifiedBy(loader);
        cui.setPublished(true);
        cui.setPublishable(true);
        cui.setTerminology(terminology);
        cui.setTerminologyId(fields[0]);
        cui.setVersion(version);
        cui.setWorkflowStatus(published);
      }
      cui.addAtom(atom);
      prevCui = fields[0];
    }
    // Add last concept
    if (prevCui != null) {
      cui.setName(getComputedPreferredName(cui));
      addConcept(cui);

      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
    }


    // commit
    commitClearBegin();

  }

  /**
   * Load atoms before computing.
   *
   * @param atomClass the atom class
   * @return the computed preferred name
   * @throws Exception the exception
   */
  @Override
  public String getComputedPreferredName(AtomClass atomClass) throws Exception {
    final List<Atom> atoms = new ArrayList<>();
    for (final Atom atom : atomClass.getAtoms()) {
      atoms.add(getAtom(atom.getId()));
    }
    atomClass.setAtoms(atoms);
    return super.getComputedPreferredName(atomClass);
  }



  /**
   * Reset.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void reset() throws Exception {
    // do nothing
  }

  /**
   * Fires a {@link ProgressEvent}.
   * @param pct percent done
   * @param note progress note
   */
  public void fireProgressEvent(int pct, String note) throws Exception {
    final ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(getClass()).info("    " + pct + "% " + note);
  }

  /**
   * Adds the progress listener.
   *
   * @param l the l
   */
  /* see superclass */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /**
   * Removes the progress listener.
   *
   * @param l the l
   */
  /* see superclass */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /**
   * Cancel.
   */
  /* see superclass */
  @Override
  public void cancel() {
    throw new UnsupportedOperationException("cannot cancel.");
  }

  /**
   * Returns the elapsed time.
   *
   * @param time the time
   * @return the elapsed time
   */
  @SuppressWarnings({
      "boxing", "unused"
  })
  private static Long getElapsedTime(long time) {
    return (System.nanoTime() - time) / 1000000000;
  }

  /**
   * Returns the total elapsed time str.
   *
   * @param time the time
   * @return the total elapsed time str
   */
  @SuppressWarnings("boxing")
  private static String getTotalElapsedTimeStr(long time) {
    Long resultnum = (System.nanoTime() - time) / 1000000000;
    String result = resultnum.toString() + "s";
    resultnum = resultnum / 60;
    result = result + " / " + resultnum.toString() + "m";
    resultnum = resultnum / 60;
    result = result + " / " + resultnum.toString() + "h";
    return result;
  }

  /**
   * Close.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void close() throws Exception {
    super.close();
    readers = null;
  }

  /**
   * Standard hashing strategy.
   */
  @SuppressWarnings("serial")
  public class StandardStrategy implements HashingStrategy<String> {

    /**
     * Instantiates an empty {@link StandardStrategy}.
     */
    public StandardStrategy() {
      // n/a
    }

    /* see superclass */
    @Override
    public int computeHashCode(String object) {
      return object.hashCode();
    }

    /* see superclass */
    @Override
    public boolean equals(String o1, String o2) {
      return o1.equals(o2);
    }

  }

}
