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
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesListModel;
import com.wci.tt.jpa.infomodels.PropertyModel;
import com.wci.tt.jpa.infomodels.RxcuiHistoryModel;
import com.wci.tt.jpa.infomodels.RxcuiModel;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.PfscParameter;
import com.wci.umls.server.helpers.SearchCriteria;
import com.wci.umls.server.helpers.SearchResult;
import com.wci.umls.server.helpers.SearchResultList;
import com.wci.umls.server.jpa.helpers.PfscParameterJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Attribute;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;

/**
 * Default implementation of {@link ProviderHandler}.
 * 
 * This provider converts a normalized NDC code into an RXNORM code (with
 * history information).
 * 
 */
public class NdcProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  private TransformRecord transformRecord;
  
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
        NdcPropertiesModelList.class.getName(), null, null);
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

    transformRecord = record;
    final String inputString = transformRecord.getInputString();
    final DataContext inputContext = transformRecord.getInputContext();
    final DataContext outputContext = transformRecord.getProviderOutputContext();

    // Validate input/output context
    validate(inputContext, outputContext);

    // Set up return value
    final List<ScoredResult> results = new ArrayList<ScoredResult>();

    if (inputContext.getTerminology().equals("NDC")
        && outputContext.getInfoModelClass().equals(NdcModel.class.getName())) {

      // Attempt to find the RXNORM CUI (or CUIs) from the NDC code
      final NdcModel model =
          getNdcModel(inputString, record.getNormalizedResults());
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

    else if (inputContext.getTerminology().equals("RXNORM") && outputContext
        .getInfoModelClass().equals(RxcuiModel.class.getName())) {

      // Attempt to find the NDC codes from the RXNORM cui
      final RxcuiModel model =
          getRxcuiModel(inputString, record.getNormalizedResults());
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

    else if (inputContext.getTerminology().equals("NDC") && outputContext
        .getInfoModelClass().equals(NdcPropertiesListModel.class.getName())) {
      
      // Attempt to find the ndc properties models for the given splsetid
      final NdcPropertiesListModel model =
          getPropertiesModelList(inputString, record.getNormalizedResults());
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
   * The Class Record.
   */
  private class NdcRecord implements Comparable<NdcRecord> {

    /** The version. */
    public String version;

    /** The ndc active. */
    public boolean ndcActive;

    /** The rxcui. */
    public String rxcui;


    /**
     * Compare to.
     *
     * @param o the o
     * @return the int
     */
    @Override
    public int compareTo(NdcRecord o) {
      return o.version.compareTo(version);
    }
  }

  private class RxcuiRecord implements Comparable<RxcuiRecord> {

    /** The version. */
    public String version;

    /** The ndc. */
    public String ndc;

    /**
     * Compare to.
     *
     * @param o the o
     * @return the int
     */
    @Override
    public int compareTo(RxcuiRecord o) {
      return (o.ndc + o.version).compareTo(ndc + version);
    }
  }

  private RxcuiModel getRxcuiModel(String inputString,
    List<ScoredResult> normalizedResults) throws Exception {
    final ContentService service = new ContentServiceJpa();

    try {

      // try to find NDC based on inputString
      PfscParameter pfsc = new PfscParameterJpa();
      pfsc.setSearchCriteria(new ArrayList<SearchCriteria>());
      // rxcui -> ndc
      SearchResultList list =
          service.findConceptsForQuery("RXNORM", null, Branch.ROOT,
              "terminology:RXNORM AND terminologyId:" + inputString, pfsc);

      // [ {version,ndc,ndcActive,rxcui,rxcuiActive}, ... ]
      List<RxcuiRecord> recordList = new ArrayList<>();

      // list will have each matching concept - e.g. from each version.
      if (list.getCount() > 0) {
        // Convert each search result into a record
        for (final SearchResult result : list.getObjects()) {
          final Concept concept = service.getConcept(result.getId());

          for (Atom atom : concept.getAtoms()) {
            if (atom.getTermType().equals("NDC")) {
              final RxcuiRecord record = new RxcuiRecord();
              record.ndc = atom.getName();
              record.version = result.getVersion();
              recordList.add(record);
            }
          }

        }

        // Sort the record list (so most recent is at the top)
        Collections.sort(recordList);

        final RxcuiModel model = new RxcuiModel();
        model.setRxcui(inputString);

        // NDC VERSION ACTIVE
        // 12343 20160404 true
        // 12343 20160304 true
        // 43921 20160204 true
        //
        // History
        // {ndc: 12343, startDate:20160304, endDate: 20160404
        // },
        // {ndc: 43921, startDate:20160204, endDate: 20160204 }

        List<RxcuiHistoryModel> historyModels = new ArrayList<>();
        RxcuiHistoryModel historyModel = new RxcuiHistoryModel();
        String prevNdc = null;
        String prevVersion = null;
        for (RxcuiRecord record : recordList) {
          // handle first record
          if (prevNdc == null) {
            historyModel.setNdc(record.ndc);
            historyModel.setEndDate(record.version);
          }

          // when ndc changes
          if (prevNdc != null && !prevNdc.equals(record.ndc)) {
            if (historyModel != null) {
              historyModel.setStartDate(prevVersion);
              historyModels.add(historyModel);
            }
            historyModel = new RxcuiHistoryModel();
            historyModel.setNdc(record.ndc);
            historyModel.setEndDate(record.version);
          }

          prevNdc = record.ndc;
          prevVersion = record.version;
        }
        // Handle the final record
        historyModel.setStartDate(prevVersion);
        historyModels.add(historyModel);

        model.setHistory(historyModels);
        return model;

      } // if list.getCount() >1

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
    return null;
  }

  private NdcPropertiesListModel getPropertiesModelList(String splsetid,
    List<ScoredResult> normalizedResults) throws Exception {
    final NdcPropertiesListModel model = new NdcPropertiesListModel();
    // TODO:  need to get Ndcs for splsetid and call getPropertiesModel on each ndc
    
    final ContentService service = new ContentServiceJpa();

    try {

      

        // try to find NDC based on inputString
        PfscParameter pfsc = new PfscParameterJpa();
        pfsc.setSearchCriteria(new ArrayList<SearchCriteria>());
        SearchResultList list = service.findConceptsForQuery("RXNORM", null,
            Branch.ROOT, "atoms.termType:NDC AND atoms.attributes.value:" + splsetid, pfsc);

        // [ {version,ndc,ndcActive,rxcui,rxcuiActive}, ... ]
        List<NdcRecord> recordList = new ArrayList<>();

        // list will have each matching concept - e.g. from each version.
        if (list.getCount() > 0) {
          // Convert each search result into a record
          for (final SearchResult result : list.getObjects()) {
            final Concept concept = service.getConcept(result.getId());
          }
        }
    } catch (Exception e) {
      
    }
    return model;
  }
  
  /**
   * Returns the ndc properties model.
   *
   * @param inputString the input string
   * @param normalizedResults the normalized results
   * @return the ndc properties model
   * @throws Exception the exception
   */
  private NdcPropertiesModel getPropertiesModel(String inputString,
    List<ScoredResult> normalizedResults) throws Exception {
    final ContentService service = new ContentServiceJpa();
    final NdcPropertiesModel model = new NdcPropertiesModel();
    

    try {

      // gather together original input string and normalized results
      Set<String> inputStrings = new HashSet<>();
      for (final ScoredResult result : normalizedResults) {
        inputStrings.add(result.getValue());
      }
      inputStrings.add(inputString);

      // Check all possible values of NDC
      for (final String query : inputStrings) {

        // try to find NDC based on inputString
        PfscParameter pfsc = new PfscParameterJpa();
        pfsc.setSearchCriteria(new ArrayList<SearchCriteria>());
        SearchResultList list = service.findConceptsForQuery("RXNORM", null,
            Branch.ROOT, "atoms.termType:NDC AND atoms.name:" + query, pfsc);

        
        // list will have each matching concept - e.g. from each version.
        if (list.getCount() > 0) {
          for (final SearchResult result : list.getObjects()) {
            final Concept concept = service.getConcept(result.getId());
            for (final Atom atom : concept.getAtoms()) {
              // if MTHSPL
              if (atom.getTerminology().equals("MTHSPL") 
                  && atom.getAttributeByName("NDC") != null) {
                // Take the value of the MTHSPL NDC attribute and 
                // run it through the NDC normalizer function 
                Attribute att = atom.getAttributeByName("NDC");
                String atv = att.getValue();
                NdcNormalizer normalizer = new NdcNormalizer();
                List<ScoredResult> results = normalizer.normalize(atv, transformRecord.getInputContext());
                for (ScoredResult normalizedAtv : results) {
                  if (normalizedAtv.getValue().equals(query)) {
                    List<PropertyModel> propertyModel =
                        new ArrayList<PropertyModel>();
                    for (Attribute attrib : atom.getAttributes()) {
                      if (attrib.getName().equals("SPL_SET_ID")) {
                        model.setSplSetId(attrib.getValue());
                      } else {
                        PropertyModel prop = new PropertyModel();
                        prop.setProp(attrib.getName());
                        prop.setValue(attrib.getValue());
                        propertyModel.add(prop);
                      }
                    }
                    model.setNdc9(getNdc9(query));
                    model.setNdc10(getNdc10(query));
                    model.setNdc11(query);
                    model.setPropertyList(propertyModel);
                    model.setRxcui(concept.getTerminologyId());
                    return model;
                  }
                }            
              }
            }       
          }

        } // if list.getCount() >1
      } 

      return model;

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
  }

  /**
   * Gets the ndc10.
   *
   * @param elevenDigitNdc the eleven digit ndc
   * @return the ndc10
   */
  private String getNdc10(String elevenDigitNdc) {
    StringBuffer outputString = new StringBuffer();
    outputString.append(elevenDigitNdc.substring(1, 5)).append("-");
    outputString.append(elevenDigitNdc.substring(5, 9)).append("-");
    outputString.append(elevenDigitNdc.substring(9));
    return outputString.toString();
  }
  
  /**
   * Gets the ndc9.
   *
   * @param elevenDigitNdc the eleven digit ndc
   * @return the ndc9
   */
  private String getNdc9(String elevenDigitNdc) {
    StringBuffer outputString = new StringBuffer();
    outputString.append(elevenDigitNdc.substring(1, 5)).append("-");
    outputString.append(elevenDigitNdc.substring(5, 9));
    return outputString.toString();
  }
  
  /**
   * Returns the model.
   *
   * @param inputString the input string
   * @param normalizedResults the normalized results
   * @return the model
   * @throws Exception the exception
   */
  private NdcModel getNdcModel(String inputString,
    List<ScoredResult> normalizedResults) throws Exception {

    final ContentService service = new ContentServiceJpa();

    try {

      // gather together original input string and normalized results
      Set<String> inputStrings = new HashSet<>();
      for (final ScoredResult result : normalizedResults) {
        inputStrings.add(result.getValue());
      }
      inputStrings.add(inputString);

      // Check all possible values of NDC
      for (final String query : inputStrings) {

        // try to find NDC based on inputString
        PfscParameter pfsc = new PfscParameterJpa();
        pfsc.setSearchCriteria(new ArrayList<SearchCriteria>());
        SearchResultList list = service.findConceptsForQuery("RXNORM", null,
            Branch.ROOT, "atoms.termType:NDC AND atoms.name:" + query, pfsc);

        // [ {version,ndc,ndcActive,rxcui,rxcuiActive}, ... ]
        List<NdcRecord> recordList = new ArrayList<>();

        // list will have each matching concept - e.g. from each version.
        if (list.getCount() > 0) {
          // Convert each search result into a record
          for (final SearchResult result : list.getObjects()) {
            final Concept concept = service.getConcept(result.getId());
            boolean foundActiveMatchingNdc = false;
            for (final Atom atom : concept.getAtoms()) {
              if (atom.getTermType().equals("NDC") && !atom.isObsolete()
                  && atom.getName().equals(inputString)) {
                foundActiveMatchingNdc = true;
              }
            }

            final NdcRecord record = new NdcRecord();
            record.ndcActive = foundActiveMatchingNdc;
            record.rxcui = result.getTerminologyId();
            record.version = result.getVersion();

            recordList.add(record);
          }

          // Sort the record list (so most recent is at the top)
          Collections.sort(recordList);

          final NdcModel model = new NdcModel();
          model.setActive(recordList.get(0).ndcActive);
          model.setNdc(inputString);
          model.setRxcui(recordList.get(0).rxcui);

          // RXCUI VERSION ACTIVE
          // 12343 20160404 true
          // 12343 20160304 true
          // 43921 20160204 true
          //
          // History
          // {rxcui: 12343, startDate:20160304, endDate: 20160404
          // },
          // {rxcui: 43921, startDate:20160204, endDate: 20160204 }

          List<NdcHistoryModel> historyModels = new ArrayList<>();
          NdcHistoryModel historyModel = new NdcHistoryModel();
          String prevRxcui = null;
          String prevVersion = null;
          for (NdcRecord record : recordList) {
            // handle first record
            if (prevRxcui == null) {
              historyModel.setRxcui(record.rxcui);
              historyModel.setEndDate(record.version);
            }

            // when rxcui changes
            if (prevRxcui != null && !prevRxcui.equals(record.rxcui)) {
              if (historyModel != null) {
                historyModel.setStartDate(prevVersion);
                historyModels.add(historyModel);
              }
              historyModel = new NdcHistoryModel();
              historyModel.setRxcui(record.rxcui);
              historyModel.setEndDate(record.version);
            }

            prevRxcui = record.rxcui;
            prevVersion = record.version;
          }
          // Handle the final record
          historyModel.setStartDate(prevVersion);
          historyModels.add(historyModel);

          model.setHistory(historyModels);
          return model;

        } // if list.getCount() >1
      }

      // try to find NDC based on normalizedResults

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }
    return null;
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
}
