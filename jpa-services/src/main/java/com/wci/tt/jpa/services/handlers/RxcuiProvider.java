/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredDataContextJpa;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
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
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;

/**
 * Default implementation of {@link ProviderHandler}.
 * 
 * This provider converts a Rxcui into an NDC code (with
 * history information).
 * 
 */
public class RxcuiProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  /**
   * Instantiates an empty {@link RxcuiProvider}.
   *
   * @throws Exception the exception
   */
  public RxcuiProvider() throws Exception {

    // Configure input/output matchers

    // Input matcher needs to have a code, e.g. the caller should use this:
    // DataContext inputContext = new DataContextJpa();
    // inputContext.setType(DataContextType.CODE);
    // inputContext.setTerminology("RXNORM");
    DataContextMatcher inputMatcher = new DataContextMatcher();
    inputMatcher.configureContext(DataContextType.CODE, null, null, null, null,
        "RXNORM", null);
    DataContextMatcher outputMatcher = new DataContextMatcher();

    // Output matcher needs to have an RxcuiModel information model, e.g. the
    // caller should use this:
    // DataContext outputContext = new DataContextJpa();
    // outputContext.setType(DataContextType.INFO_MODEL);
    // inputContext.setInfoModelClass(RxcuiModel.class.getName());
    outputMatcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        RxcuiModel.class.getName(), null, null);
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
    return "Rxcui Provider Handler";
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

    // final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    // Simply return context passed in for this "naive" case. As such, the score
    // is set to '1'.
    List<ScoredDataContext> scoredContexts = new ArrayList<ScoredDataContext>();

    // Check whether inputString is an NDC code (worry about this later)
    if (!process(record).isEmpty()) {
      // If so, we know it is the supported input type.
      ScoredDataContext scoredContext = new ScoredDataContextJpa(inputContext);
      scoredContext.setScore(1);
      scoredContexts.add(scoredContext);
    }
    return scoredContexts;
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
    Logger.getLogger(getClass()).debug("  process - " + record.getInputString());

    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    final DataContext providerOutputContext = record.getProviderOutputContext();

    // Validate input/output context
    validate(inputContext, providerOutputContext);

    // Set up return value
    final List<ScoredResult> results = new ArrayList<ScoredResult>();

    // Attempt to find the RXNORM CUI (or CUIs) from the NDC code
    final RxcuiModel model = getModel(inputString, record.getNormalizedResults());
    if (model != null) {
      final ScoredResult result = new ScoredResultJpa();
      result.setValue(model.getModelValue());
      result.setScore(1);
      results.add(result);
      Logger.getLogger(getClass()).debug("    result = " + result.getValue());
    } else {
      return new ArrayList<ScoredResult>();
    }

    return results;
  }

  /**
   * The Class Record.
   */
  private class Record implements Comparable<Record> {

    /** The version. */
    public String version;

    /** The rxcui active. */
    public boolean rxcuiActive;

    /** The ndc. */
    public String ndc;

    /**
     * Compare to.
     *
     * @param o the o
     * @return the int
     */
    @Override
    public int compareTo(Record o) {
      return o.version.compareTo(version);
    }
  }

  /**
   * Returns the model.
   *
   * @param inputString the input string
   * @param normalizedResults the normalized results
   * @return the model
   * @throws Exception the exception
   */
  private RxcuiModel getModel(String inputString,
    List<ScoredResult> normalizedResults) throws Exception {

    final ContentService service = new ContentServiceJpa();

    try {

        // try to find NDC based on inputString
        PfscParameter pfsc = new PfscParameterJpa();
        pfsc.setSearchCriteria(new ArrayList<SearchCriteria>());
        // TODO for rxcui -> ndc
        SearchResultList list = service.findConceptsForQuery("RXNORM", null,
            Branch.ROOT, "atoms.termType:NDC AND atoms.name:" + inputString, pfsc);

        // [ {version,ndc,ndcActive,rxcui,rxcuiActive}, ... ]
        List<Record> recordList = new ArrayList<>();

        // list will have each matching concept - e.g. from each version.
        if (list.getCount() > 0) {
          // Convert each search result into a record
          for (final SearchResult result : list.getObjects()) {
            final Concept concept = service.getConcept(result.getId());
            // TODO redo this part
            boolean foundActiveMatchingNdc = false;
            for (final Atom atom : concept.getAtoms()) {
              if (atom.getTermType().equals("NDC") && !atom.isObsolete()
                  && atom.getName().equals(inputString)) {
                foundActiveMatchingNdc = true;
              }
            }

            final Record record = new Record();
            record.rxcuiActive = foundActiveMatchingNdc;
            record.ndc = result.getTerminologyId();
            record.version = result.getVersion();

            recordList.add(record);
          }

          // Sort the record list (so most recent is at the top)
          Collections.sort(recordList);

          final RxcuiModel model = new RxcuiModel();
          model.setActive(recordList.get(0).rxcuiActive);
          model.setRxcui(inputString);
          //model.setNdc(recordList.get(0).ndc);

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
          for (Record record : recordList) {
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
