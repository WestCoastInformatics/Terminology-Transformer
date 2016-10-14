/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.jpa.infomodels.NdcHistoryModel;
import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesListModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.PropertyModel;
import com.wci.tt.jpa.infomodels.RxcuiModel;
import com.wci.tt.jpa.infomodels.RxcuiNdcHistoryModel;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Attribute;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.handlers.SearchHandler;

/**
 * NDC-RXNORM provider. Converts between NDCs, RXNORM concepts, and SPL Set ids,
 * providing history info and detailed properties info.
 */
public class NdcProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  /**
   * Instantiates an empty {@link NdcProvider}.
   *
   * @throws Exception the exception
   */
  public NdcProvider() throws Exception {

    // Configure input/output matchers

    // Input matcher needs to have a code, e.g. the caller should use this:
    // DataContext inputContext = new DataContextJpa();
    // inputContext.setType(DataContextType.CODE);
    // inputContext.setTerminology("NDC");
    DataContextMatcher inputMatcher = new DataContextMatcher();
    inputMatcher.configureContext(DataContextType.CODE, null, null, null, null,
        "NDC", null);
    inputMatcher.configureContext(DataContextType.CODE, null, null, null, null,
        "SPL", null);
    inputMatcher.configureContext(DataContextType.CODE, null, null, null, null,
        "RXNORM", null);
    DataContextMatcher outputMatcher = new DataContextMatcher();

    // Output matcher needs to have an NdcModel information model, e.g. the
    // caller should use this:
    // DataContext outputContext = new DataContextJpa();
    // outputContext.setType(DataContextType.INFO_MODEL);
    // inputContext.setInfoModelClass(NdcModel.class.getName());
    outputMatcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        NdcModel.class.getName(), null, null);
    outputMatcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        RxcuiModel.class.getName(), null, null);
    outputMatcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        NdcPropertiesModel.class.getName(), null, null);
    outputMatcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        NdcPropertiesListModel.class.getName(), null, null);
    addMatcher(inputMatcher, outputMatcher);

  }

  /**
   * Returns the name.
   *
   * @return the name
   */
  /* see superclass */
  @Override
  public String getName() {
    return "NDC Provider Handler";
  }

  /**
   * Identify.
   *
   * @param record the record
   * @return the list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(TransformRecord record)
    throws Exception {
    throw new UnsupportedOperationException();
  }

  /**
   * Process.
   *
   * @param record the record
   * @return the list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public List<ScoredResult> process(TransformRecord record) throws Exception {
    Logger.getLogger(getClass())
        .debug("  process - " + record.getInputString());

    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    final DataContext outputContext = record.getProviderOutputContext();
    boolean history = inputContext.getParameters() != null
        && inputContext.getParameters().get("history") != null
        && inputContext.getParameters().get("history").equals("true");
    // Validate input/output context
    validate(inputContext, outputContext);

    // Set up return value
    final List<ScoredResult> results = new ArrayList<ScoredResult>();

    // Handle NDC -> NdcModel lookup
    if (inputContext.getTerminology().equals("NDC")
        && outputContext.getInfoModelClass().equals(NdcModel.class.getName())) {

      // Attempt to find the RXNORM CUI (or CUIs) from the NDC code
      final NdcModel model =
          getNdcModel(inputString, record.getNormalizedResults(), history);
      if (model != null) {
        final ScoredResult result = new ScoredResultJpa();
        result.setValue(model.getModelValue());
        result.setScore(1);
        results.add(result);
        Logger.getLogger(getClass()).debug("    result = " + result.getValue());
      } else {
        return new ArrayList<ScoredResult>();
      }
    }

    // Handle RXNORM -> RxcuiModel lookup
    else if (inputContext.getTerminology().equals("RXNORM") && outputContext
        .getInfoModelClass().equals(RxcuiModel.class.getName())) {

      // Attempt to find the NDC codes from the RXNORM cui
      final RxcuiModel model =
          getRxcuiModel(inputString, record.getNormalizedResults(), history);
      if (model != null) {
        final ScoredResult result = new ScoredResultJpa();
        result.setValue(model.getModelValue());
        result.setScore(1);
        results.add(result);
        Logger.getLogger(getClass()).debug("    result = " + result.getValue());
      } else {
        return new ArrayList<ScoredResult>();
      }
    }

    // Handle NDC -> NdcPropertiesModel lookup
    else if (inputContext.getTerminology().equals("NDC") && outputContext
        .getInfoModelClass().equals(NdcPropertiesModel.class.getName())) {

      // Attempt to find the properties for the NDC code
      final NdcPropertiesModel model =
          getPropertiesModel(inputString, record.getNormalizedResults());
      if (model != null) {
        final ScoredResult result = new ScoredResultJpa();
        result.setValue(model.getModelValue());
        result.setScore(1);
        results.add(result);
        Logger.getLogger(getClass()).debug("    result = " + result.getValue());
      } else {
        return new ArrayList<ScoredResult>();
      }

    }

    // Handle SPL_SET_ID -> NdcPropertiesModelList lookup
    else if (inputContext.getTerminology().equals("SPL") && outputContext
        .getInfoModelClass().equals(NdcPropertiesListModel.class.getName())) {

      // Attempt to find the ndc properties models for the given splsetid
      final NdcPropertiesListModel model =
          getPropertiesListModel(inputString, record.getNormalizedResults());
      if (model != null) {
        final ScoredResult result = new ScoredResultJpa();
        result.setValue(model.getModelValue());
        result.setScore(1);
        results.add(result);
        Logger.getLogger(getClass()).debug("    result = " + result.getValue());
      } else {
        return new ArrayList<ScoredResult>();
      }

    }

    else {
      return new ArrayList<ScoredResult>();
    }

    return results;
  }

  /**
   * Returns the NDC model for an normalized NDC code lookup.
   *
   * @param ndc the input string
   * @param normalizedResults the normalized results
   * @param history the history
   * @return the model
   * @throws Exception the exception
   */
  private NdcModel getNdcModel(String ndc, List<ScoredResult> normalizedResults,
    boolean history) throws Exception {
    Logger.getLogger(getClass()).debug("Get NDC Model - " + ndc);

    final ContentServiceJpa service = new ContentServiceJpa();
    try {

      // Gather together original input string and normalized results
      final Set<String> inputStrings = new HashSet<>();
      if (normalizedResults.size() > 0) {
        for (final ScoredResult result : normalizedResults) {
          inputStrings.add(result.getValue());
        }

      } else {
        inputStrings.add(ndc);
      }
      if (inputStrings.size() != 1) {
        throw new Exception(
            "Unexpected number of input strings: " + inputStrings.size());
      }
      // Check all possible values of NDC
      final String query = inputStrings.iterator().next();
      Logger.getLogger(getClass()).debug("  ndc = " + query);

      // Determine the current RXNORM version
      final String rxnormLatestVersion =
          service.getTerminologyLatestVersion("RXNORM").getVersion();
      Logger.getLogger(getClass())
          .debug("  latest RXNORM version = " + rxnormLatestVersion);

      // Find all matching RXNORM/NDC atoms from all versions
      // (or just the latest version if we're not interested in history)
      final PfsParameter pfs = new PfsParameterJpa();
      final SearchHandler handler =
          service.getSearchHandler(ConfigUtility.ATOMCLASS);
      final int[] totalCt = new int[1];
      final List<ConceptJpa> list = handler.getQueryResults("RXNORM", null,
          Branch.ROOT, "atoms.termType:NDC AND atoms.name:" + query, null,
          ConceptJpa.class, pfs, totalCt, service.getEntityManager());

      // [ {version,ndc,ndcActive,rxcui,rxcuiActive}, ... ]
      final List<NdcRxcuiHistoryRecord> recordList = new ArrayList<>();

      // list will have each matching concept - e.g. from each version.
      if (list.size() > 0) {

        // Convert each search result into a record
        String splSetId = null;
        for (final Concept concept : list) {
          boolean foundActiveMatchingNdc = false;
          for (final Atom atom : concept.getAtoms()) {
            if (atom.getTerminology().equals("RXNORM")
                && atom.getTermType().equals("NDC") && !atom.isObsolete()
                && atom.getName().equals(ndc)) {
              if (atom.getVersion().equals(rxnormLatestVersion)) {
                splSetId = atom.getCodeId();
              }
              foundActiveMatchingNdc = true;
              break;
            }
          }

          final NdcRxcuiHistoryRecord record = new NdcRxcuiHistoryRecord();
          record.active = foundActiveMatchingNdc;
          record.rxcui = concept.getTerminologyId();
          record.version = concept.getVersion();

          recordList.add(record);
        }

        // Sort the record list (so most recent is at the top)
        Collections.sort(recordList);

        // Build the model
        final NdcModel model = new NdcModel();
        // Active if the latest entry is active and matches the latest RXNORM
        // version
        model.setActive(recordList.get(0).active
            && recordList.get(0).version.equals(rxnormLatestVersion));
        model.setNdc(query);
        if (splSetId == null || splSetId.isEmpty()) {
          model.setSplSetId(null);
        } else {
          model.setSplSetId(splSetId);
        }
        Concept concept = service.getConcept(recordList.get(0).rxcui, "RXNORM",
            rxnormLatestVersion, Branch.ROOT);
        model.setRxcuiName(concept != null ? concept.getName() : "");
        model.setRxcui(concept != null ? concept.getTerminologyId() : "");

        // Look up history if desired
        if (history) {
          // RXCUI VERSION ACTIVE
          // 12343 20160404 true
          // 12343 20160304 true
          // 43921 20160204 true
          //
          // History
          // {rxcui: 12343, start:20160304, end: 20160404
          // },
          // {rxcui: 43921, start:20160204, end: 20160204 }
          final List<NdcHistoryModel> historyModels = new ArrayList<>();
          NdcHistoryModel historyModel = new NdcHistoryModel();
          String prevRxcui = null;
          String prevVersion = null;
          for (final NdcRxcuiHistoryRecord record : recordList) {
            // handle first record
            if (prevRxcui == null) {
              historyModel.setRxcui(record.rxcui);
              historyModel.setEnd(record.version);
            }

            // when rxcui changes
            if (prevRxcui != null && !prevRxcui.equals(record.rxcui)) {
              historyModel.setStart(prevVersion);
              historyModels.add(historyModel);

              Logger.getLogger(getClass())
                  .debug("    history = " + historyModel);
              historyModel = new NdcHistoryModel();
              historyModel.setRxcui(record.rxcui);
              historyModel.setEnd(record.version);
            }

            prevRxcui = record.rxcui;
            prevVersion = record.version;
          }
          // Handle the final record, if there was a record
          if (recordList.size() > 0) {
            historyModel.setStart(prevVersion);
            historyModels.add(historyModel);
          }
          Logger.getLogger(getClass()).debug("    history = " + historyModel);

          model.setHistory(historyModels);

        }
        Logger.getLogger(getClass()).debug("  model = " + model);
        return model;

      }

      // otherwise, empty list
      else {
        return new NdcModel();
      }

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
  }

  /**
   * Returns the rxcui model.
   *
   * @param rxcui the input string
   * @param normalizedResults the normalized results
   * @param history the history
   * @return the rxcui model
   * @throws Exception the exception
   */
  private RxcuiModel getRxcuiModel(String rxcui,
    List<ScoredResult> normalizedResults, boolean history) throws Exception {
    Logger.getLogger(getClass()).debug("Get RXCUI Model - " + rxcui);

    final ContentServiceJpa service = new ContentServiceJpa();
    try {

      // Determine the current RXNORM version
      final String rxnormLatestVersion =
          service.getTerminologyLatestVersion("RXNORM").getVersion();
      Logger.getLogger(getClass())
          .debug("  latest RXNORM version = " + rxnormLatestVersion);

      // try to find RXCUI for all versions (or just latest version if
      // not interested in history)
      final PfsParameter pfs = new PfsParameterJpa();
      // rxcui -> ndc
      final SearchHandler handler =
          service.getSearchHandler(ConfigUtility.ATOMCLASS);
      final int[] totalCt = new int[1];
      final List<ConceptJpa> list = handler.getQueryResults("RXNORM", null,
          Branch.ROOT, "terminologyId:" + rxcui, null, ConceptJpa.class, pfs,
          totalCt, service.getEntityManager());

      // [ {version,ndc,ndcActive,rxcui,rxcuiActive}, ... ]
      final List<RxcuiNdcHistoryRecord> recordList = new ArrayList<>();

      // list will have each matching concept - e.g. from each version.
      if (list.size() > 0) {
        // Convert each search result into a record
        String maxVersion = "000000";
        String rxcuiName = null;
        final Set<String> splSetIds = new HashSet<>();
        for (final Concept concept : list) {
          // get the most recent rxcui name
          if (concept.getVersion().compareTo(maxVersion) > 0) {
            maxVersion = concept.getVersion();
            rxcuiName = concept.getName();
          }
          for (Atom atom : concept.getAtoms()) {
            if (atom.getTerminology().equals("RXNORM")
                && atom.getTermType().equals("NDC") && !atom.isObsolete()) {
              final RxcuiNdcHistoryRecord record = new RxcuiNdcHistoryRecord();
              record.ndc = atom.getName();
              record.version = concept.getVersion();
              recordList.add(record);

              // Keep only latest version SPL_SET_IDs
              if (!atom.getCodeId().equals("")
                  && atom.getVersion().equals(rxnormLatestVersion)) {
                splSetIds.add(atom.getCodeId());
              }
            }
          }

        }

        // Sort the record list (so most recent is at the top)
        Collections.sort(recordList);

        final RxcuiModel model = new RxcuiModel();
        // Determine if latest version of RXCUI is active or not
        final Concept concept = service.getConcept(rxcui, "RXNORM",
            rxnormLatestVersion, Branch.ROOT);
        model.setActive(concept != null && !concept.isObsolete());
        model.setRxcui(rxcui);
        model.setRxcuiName(rxcuiName);
        if (concept != null) {
          // leave this blank if the rxcui is obsolete
          model.setSplSetIds(new ArrayList<>(splSetIds));
        }

        if (history) {
          // NDC VERSION ACTIVE
          // 12343 20160404 true
          // 12343 20160304 true
          // 43921 20160204 true
          //
          // History
          // {ndc: 12343, start:20160304, end: 20160404
          // },
          // {ndc: 43921, start:20160204, end: 20160204 }

          List<RxcuiNdcHistoryModel> historyModels = new ArrayList<>();
          RxcuiNdcHistoryModel historyModel = new RxcuiNdcHistoryModel();
          String prevNdc = null;
          String prevVersion = null;
          for (RxcuiNdcHistoryRecord record : recordList) {
            // handle first record
            if (prevNdc == null) {
              historyModel.setNdc(record.ndc);
              historyModel.setEnd(record.version);
            }

            // when ndc changes
            if (prevNdc != null && !prevNdc.equals(record.ndc)) {
              historyModel.setStart(prevVersion);
              historyModels.add(historyModel);

              Logger.getLogger(getClass())
                  .debug("    history = " + historyModel);
              historyModel = new RxcuiNdcHistoryModel();
              historyModel.setNdc(record.ndc);
              historyModel.setEnd(record.version);
            }

            prevNdc = record.ndc;
            prevVersion = record.version;
          }
          // Handle the final record if there was at least one record
          if (recordList.size() > 0) {
            historyModel.setStart(prevVersion);
            historyModels.add(historyModel);
          }
          Logger.getLogger(getClass()).debug("    history = " + historyModel);

          model.setHistory(historyModels);
        }

        Logger.getLogger(getClass()).debug("  model = " + model);
        return model;

      }

      // Otherwise, list is empty
      else {
        return new RxcuiModel();
      }

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
  }

  /**
   * Returns the ndc properties model for the specified NDC code.
   *
   * @param ndc the input string
   * @param normalizedResults the normalized results
   * @return the ndc properties model
   * @throws Exception the exception
   */
  private NdcPropertiesModel getPropertiesModel(String ndc,
    List<ScoredResult> normalizedResults) throws Exception {
    Logger.getLogger(getClass()).debug("Get NDC Properties Model - " + ndc);
    final ContentServiceJpa service = new ContentServiceJpa();
    try {

      // gather together original input string and normalized results
      final Set<String> inputStrings = new HashSet<>();
      if (normalizedResults.size() > 0) {
        for (final ScoredResult result : normalizedResults) {
          inputStrings.add(result.getValue());
        }
      } else {
        inputStrings.add(ndc);
      }
      if (inputStrings.size() != 1) {
        throw new Exception(
            "Unexpected number of input strings: " + inputStrings.size());
      }
      // Check all possible values of NDC
      final String query = inputStrings.iterator().next();
      Logger.getLogger(getClass()).debug("  ndc = " + ndc);

      // try to find NDC based on inputString
      final SearchHandler handler =
          service.getSearchHandler(ConfigUtility.ATOMCLASS);
      final int[] totalCt = new int[1];
      final List<ConceptJpa> list = handler.getQueryResults("RXNORM",
          service.getTerminologyLatestVersion("RXNORM").getVersion(),
          Branch.ROOT, "atoms.termType:NDC AND atoms.name:" + query, null,
          ConceptJpa.class, null, totalCt, service.getEntityManager());

      // Should be a single matching concept
      if (list.size() == 1) {

        // RXNORM concept returned, there should be only one.
        final Concept concept = list.get(0);
        final NdcPropertiesModel model = new NdcPropertiesModel();
        model.setRxcui(concept.getTerminologyId());
        model.setRxcuiName(concept.getName());
        model.setNdc11(query);

        String ndc9 = null;
        String ndc10 = null;
        for (final Atom atom : concept.getAtoms()) {

          if (atom.getTerminology().equals("RXNORM")
              && atom.getTermType().equals("NDC") && !atom.isObsolete()
              && atom.getName().equals(query)) {
            // Get the SPL_SET_ID from the code
            if (atom.getCodeId().isEmpty()) {
              model.setSplSetId(null);
            } else {
              model.setSplSetId(atom.getCodeId());
            }
            // Now, look up NDC properties
            final List<PropertyModel> properties =
                new ArrayList<PropertyModel>();
            for (final Attribute attrib : atom.getAttributes()) {

              // Handle NDC9
              if (attrib.getName().equals("NDC9")) {
                ndc9 = attrib.getValue();
              }
              // Handle NDC10
              else if (attrib.getName().equals("NDC10")) {
                ndc10 = attrib.getValue();
              }
              // Otherwise, add a property
              else {
                final PropertyModel prop = new PropertyModel();
                prop.setProp(attrib.getName());
                prop.setValue(attrib.getValue());
                properties.add(prop);
              }
            }
            model.setPropertyList(properties);

          }
        }

        model.setNdc9(ndc9);
        model.setNdc10(ndc10);

        Logger.getLogger(getClass()).debug("  model = " + model);
        return model;
      }

      Logger.getLogger(getClass())
          .debug("  model = " + new NdcPropertiesModel());
      return new NdcPropertiesModel();

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }

  }

  /**
   * Returns the properties model list.
   *
   * @param splsetid the splsetid
   * @param normalizedResults the normalized results
   * @return the properties model list
   * @throws Exception the exception
   */
  private NdcPropertiesListModel getPropertiesListModel(String splsetid,
    List<ScoredResult> normalizedResults) throws Exception {

    final ContentServiceJpa service = new ContentServiceJpa();

    try {
      // Look only in latest RXNORM version for NDCs with a matching codeId
      final SearchHandler handler =
          service.getSearchHandler(ConfigUtility.ATOMCLASS);
      final int[] totalCt = new int[1];
      final List<ConceptJpa> list = handler.getQueryResults("RXNORM",
          service.getTerminologyLatestVersion("RXNORM").getVersion(),
          Branch.ROOT, "atoms.termType:NDC AND atoms.codeId:" + splsetid, null,
          ConceptJpa.class, null, totalCt, service.getEntityManager());

      // list will have each matching concept - e.g. from each version.
      if (list.size() > 0) {
        final NdcPropertiesListModel model = new NdcPropertiesListModel();

        // Gather results
        for (final Concept concept : list) {

          for (final Atom atom : concept.getAtoms()) {
            if (atom.getTerminology().equals("RXNORM")
                && atom.getTermType().equals("NDC") && !atom.isObsolete()
                && atom.getCodeId().equals(splsetid)) {

              // For each NDC code, get the model
              final NdcPropertiesModel resultModel =
                  getPropertiesModel(atom.getName(), new ArrayList<>());
              if (resultModel.getRxcui() == null
                  || resultModel.getRxcui().isEmpty()) {
                throw new Exception(
                    "Unexpectedly unable to find entry for NDC - "
                        + atom.getName());
              }
              model.getList().add(resultModel);
            }
          }

        }
        Logger.getLogger(getClass()).debug("  model = " + model);
        return model;
      }

      // else nothing found
      else {
        return new NdcPropertiesListModel();
      }

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }

  }

  /**
   * Sets the properties.
   *
   * @param p the properties
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    super.setProperties(p);
  }

  /**
   * Adds the feedback.
   *
   * @param inputString the input string
   * @param context the context
   * @param feedbackString the feedback string
   * @param outputContext the output context
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void addFeedback(String inputString, DataContext context,
    String feedbackString, DataContext outputContext) throws Exception {
    // n/a
  }

  /**
   * Removes the feedback.
   *
   * @param inputString the input string
   * @param context the context
   * @param outputContext the output context
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void removeFeedback(String inputString, DataContext context,
    DataContext outputContext) throws Exception {
    // n/a
  }

  /**
   * Returns the log base value.
   *
   * @return the log base value
   */
  /* see superclass */
  @Override
  public float getLogBaseValue() {
    return 0;
  }

  /**
   * Close.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }

  /**
   * Indicates whether or not pre check valid is the case.
   *
   * @param record the record
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  /* see superclass */
  @Override
  public boolean isPreCheckValid(TransformRecord record) {
    // Initial setup until specific rules defined
    return true;
  }

  /**
   * Filter results.
   *
   * @param providerEvidenceMap the provider evidence map
   * @param record the record
   * @return the map
   */
  /* see superclass */
  @Override
  public Map<String, Float> filterResults(
    Map<String, Float> providerEvidenceMap, TransformRecord record) {
    // Initial setup until specific rules defined
    return providerEvidenceMap;
  }

  //
  // Helper classes
  //

  /**
   * Represents a record from which to derive RXCUI history for an NDC.
   */
  private class NdcRxcuiHistoryRecord
      implements Comparable<NdcRxcuiHistoryRecord> {

    /** The version. */
    public String version;

    /** The rxcui. */
    public String rxcui;

    /** The active. */
    public boolean active;

    /**
     * Instantiates an empty {@link NdcRxcuiHistoryRecord}.
     */
    public NdcRxcuiHistoryRecord() {
      // n/a
    }

    /**
     * Compare to.
     *
     * @param o the o
     * @return the int
     */
    @Override
    public int compareTo(NdcRxcuiHistoryRecord o) {
      return o.version.compareTo(version);
    }
  }

  /**
   * Represents a record from which to derive NDC history for an RXCUI.
   */
  private class RxcuiNdcHistoryRecord
      implements Comparable<RxcuiNdcHistoryRecord> {

    /** The version. */
    public String version;

    /** The ndc. */
    public String ndc;

    /**
     * Instantiates an empty {@link RxcuiNdcHistoryRecord}.
     */
    public RxcuiNdcHistoryRecord() {
      // n/a
    }

    /**
     * Compare to.
     *
     * @param o the o
     * @return the int
     */
    @Override
    public int compareTo(RxcuiNdcHistoryRecord o) {
      return (o.ndc + o.version).compareTo(ndc + version);
    }
  }

}
