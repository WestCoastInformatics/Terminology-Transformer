/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.algo;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

  private Terminology term;

  /** The loaded root terminologies. */
  private Map<String, RootTerminology> loadedRootTerminologies =
      new HashMap<>();

  /** The term id type map. */
  private Map<String, IdType> termIdTypeMap = new HashMap<>();

  /** The concept map. */
  private Map<String, Long> conceptIdMap = new HashMap<>(10000);

  /** The atom map. */
  private Map<String, Long> atomIdMap = new HashMap<>(10000);

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
      logInfo("Start loading RRF");
      logInfo("  terminology = " + terminology);
      logInfo("  version = " + version);
      logInfo("  releaseVersion = " + releaseVersion);
      logInfo("  inputDir = " + inputDir);

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Sort files - not really needed because files are already sorted
      logInfo("  Sort RRF Files");
      final RrfFileSorter sorter = new RrfFileSorter();
      // Be flexible about missing files for RXNORM
      sorter.setRequireAllFiles(false);
      // File outputDir = new File(inputDirFile, "/RRF-sorted-temp/");
      // sorter.sortFiles(inputDirFile, outputDir);
      // TODO: this is only getting the year right, not the correct month and day
      String releaseVersion = sorter.getFileVersion(inputDirFile);
      if (releaseVersion == null) {
        releaseVersion = version;
      }
      logInfo("  releaseVersion = " + releaseVersion);

      // Open readers - just open original RRF
      readers = new RrfReaders(inputDirFile);
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
      term = new TerminologyJpa();
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

      // Load the content
      loadMrconso();

      // Attributes
      loadMrsat();

      // Final logging messages
      logInfo("      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      logInfo("Done ...");

      // clear and commit
      commit();
      clear();

    } catch (Exception e) {
      logError(e.getMessage());
      throw e;
    }
  }

  /**
   * Load MRSAT. This is responsible for loading {@link Attribute}s.
   *
   * @throws Exception the exception
   */
  private void loadMrsat() throws Exception {
    logInfo("  Load MRSAT data");
    String line = null;

    int objectCt = 0;
    final PushBackReader reader = readers.getReader(RrfReaders.Keys.MRSAT);
    // make set of all atoms that got an additional attribute

    Set<Concept> modifiedConcepts = new HashSet<>();
    final String fields[] = new String[13];
    while ((line = reader.readLine()) != null) {
      line = line.replace("\r", "");
      FieldedStringTokenizer.split(line, "|", 13, fields);

      if (!fields[9].equals("RXNORM") && !fields[9].equals("MTHSPL")) {
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

      if (fields[8].equals("NDC") && fields[9].equals("RXNORM")) {

        final Atom atom = new AtomJpa();

        atom.setTimestamp(releaseVersionDate);
        atom.setLastModified(releaseVersionDate);
        atom.setLastModifiedBy(loader);
        atom.setObsolete(fields[11].equals("O"));
        atom.setSuppressible(!fields[11].equals("N"));
        atom.setPublished(true);
        atom.setPublishable(true);
        // fields[5] CODE not used - redundant
        atom.setTerminologyId(fields[7]);
        atom.setTerminology(fields[9].intern());
        if (!terminology.equals(fields[9])) {
          throw new Exception(
              "Attribute references terminology that does not exist: "
                  + fields[9]);
        } else {
          atom.setVersion(version);
        }
        atom.setName(fields[10]);
        atom.setConceptId(fields[0]);
        atom.setCodeId("");
        atom.setDescriptorId("");
        atom.setConceptTerminologyIds(new HashMap<String, String>());
        atom.setStringClassId("");
        atom.setLexicalClassId("");
        atom.setTermType("NDC");
        atom.setLanguage("ENG");

        Concept concept = getConcept(conceptIdMap.get(fields[0]));
        concept.addAtom(atom);
        addAtom(atom);
        modifiedConcepts.add(concept);

      // load as an attribute that is connected to the MTHSPL aui
      } else if (fields[9].equals("MTHSPL")) {

        Long aui = atomIdMap.get(fields[3]);
        Atom atom = getAtom(aui);
        Attribute att = new AttributeJpa();
        att.setName(fields[8]); 
        att.setValue(fields[10]);
        att.setTimestamp(releaseVersionDate);
        att.setLastModified(releaseVersionDate);
        att.setLastModifiedBy(loader);
        att.setObsolete(false);
        att.setSuppressible(false);
        att.setPublished(true);
        att.setPublishable(true);
        att.setTerminology(terminology);
        att.setVersion(version);
        att.setTerminologyId("");

        addAttribute(att, atom);
      }
      // log and commit
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);

    } // end while loop
    


    // commit
    commitClearBegin();

    for (Concept c : modifiedConcepts) {
      updateConcept(c);
      
      // log and commit
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);

    }
    

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
    logInfo("  Load MRCONSO");
    logInfo("  Insert atoms and concepts ");

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

      // Only create atoms if SAB=RXNORM or MTHSPL
      if (!fields[11].equals("RXNORM") && !fields[11].equals("MTHSPL")) {
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
      /*loadedRootTerminologies.get(fields[11])
          .setLanguage(loadedLanguages.get(fields[1]));*/

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
      if (!terminology.equals(fields[11])) {
        throw new Exception(
            "Atom references terminology that does not exist: " + fields[11]);
      }
      atom.setVersion(version);
      // skip in single mode

      atom.setTerminologyId(fields[8]);
      atom.setTermType(fields[12].intern());
      atom.setWorkflowStatus(published);

      atom.setConceptId(fields[9]);
      atom.setCodeId("");
      atom.setDescriptorId("");
      atom.setConceptTerminologyIds(new HashMap<String, String>());
      atom.setStringClassId(fields[5]);
      atom.setLexicalClassId(fields[3]);

      termIdTypeMap.put(atom.getTerminology(), IdType.CONCEPT);

      // Add atoms and commit periodically
      addAtom(atom);
      atomIdMap.put(fields[7], atom.getId());
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
      // Add concept
      if (prevCui == null || !fields[0].equals(prevCui)) {
        if (prevCui != null) {
          cui.setName(getComputedPreferredName(cui));
          addConcept(cui);
          conceptIdMap.put(prevCui, cui.getId());
          
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
   *
   * @param pct percent done
   * @param note progress note
   * @throws Exception the exception
   */
  public void fireProgressEvent(int pct, String note) throws Exception {
    final ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    logInfo("    " + pct + "% " + note);
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
