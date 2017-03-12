/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.algo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.wci.umls.server.AlgorithmParameter;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.KeyValuePair;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.PrecedenceList;
import com.wci.umls.server.helpers.content.ConceptList;
import com.wci.umls.server.jpa.AlgorithmParameterJpa;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.algo.AbstractTerminologyLoaderAlgorithm;
import com.wci.umls.server.jpa.content.AtomJpa;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.content.SemanticTypeComponentJpa;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.PrecedenceListJpa;
import com.wci.umls.server.jpa.meta.GeneralMetadataEntryJpa;
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
import com.wci.umls.server.model.meta.GeneralMetadataEntry;
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
import com.wci.umls.server.services.handlers.ComputePreferredNameHandler;
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

  /** The branch. */
  private String branch = null;

  /** The concepts file name. */
  private String inputFile = null;

  /** The workflow status fo rnew concepts. */
  private WorkflowStatus workflowStatus = null;

  /** The input file stream. */
  private InputStream inputStream = null;

  /** Whether to keep file ids or compute new ids . */
  private boolean keepFileIds = false;

  private ValidationResult validationResult = null;

  /**
   * Instantiates an empty {@link TerminologySimpleCsvLoaderAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public TerminologySimpleCsvLoaderAlgorithm() throws Exception {
    super();
  }

  public void setInputFile(String inputFile) {
    this.inputFile = inputFile;
  }

  public String getInputFile() {
    return this.inputFile;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public InputStream getInputStream() {
    return this.inputStream;
  }

  public void setWorkflowStatus(WorkflowStatus status) {
    this.workflowStatus = status;
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

    final ValidationResult precheck = checkPreconditions();
    if (!precheck.isValid()) {
      Logger.getLogger(getClass()).error("Compute failed preconditions:");
      for (String error : precheck.getErrors()) {
        Logger.getLogger(getClass()).error("  " + error);
      }
      throw new Exception("Failed preconditions");
    }

    validationResult = new ValidationResultJpa();

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

    branch = getProject() == null || getProject().getBranch() == null
        ? Branch.ROOT : getProject().getBranch();

    // Check the input directory XOR input stream
    if (getInputFile() == null && inputStream == null) {
      throw new Exception("Neither input file nor input stream specified");
    } else if (getInputFile() != null) {
      File inputDirFile = new File(getInputFile());
      if (inputStream == null && !inputDirFile.exists()) {
        throw new Exception("Specified input file does not exist");
      }
    }

    // faster performance.
    beginTransaction();

    // Semantic type, termTypes, languages, PAR/CHD rel types, prec list, etc.
    loadMetadata();

    // Assume files concepts.txt, parChd.txt
    loadAtoms();

    // Commit
    commitClearBegin();

    final Terminology terminology =
        getTerminologyLatestVersion(getTerminology());

    // Clear concept cache

    logInfo("Log component stats");
    final Map<String, Integer> stats = getComponentStats(
        terminology.getTerminology(), terminology.getVersion(), branch);
    final List<String> statsList = new ArrayList<>(stats.keySet());
    Collections.sort(statsList);
    for (final String key : statsList) {
      logInfo("  " + key + " = " + stats.get(key));
    }

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

    // NOTE: Semantic Type computation moved to loadAtoms() as inputStream can
    // only be
    // read once, and typical implementations do not support mark/reset

    // if terminology exists, skip
    if (getTerminology(getTerminology(), getVersion()) == null) {

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
      lat.setBranch(branch);
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
      tty.setBranch(branch);
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
      list.setBranch(branch);
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

      String[] labels = new String[] {
          "Atoms_Label", "Semantic_Types_Label"
      };
      String[] labelValues = new String[] {
          "Terms", "Features"
      };
      int i = 0;
      for (final String label : labels) {
        final GeneralMetadataEntry entry = new GeneralMetadataEntryJpa();
        entry.setTerminology(getTerminology());
        entry.setVersion(getVersion());
        entry.setLastModified(date);
        entry.setLastModifiedBy(loader);
        entry.setPublishable(true);
        entry.setPublished(true);
        entry.setAbbreviation(label);
        entry.setExpandedForm(labelValues[i++]);
        entry.setKey("label_metadata");
        entry.setType("label_values");
        addGeneralMetadataEntry(entry);
      }
      commitClearBegin();

      commitClearBegin();
    }
  }

  /**
   * Load the concepts.txt file
   *
   * @throws Exception the exception
   */
  private void loadAtoms() throws Exception {
    logInfo("  Insert atoms and concepts and semantic types");

    IdentifierAssignmentHandler idHandler =
        getIdentifierAssignmentHandler(getTerminology());

    // check for new semantic types (or add all if none exist)
    final Set<String> existingStys = new HashSet<>();
    for (SemanticType type : getSemanticTypes(getTerminology(), getVersion())
        .getObjects()) {
      existingStys.add(type.getExpandedForm());
    }
    int styCt = 0;
    int conceptCt = 0;
    int atomsCt = 0;
    int skipCt = 0;

    Concept concept = null;
    String lastConceptId = null;

    ComputePreferredNameHandler pnHandler =
        getComputePreferredNameHandler(getTerminology());
    PrecedenceList precedenceList =
        getPrecedenceList(getTerminology(), getVersion());

    Logger.getLogger(getClass())
        .info("Identifier handler: " + idHandler.getName());
    Logger.getLogger(getClass())
        .info("Compute preferred name handler: " + pnHandler.getName());

    final PushBackReader reader = inputStream != null
        ? new PushBackReader(new InputStreamReader(inputStream, "UTF-8"))
        : new PushBackReader(new FileReader(new File(inputFile)));
    Iterable<CSVRecord> parser = CSVFormat.DEFAULT.parse(reader);
    Iterator<CSVRecord> iterator = parser.iterator();
    CSVRecord record = iterator.next();

    // get term types for validation
    List<String> termTypes = new ArrayList<>();
    for (TermType tty : getTermTypes(getTerminology(), getVersion())
        .getObjects()) {
      termTypes.add(tty.getAbbreviation());
    }

    // skip header line
    if (record.get(0).equals("terminologyId")) {
      record = iterator.next();
    }
    do {

      boolean skip = false;

      // Field Description
      // 0 conceptid
      // 1 sty
      // 2 name
      // 3 term type

      // check for completeness
      if (record.size() != 5) {
        validationResult.addError("Line " + record.getRecordNumber()
            + ": Expected 4 fields but found " + record.size());
        skip = true;
      }
      if (record.get(0) == null || record.get(0).isEmpty()) {
        validationResult.addError(
            "Line " + record.getRecordNumber() + ": Terminology id blank");
        skip = true;
      }
      if (record.get(1) == null || record.get(1).isEmpty()) {
        validationResult.addError(
            "Line " + record.getRecordNumber() + ": Source data blank");
        skip = true;
      }
      if (record.get(2) == null || record.get(2).isEmpty()) {
        validationResult
            .addError("Line " + record.getRecordNumber() + ": Feature blank");
        skip = true;
      }
      if (record.get(3) == null || record.get(3).isEmpty()) {
        validationResult
            .addError("Line " + record.getRecordNumber() + ": Name blank");
        skip = true;
      }
      if (record.get(4) == null || record.get(4).isEmpty()) {
        validationResult
            .addError("Line " + record.getRecordNumber() + ": Term type blank");
        skip = true;
      }
      if (!termTypes.contains(record.get(4).toUpperCase())) {
        validationResult.addError(
            "Line " + record.getRecordNumber() + ": Term type invalid: " + record.get(4));
        skip = true;
      }

      if (!skip) {

        if (!record.get(0).trim().equals(lastConceptId)) {

          if (concept != null) {

            concept.setName(pnHandler.computePreferredName(concept.getAtoms(),
                precedenceList));
            updateConcept(concept);
            conceptCt++;

            // commit periodically
            logAndCommit(conceptCt, RootService.logCt, RootService.commitCt);

          }

          // create and add next concept
          concept = new ConceptJpa();
          setCommonFields(concept);
          concept.setTerminologyId(keepFileIds ? record.get(0).trim()
              : idHandler.getTerminologyId(concept));
          concept.setWorkflowStatus(workflowStatus == null
              ? WorkflowStatus.PUBLISHED : workflowStatus);
          concept.setName("TBD");
          concept = addConcept(concept);

          // last concept id
          lastConceptId = record.get(0).trim();
        }

        Atom atom = new AtomJpa();
        setCommonFields(atom);
        atom.setWorkflowStatus(WorkflowStatus.PUBLISHED);
        atom.setName(record.get(3).trim());
        atom.setTerminologyId(idHandler.getTerminologyId(atom));
        atom.setTermType(record.get(4).trim().toUpperCase());
        atom.setLanguage("en");
        atom.setCodeId("");
        atom.setConceptId(concept.getTerminologyId());
        atom.setDescriptorId("");
        atom.setStringClassId("");
        atom.setLexicalClassId("");

        // put source data in lastModifiedBy
        atom.setLastModifiedBy(record.get(1).trim());

        // Add atom
        atom = addAtom(atom);
        concept.getAtoms().add(atom);
        updateConcept(concept);
        atomsCt++;

        // add semantic type if does not exist
        if (!existingStys.contains(record.get(2).trim())) {
          final SemanticType sty = new SemanticTypeJpa();
          sty.setAbbreviation(record.get(2).trim());
          sty.setBranch(branch);
          sty.setDefinition("");
          sty.setExample("");
          sty.setExpandedForm(record.get(2).trim());
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

          addSemanticType(sty);
          styCt++;
          existingStys.add(sty.getExpandedForm());
        }

        // Add semantic type
        boolean styPresent = false;
        for (SemanticTypeComponent conceptSty : concept.getSemanticTypes()) {
          if (conceptSty.getSemanticType().equals(record.get(2))) {
            styPresent = true;
          }
        }
        if (!styPresent) {

          final SemanticTypeComponent sty = new SemanticTypeComponentJpa();
          setCommonFields(sty);
          sty.setSemanticType(record.get(2));
          sty.setTerminologyId("");
          sty.setWorkflowStatus(WorkflowStatus.PUBLISHED);
          addSemanticTypeComponent(sty, concept);
          concept.getSemanticTypes().add(sty);

        }

        // update concept changes (atoms, stys)
        updateConcept(concept);
      } else {
        skipCt++;
      }

      // cycle while still records and the next record is not blank (proxy for
      // EOF)
    } while (iterator.hasNext() && (record = iterator.next()) != null);

    // fail gracefully on blank end lines
    if (concept != null) {
      // update and commit last concept
      concept.setName(
          pnHandler.computePreferredName(concept.getAtoms(), precedenceList));
      updateConcept(concept);
      conceptCt++;
    }

    if (skipCt > 0) {
      validationResult.getWarnings().add("Skipped " + skipCt + " + terms");
    }

    commitClearBegin();

    validationResult.getComments().add("Added " + atomsCt + " terms");
    validationResult.getComments().add("Added " + conceptCt + " concepts");
    if (styCt > 0) {
      validationResult.getComments().add("Added " + styCt + " features");
    }
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
   * @param time t he time
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
    ValidationResult result = new ValidationResultJpa();
    if (inputStream == null && inputFile == null) {
      result.getErrors().add("Neither input file nor input stream set");
    }
    if (inputStream != null && inputFile != null) {
      result.getErrors().add("Both input file and input stream set");
    }
    return result;
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

  // wraps quotes around text containing commas
  private String getCsvField(String field) {
    if (field.contains(",")) {
      return "\"" + field + "\"";
    }
    return field;
  }

  public ByteArrayInputStream export(String terminology, String version,
    String branch, boolean acceptNew, boolean readyOnly) throws Exception {

    setMolecularActionFlag(false);
    setLastModifiedFlag(true);

    if (acceptNew) {
      final ConceptList concepts = findConcepts(terminology, version,
          branch == null ? Branch.ROOT : branch, "workflowStatus:NEW", null);
      if (concepts.getTotalCount() > 0) {
        setTransactionPerOperation(false);
        beginTransaction();
        for (Concept concept : concepts.getObjects()) {
          concept.setWorkflowStatus(WorkflowStatus.PUBLISHED);
          updateConcept(concept);
        }
        commit();
      }
    }

    // Write a header
    // Obtain members for refset,
    // Write RF2 simple refset pattern to a StringBuilder
    // wrap and return the string for that as an input stream
    StringBuilder sb = new StringBuilder();
    sb.append("terminologyId").append(",");
    sb.append("feature").append(",");
    sb.append("name").append(",");
    sb.append("termType").append("\r\n");

    // sort by key
    PfsParameter pfs = new PfsParameterJpa();
    pfs.setSortField("key");

    ConceptList concepts =
        findConcepts(terminology, version, Branch.ROOT, null, null);
    for (Concept concept : concepts.getObjects()) {
      if (!readyOnly
          || !WorkflowStatus.NEEDS_REVIEW.equals(concept.getWorkflowStatus())) {
        for (Atom atom : concept.getAtoms()) {
          sb.append(getCsvField(concept.getTerminologyId())).append(",");
          sb.append(concept.getSemanticTypes() != null
              && concept.getSemanticTypes().size() > 0
                  ? getCsvField(
                      concept.getSemanticTypes().get(0).getSemanticType())
                  : "")
              .append(",");
          sb.append(getCsvField(atom.getName())).append(",");
          sb.append(getCsvField(atom.getTermType())).append("\r\n");
        }
      }
    }
    return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
  }

  public ValidationResult getValidationResult() {
    return validationResult;
  }

}
