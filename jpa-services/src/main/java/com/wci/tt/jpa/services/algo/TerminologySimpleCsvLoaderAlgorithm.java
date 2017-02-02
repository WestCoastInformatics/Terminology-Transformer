/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.algo;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wci.umls.server.AlgorithmParameter;
import com.wci.umls.server.ReleaseInfo;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.helpers.KeyValuePair;
import com.wci.umls.server.helpers.PrecedenceList;
import com.wci.umls.server.jpa.AlgorithmParameterJpa;
import com.wci.umls.server.jpa.ReleaseInfoJpa;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.algo.AbstractTerminologyLoaderAlgorithm;
import com.wci.umls.server.jpa.content.AtomJpa;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.content.SemanticTypeComponentJpa;
import com.wci.umls.server.jpa.helpers.PrecedenceListJpa;
import com.wci.umls.server.jpa.meta.LanguageJpa;
import com.wci.umls.server.jpa.meta.RootTerminologyJpa;
import com.wci.umls.server.jpa.meta.SemanticTypeJpa;
import com.wci.umls.server.jpa.meta.TermTypeJpa;
import com.wci.umls.server.jpa.meta.TerminologyJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Component;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.SemanticTypeComponent;
import com.wci.umls.server.model.meta.CodeVariantType;
import com.wci.umls.server.model.meta.IdType;
import com.wci.umls.server.model.meta.Language;
import com.wci.umls.server.model.meta.NameVariantType;
import com.wci.umls.server.model.meta.RootTerminology;
import com.wci.umls.server.model.meta.SemanticType;
import com.wci.umls.server.model.meta.TermType;
import com.wci.umls.server.model.meta.TermTypeStyle;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.model.meta.UsageType;
import com.wci.umls.server.model.workflow.WorkflowStatus;
import com.wci.umls.server.services.RootService;
import com.wci.umls.server.services.handlers.IdentifierAssignmentHandler;
import com.wci.umls.server.services.helpers.PushBackReader;

/**
 * Implementation of an algorithm to import data from two files.
 * 
 * <pre>
 * 1. a conceptId|type|pt[|sy]* file 2. a par/chd relationships file
 */
public class TerminologySimpleCsvLoaderAlgorithm
    extends AbstractTerminologyLoaderAlgorithm {

  /** The loader. */
  private final String loader = "loader";

  /** The date. */
  private final Date date = new Date();

  /** The concepts file name. */
  private String conceptsFile;

  /** Whether to keep file ids or compute new ids . */
  private boolean keepFileIds = false;

  /**
   * Instantiates an empty {@link TerminologySimpleCsvLoaderAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public TerminologySimpleCsvLoaderAlgorithm() throws Exception {
    super();
  }

  public void setInputFile(String conceptsFile) {
    this.conceptsFile = conceptsFile;
  }

  public String getInputFile() {
    return this.conceptsFile;
  }

  /**
   * Compute.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void compute() throws Exception {

    logInfo("Start simple load");
    logInfo("  terminology = " + getTerminology());
    logInfo("  version = " + getVersion());
    logInfo("  inputFile = " + getInputFile());

    // Set the "release version"
    setReleaseVersion(ConfigUtility.DATE_FORMAT.format(date));
    // Track system level information
    long startTimeOrig = System.nanoTime();
    // control transaction scope
    setTransactionPerOperation(false);
    // Turn of ID computation when loading a terminology
    setAssignIdentifiersFlag(false);
    // Let loader set last modified flags.
    setLastModifiedFlag(false);
    // Turn off action handling
    setMolecularActionFlag(false);

    // Check the input directory
    File inputDirFile = new File(getInputFile());
    if (!inputDirFile.exists()) {
      throw new Exception("Specified input file does not exist");
    }

    // faster performance.
    beginTransaction();

    // Semantic type, termTypes, languages, PAR/CHD rel types, prec list, etc.
    loadMetadata();

    // Assume files concepts.txt, parChd.txt
    loadAtoms();

    // Commit
    commitClearBegin();

    // Add release info for this load
    final Terminology terminology =
        getTerminologyLatestVersion(getTerminology());
    ReleaseInfo info =
        getReleaseInfo(terminology.getTerminology(), getReleaseVersion());
    if (info == null) {
      info = new ReleaseInfoJpa();
      info.setName(getTerminology());
      info.setDescription(terminology.getTerminology() + " "
          + getReleaseVersion() + " release");
      info.setPlanned(false);
      info.setPublished(true);
      info.setReleaseBeginDate(null);
      info.setTerminology(terminology.getTerminology());
      info.setVersion(getReleaseVersion());
      info.setLastModified(date);
      info.setLastModifiedBy(loader);
      info.setTimestamp(new Date());
      addReleaseInfo(info);
    } else {
      throw new Exception("Release info unexpectedly already exists for "
          + getReleaseVersion());
    }

    // Clear concept cache

    logInfo("Log component stats");
    final Map<String, Integer> stats = getComponentStats(null, null, null);
    final List<String> statsList = new ArrayList<>(stats.keySet());
    Collections.sort(statsList);
    for (final String key : statsList) {
      logInfo("  " + key + " = " + stats.get(key));
    }

    // clear and commit
    commit();
    clear();

    // Final logging messages
    Logger.getLogger(getClass())
        .info("      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
    Logger.getLogger(getClass()).info("done ...");

  }

  /**
   * Compute metadata from CSV file
   *
   * @throws Exception the exception
   */
  private void loadMetadata() throws Exception {
    logInfo("  Load Semantic types");

    String line = null;
    int objectCt = 0;

    final PushBackReader reader =
        new PushBackReader(new FileReader(new File(conceptsFile)));
    final String[] fields = new String[4];

    final Set<String> types = new HashSet<>();
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      FieldedStringTokenizer.split(line, ",", 4, fields);

      if (!ConfigUtility.isEmpty(fields[23])) {
        types.add(fields[2]);
      }
    }
    reader.close();

    // Create a semantic type for each unique value
    for (final String type : types) {

      final SemanticType sty = new SemanticTypeJpa();
      sty.setAbbreviation(type);
      sty.setDefinition("");
      sty.setExample("");
      sty.setExpandedForm(type);
      sty.setNonHuman(false);
      sty.setTerminology(getTerminology());
      sty.setVersion(getVersion());
      sty.setTreeNumber("");
      sty.setTypeId("");
      sty.setUsageNote("");
      sty.setTimestamp(date);
      sty.setLastModified(date);
      sty.setLastModifiedBy(loader);
      sty.setPublished(true);
      sty.setPublishable(true);
      Logger.getLogger(getClass()).debug("    add semantic type - " + sty);
      addSemanticType(sty);
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);

    }
    commitClearBegin();

    // Root terminology
    final RootTerminology root = new RootTerminologyJpa();
    root.setFamily(getTerminology());
    root.setPreferredName(getTerminology());
    root.setRestrictionLevel(0);
    root.setTerminology(getTerminology());
    root.setTimestamp(date);
    root.setLastModified(date);
    root.setLastModifiedBy(loader);
    addRootTerminology(root);

    // Terminology
    final Terminology term = new TerminologyJpa();
    term.setAssertsRelDirection(false);
    term.setCurrent(true);
    term.setOrganizingClassType(IdType.CONCEPT);
    term.setPreferredName(getTerminology());
    term.setTimestamp(date);
    term.setLastModified(date);
    term.setLastModifiedBy(loader);
    term.setTerminology(getTerminology());
    term.setVersion(getVersion());
    term.setDescriptionLogicTerminology(false);
    term.setMetathesaurus(false);
    term.setRootTerminology(root);
    addTerminology(term);

    // Languages (ENG)
    final Language lat = new LanguageJpa();
    lat.setAbbreviation("en");
    lat.setExpandedForm("English");
    lat.setTimestamp(date);
    lat.setLastModified(date);
    lat.setLastModifiedBy(loader);
    lat.setTerminology(getTerminology());
    lat.setVersion(getVersion());
    lat.setPublished(true);
    lat.setPublishable(true);
    lat.setISO3Code("ENG");
    lat.setISOCode("en");
    addLanguage(lat);

    // Term types (PT, SY)
    TermType tty = new TermTypeJpa();
    tty.setAbbreviation("PT");
    tty.setExpandedForm("Preferred term");
    tty.setTimestamp(date);
    tty.setLastModified(date);
    tty.setLastModifiedBy(loader);
    tty.setTerminology(getTerminology());
    tty.setVersion(getVersion());
    tty.setPublished(true);
    tty.setPublishable(true);
    tty.setCodeVariantType(CodeVariantType.UNDEFINED);
    tty.setHierarchicalType(false);
    tty.setNameVariantType(NameVariantType.UNDEFINED);
    tty.setSuppressible(false);
    tty.setStyle(TermTypeStyle.UNDEFINED);
    tty.setUsageType(UsageType.UNDEFINED);
    addTermType(tty);

    tty = new TermTypeJpa(tty);
    tty.setId(null);
    tty.setAbbreviation("SY");
    tty.setExpandedForm("Synonym");
    addTermType(tty);

    // Precedence List PT, SY
    final PrecedenceList list = new PrecedenceListJpa();
    list.setTerminology(getTerminology());
    list.setVersion(getVersion());
    list.setLastModified(date);
    list.setTimestamp(date);
    list.setLastModifiedBy(loader);
    list.setName("Default precedence list");
    list.getPrecedence()
        .addKeyValuePair(new KeyValuePair(getTerminology(), "PT"));
    list.getPrecedence()
        .addKeyValuePair(new KeyValuePair(getTerminology(), "SY"));
    addPrecedenceList(list);

    commitClearBegin();
  }

  /**
   * Load the concepts.txt file
   *
   * @throws Exception the exception
   */
  private void loadAtoms() throws Exception {
    logInfo("  Insert atoms and concepts and semantic types");

    // Set up maps
    String line = null;

    // Map of temp ids to assigned ids
    Map<String, String> idMap = new HashMap<>();
    Map<String, Concept> idConceptMap = new HashMap<>();
    IdentifierAssignmentHandler idHandler =
        getIdentifierAssignmentHandler(getTerminology());

    int objectCt = 0;
    final PushBackReader reader =
        new PushBackReader(new FileReader(new File(getInputFile())));

    // read list of atoms
    while ((line = reader.readLine()) != null) {
     

      line = line.replace("\r", "");
      final String[] fields = FieldedStringTokenizer.split(line, ",");
      
      System.out.println("line: " + line);
      System.out.println("  fields: " + fields.length);

      // Field Description
      // 0 conceptid
      // 1 sty
      // 2 name
      // 3 term type

      final String idKey = keepFileIds ? fields[0] : idMap.get(fields[0]);
      Concept concept = idConceptMap.get(idKey);
      if (concept == null) {
        concept = new ConceptJpa();
        setCommonFields(concept);
        concept.setTerminologyId(
            keepFileIds ? fields[0] : idHandler.getTerminologyId(concept));
        concept.setWorkflowStatus(WorkflowStatus.PUBLISHED);
      }

      final Atom atom = new AtomJpa();
      setCommonFields(atom);
      atom.setWorkflowStatus(WorkflowStatus.PUBLISHED);
      atom.setName(fields[2]);
      atom.setTerminologyId("");
      atom.setTermType(fields[3].toUpperCase());
      atom.setLanguage("en");
      atom.setCodeId("");
      atom.setConceptId(concept.getTerminologyId());
      atom.setDescriptorId("");
      atom.setStringClassId("");
      atom.setLexicalClassId("");
      // Add atom
      addAtom(atom);
      concept.getAtoms().add(atom);
      concept.setName(atom.getName());

      // Add semantic type
      boolean styPresent = false;
      for (SemanticTypeComponent conceptSty : concept.getSemanticTypes()) {
        if (conceptSty.getSemanticType() == fields[1]) {
          styPresent = true;
        }
      }
      if (!styPresent) {
        final SemanticTypeComponent sty = new SemanticTypeComponentJpa();
        setCommonFields(sty);
        sty.setSemanticType(fields[1]);
        sty.setTerminologyId("");
        sty.setWorkflowStatus(WorkflowStatus.PUBLISHED);
        addSemanticTypeComponent(sty, concept);
        concept.getSemanticTypes().add(sty);
      }

      addConcept(concept);
 
      // commit periodically
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);

    }

    commitClearBegin();
    reader.close();
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
  public ValidationResult checkPreconditions() throws Exception {
    return new ValidationResultJpa();
  }

  /* see superclass */
  @Override
  public void checkProperties(Properties p) throws Exception {
    checkRequiredProperties(new String[] {
        "inputFile"
    }, p);
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {

    if (p.getProperty("inputDir") != null) {
      setInputPath(p.getProperty("inputDir"));
    }

    if (p.getProperty("inputDir") != null) {
      setInputPath(p.getProperty("inputDir"));
    }
  }

  /* see superclass */
  @Override
  public List<AlgorithmParameter> getParameters() throws Exception {
    final List<AlgorithmParameter> params = super.getParameters();
    AlgorithmParameter param = new AlgorithmParameterJpa("Input Dir",
        "inputDir", "Input RRF directory to load", "", 255,
        AlgorithmParameter.Type.DIRECTORY, "");
    params.add(param);
    return params;
  }

  /**
   * Sets the common fields.
   *
   * @param comp the common fields
   */
  private void setCommonFields(Component comp) {
    comp.setTimestamp(date);
    comp.setLastModified(date);
    comp.setLastModifiedBy(loader);
    comp.setObsolete(false);
    comp.setSuppressible(false);
    comp.setPublished(true);
    comp.setPublishable(true);
    comp.setTerminology(getTerminology());
    comp.setVersion(getVersion());
  }

  /**
   * Returns the comparator.
   *
   * @param sortColumns the sort columns
   * @return the comparator
   */
  @SuppressWarnings({
      "static-method", "unused"
  })
  private Comparator<String> getComparator(final int[] sortColumns) {
    return new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        String v1[] = s1.split("\\|");
        String v2[] = s2.split("\\|");
        for (final int sortColumn : sortColumns) {
          final int cmp = v1[sortColumn].compareTo(v2[sortColumn]);
          if (cmp != 0) {
            return cmp;
          }
        }
        return 0;
      }
    };
  }

  /* see superclass */
  @Override
  public String getFileVersion() throws Exception {
    return "";
  }

  public void setKeepFileIdsFlag(boolean keepFileIds) {
    this.keepFileIds = keepFileIds;
  }

}
