/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.algo;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.services.handlers.NdcNormalizer;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.helpers.PrecedenceList;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.algo.AbstractTerminologyLoaderAlgorithm;
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
import com.wci.umls.server.model.workflow.WorkflowStatus;
import com.wci.umls.server.services.RootService;
import com.wci.umls.server.services.helpers.PushBackReader;

/**
 * Implementation of an algorithm to import NDC/RXNORM data.
 */
public class NdcLoaderAlgorithm extends AbstractTerminologyLoaderAlgorithm {

  /** The attributes flag. */
  boolean attributesFlag = true;

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

  /** The concept map. */
  private Map<String, Long> conceptIdMap = new HashMap<>(10000);

  /** The list. */
  private PrecedenceList list;

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
  @Override
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  /**
   * Gets the terminology.
   *
   * @return the terminology
   */
  @Override
  public String getTerminology() {
    return terminology;
  }

  /**
   * Sets the terminology version.
   *
   * @param version the terminology version
   */
  @Override
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Sets the attributes flag.
   *
   * @param attributesFlag the attributes flag
   */
  public void setAttributesFlag(boolean attributesFlag) {
    this.attributesFlag = attributesFlag;
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
  @Override
  public String getVersion() {
    return version;
  }

  /**
   * Sets the release version.
   *
   * @param releaseVersion the rlease version
   */
  @Override
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
      logInfo("  attributesFlag = " + attributesFlag);

      // Check the input directory
      final File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Sort files - not really needed because files are already sorted
      logInfo("  Sort RRF Files");
      final RrfFileSorter sorter = new RrfFileSorter();
      // Be flexible about missing files for RXNORM
      sorter.setRequireAllFiles(false);

      // If using "original readers" below, then dont need to sort
      // File outputDir = new File(inputDirFile, "/RRF-sorted-temp/");
      // sorter.sortFiles(inputDirFile, outputDir, "RXN");

      // Get release version
      String releaseVersion = sorter.getFileVersion(inputDirFile);
      if (releaseVersion == null) {
        releaseVersion = version;
      }
      logInfo("  releaseVersion = " + releaseVersion);

      // Open readers - just open original RRF
      readers = new RrfReaders(inputDirFile);
      // Use default prefix if not specified
      readers.openOriginalReaders("RXN");

      releaseVersionDate = ConfigUtility.DATE_FORMAT.parse(releaseVersion);

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

      // Make root terminology
      RootTerminology root = getRootTerminology(terminology);
      if (root == null) {
        root = new RootTerminologyJpa();
        root.setFamily(terminology);
        root.setPreferredName(terminology);
        root.setRestrictionLevel(0);
        root.setTerminology(terminology);
        root.setTimestamp(releaseVersionDate);
        root.setLastModified(releaseVersionDate);
        root.setLastModifiedBy(loader);
        addRootTerminology(root);
      }

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
      term.setRootTerminology(root);
      addTerminology(term);

      commitClearBegin();

      // Load the content
      list = getPrecedenceList(getTerminology(), getVersion());
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
      e.printStackTrace();
      logError(e.getMessage());
      throw e;
    }
  }

  /**
   * Load MRCONSO.RRF. This is responsible for loading {@link Atom}s and
   * {@link AtomClass}es.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadMrconso() throws Exception {
    logInfo("  Load MRCONSO");
    logInfo("  Insert atoms and concepts ");

    // Set up maps
    String line = null;

    int objectCt = 0;
    final PushBackReader reader = readers.getReader(RrfReaders.Keys.MRCONSO);
    final String fields[] = new String[18];
    String prevCui = null;
    Concept cui = new ConceptJpa();
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      FieldedStringTokenizer.split(line, "|", 18, fields);

      // Only create atoms if SAB=RXNORM
      if (!fields[11].equals("RXNORM")) {
        continue;
      }

      if (fields[11].equals("RXNORM")) {
        // Restrict to TTYs
        // BPCK, GPCK, PSN, SBD, SCD
        if (!fields[12].equals("BPCK") && !fields[12].equals("GPCK")
            && !fields[12].equals("PSN") && !fields[12].equals("SBD")
            && !fields[12].equals("SCD")) {
          continue;
        }
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
      // 38|ENG||||||829|829|38||RXNORM|BN|38|Parlodel||N|4096|

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
      atom.setVersion(version);

      atom.setTerminologyId(fields[8]);
      atom.setTermType(fields[12].intern());
      atom.setWorkflowStatus(WorkflowStatus.PUBLISHED);

      atom.setConceptId(fields[0]);
      atom.setCodeId("");
      atom.setDescriptorId("");
      atom.setConceptTerminologyIds(new HashMap<String, String>());
      atom.setStringClassId(fields[5]);
      atom.setLexicalClassId(fields[3]);

      // Add atoms and commit periodically
      addAtom(atom);
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
      // Add concept
      if (prevCui == null || !fields[0].equals(prevCui)) {
        if (prevCui != null) {
          cui.setName(getComputedPreferredName(cui, list));
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
        cui.setWorkflowStatus(WorkflowStatus.PUBLISHED);
      }
      cui.getAtoms().add(atom);
      prevCui = fields[0];
    }
    // Add last concept
    if (prevCui != null) {
      cui.setName(getComputedPreferredName(cui, list));
      addConcept(cui);
      conceptIdMap.put(prevCui, cui.getId());
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
    }

    // commit
    commitClearBegin();

  }

  /**
   * Load MRSAT. This is responsible for loading {@link Attribute}s.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("resource")
  private void loadMrsat() throws Exception {
    logInfo("  Load MRSAT data");
    String line = null;

    int objectCt = 0;
    final PushBackReader reader = readers.getReader(RrfReaders.Keys.MRSAT);
    // make set of all atoms that got an additional attribute

    final NdcNormalizer normalizer = new NdcNormalizer();
    final Set<Concept> modifiedConcepts = new HashSet<>();
    Map<String, Atom> ndcAtoms = new HashMap<>();
    Map<String, String> mthsplNdcCodeMap = new HashMap<>();
    Map<String, String> mthsplNdc9Map = new HashMap<>();
    Map<String, String> mthsplNdc10Map = new HashMap<>();
    Map<String, Set<Attribute>> attributeMap = new HashMap<>();
    Map<String, String> splSetIdMap = new HashMap<>();
    String prevCui = null;
    final String fields[] = new String[13];
    while ((line = reader.readLine()) != null) {
      line = line.replace("\r", "");
      FieldedStringTokenizer.split(line, "|", 13, fields);

      if (!fields[9].equals("RXNORM") && !fields[9].equals("MTHSPL")) {
        continue;
      }

      // Handle CUI changing - create data structures
      if (prevCui == null || !fields[0].equals(prevCui)) {
        if (prevCui != null) {
          connectAtomsAndAttributes(fields, ndcAtoms, mthsplNdcCodeMap,
              mthsplNdc9Map, mthsplNdc10Map, attributeMap, splSetIdMap,
              modifiedConcepts);
          // log and commit
          logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
          ndcAtoms = new HashMap<>();
          mthsplNdcCodeMap = new HashMap<>();
          mthsplNdc9Map = new HashMap<>();
          mthsplNdc10Map = new HashMap<>();
          attributeMap = new HashMap<>();
          splSetIdMap = new HashMap<>();
        }
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
      // 197589|||1907932|AUI|197589|||NDC|RXNORM|12634069891|N|4096|
      // 448|||3311306|AUI|3K9958V90M|||SPL_SET_ID|MTHSPL|4192e27f-0034-4cd9-b9e0-27b52cb4b970|N|4096|

      // Create NDC atoms from RXNORM NDC Attributes
      if (fields[8].equals("NDC") && fields[9].equals("RXNORM")) {

        final Atom atom = new AtomJpa();
        atom.setTimestamp(releaseVersionDate);
        atom.setLastModified(releaseVersionDate);
        atom.setLastModifiedBy(loader);
        atom.setObsolete(fields[11].equals("O"));
        atom.setSuppressible(!fields[11].equals("N"));
        atom.setPublished(true);
        atom.setPublishable(true);
        atom.setTerminologyId(fields[3]);
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
        ndcAtoms.put(fields[10], atom);

      }

      // Save the SPL_SET_ID - Do not render as an attribute
      else if (fields[8].equals("SPL_SET_ID") && fields[9].equals("MTHSPL")) {
        // 206977|||2621612|AUI|0409-1276|||SPL_SET_ID|MTHSPL|A13AE145-5C5E-422B-D0A5-6CF34C1B8262|N||
        splSetIdMap.put(fields[3], fields[10].toLowerCase());
      }

      // Save the NDC/code map for an MTHSPL NDC entry
      // Use the normalizer algorithm
      // Do not render as an attribute
      else if (fields[8].equals("NDC") && fields[9].equals("MTHSPL")) {
        // 206977|||2621611|AUI|0409-1276|||NDC|MTHSPL|0409-1276-32|N||

        final List<ScoredResult> results =
            normalizer.normalize(fields[10], null);
        if (results.size() == 1) {
          mthsplNdcCodeMap.put(results.get(0).getValue(), fields[3]);
          mthsplNdc9Map.put(fields[3], fields[5]);
          mthsplNdc10Map.put(fields[3] + results.get(0).getValue(), fields[10]);
        } else if (results.size() == 0) {
          Logger.getLogger(getClass())
              .warn("  MTHSPL NDC cannot be normalized: " + line);
        } else if (results.size() > 1) {
          throw new Exception(
              "Unexpected multiple normalization results: " + results);
        }
      }

      // Create attributes for MTHSPL entries
      else if (fields[9].equals("MTHSPL")) {

        final Attribute att = new AttributeJpa();
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
        if (!attributeMap.containsKey(fields[3])) {
          attributeMap.put(fields[3], new HashSet<>());
        }
        attributeMap.get(fields[3]).add(att);
      }

      prevCui = fields[0];

    } // end while loop

    // Handle last data
    if (prevCui != null) {
      connectAtomsAndAttributes(fields, ndcAtoms, mthsplNdcCodeMap,
          mthsplNdc9Map, mthsplNdc10Map, attributeMap, splSetIdMap,
          modifiedConcepts);
    }

    // commit
    commitClearBegin();

    // Handle modified concepts
    for (Concept concept : modifiedConcepts) {
      updateConcept(concept);

      // log and commit
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
    }

    // commit
    commitClearBegin();

  }

  /**
   * Connect atoms and attributes.
   *
   * @param fields the fields
   * @param ndcAtoms the ndc atoms
   * @param mthsplNdcCodeMap the mthspl ndc code map
   * @param mthsplNdc9Map the mthspl ndc 9 map
   * @param mthsplNdc10Map the mthspl ndc 10 map
   * @param attributeMap the attribute map
   * @param splSetIdMap the spl set id map
   * @param modifiedConcepts the modified concepts
   * @throws Exception the exception
   */
  private void connectAtomsAndAttributes(String[] fields,
    Map<String, Atom> ndcAtoms, Map<String, String> mthsplNdcCodeMap,
    Map<String, String> mthsplNdc9Map, Map<String, String> mthsplNdc10Map,
    Map<String, Set<Attribute>> attributeMap, Map<String, String> splSetIdMap,
    Set<Concept> modifiedConcepts) throws Exception {
    // For each NDC Atom
    for (final Map.Entry<String, Atom> entry : ndcAtoms.entrySet()) {
      final String ndcCode = entry.getKey();
      final Atom ndcAtom = entry.getValue();

      final Concept concept =
          getConcept(conceptIdMap.get(ndcAtom.getConceptId()));

      final String mthsplCode = mthsplNdcCodeMap.get(ndcCode);
      if (attributesFlag && mthsplCode != null) {
        final String ndc9 = mthsplNdc9Map.get(mthsplCode);
        final String ndc10 = mthsplNdc10Map.get(mthsplCode + ndcCode);

        if (ndc9 != null) {
          final Attribute att = new AttributeJpa();
          att.setName("NDC9");
          att.setValue(ndc9);
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
          addAttribute(att, ndcAtom);
          ndcAtom.getAttributes().add(att);
        }

        if (ndc10 != null) {
          final Attribute att = new AttributeJpa();
          att.setName("NDC10");
          att.setValue(ndc10);
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
          addAttribute(att, ndcAtom);
          ndcAtom.getAttributes().add(att);
        }

      }

      // assign SPL_SET_ID to the codeId if it exists
      if (splSetIdMap.containsKey(mthsplCode)) {
        ndcAtom.setCodeId(splSetIdMap.get(mthsplCode));
      }

      // Get attributes
      if (attributesFlag && mthsplCode != null) {
        for (final Attribute attribute : attributeMap.get(mthsplCode)) {
          final Attribute copy = new AttributeJpa(attribute);
          copy.setId(null);
          addAttribute(copy, ndcAtom);
          ndcAtom.getAttributes().add(copy);
        }
      }

      // Add the NDC atom
      concept.getAtoms().add(ndcAtom);
      addAtom(ndcAtom);
      modifiedConcepts.add(concept);
    }
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

  /* see superclass */
  @Override
  public String getFileVersion() throws Exception {
    // Multi-version handler
    return null;
  }

  @Override
  public ValidationResult checkPreconditions() throws Exception {
    // unused
    return null;
  }

  @Override
  public void checkProperties(Properties properties) throws Exception {
    // unused
  }

  @Override
  public void setProperties(Properties properties) throws Exception {
    // unused
  }
}
