/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.infomodels.InfoModel;
import com.wci.tt.services.filters.Filter;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.services.handlers.SearchHandler;

/**
 * Abstract implementation of {@link ProviderHandler}.
 * 
 * Provides utilities for key algorithms:
 * 
 * <pre>
 * 1. Process 
 * 2. Identify (yet to be implemented)
 * </pre>
 */
public abstract class AbstractProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  /** The pre process filter. */
  protected Filter preProcessFilter = null;

  /** The post process filter. */
  protected Filter postProcessFilter = null;

  /** The log base value. */
  protected float LOG_BASE_VALUE = 0f;

  /** The info model. */
  protected InfoModel<?> infoModel = null;;

  /**
   * Instantiates an empty {@link AbstractProvider}.
   */
  public AbstractProvider() {
    // n/a
  }

  /* see superclass */
  // TODO: We may also need a "getPreCheckValue(inputString)" - this
  // is what gets passed to the preCheck.
  @Override
  public boolean isPreCheckValid(TransformRecord record) throws Exception {
    // Filter precheck on the term name
    if (preProcessFilter != null) {
      if (!preProcessFilter.preCheckAccepts(record.getInputContext())
          || !preProcessFilter.preCheck(record.getInputString())) {
        /*
         * if (!ConfigUtility.isAnalysisMode()) { // TODO: Write out results }
         */
        return false;
      }
    }

    return true;
  }

  /* see superclass */
  @Override
  public Map<String, Float> filterResults(
    Map<String, Float> providerEvidenceMap, TransformRecord record)
      throws Exception {
    if (postProcessFilter != null && !providerEvidenceMap.isEmpty()) {
      if (postProcessFilter.postCheckAccepts(record.getOutputContext())) {
        return postProcessFilter.postCheck(record.getInputString(),
            record.getNormalizedResults(), providerEvidenceMap);
        /*
         * if (!ConfigUtility.isAnalysisMode()) { // TODO: Write out results }
         */
      }
    }

    return providerEvidenceMap;
  }

  /**
   * Returns the provider-specific info model as created from the input string.
   *
   * @param inputString the input string
   * @return the info model
   * @throws Exception the exception
   */
  protected abstract InfoModel<?> getInfoModel(String inputString)
    throws Exception;

  /**
   * Indicates whether or not term is supported based on the provider.
   * 
   * Note: Many cases will simply return true, but some cases (like
   * distinguishing between LOINC vs regular LAB records may utilize this
   * approach)
   *
   * @param model the model
   * @return <code>true</code> if so, <code>false</code> otherwise
   * @throws Exception the exception
   */
  protected abstract boolean isTermSupported(InfoModel<?> model)
    throws Exception;

  /**
   * Perform actual provider-specific search.
   *
   * @param inputString the input string
   * @param handler the handler
   * @param service the service
   * @return the list
   * @throws Exception the exception
   */
  abstract protected List<ScoredResult> performSearch(String inputString,
    SearchHandler handler, ContentServiceJpa service) throws Exception;

  /**
   * Limit results based on some notion of local quality.
   *
   * @param results the results
   * @return the list
   * @throws Exception the exception
   */
  abstract protected List<ScoredResult> limitResults(List<ScoredResult> results)
    throws Exception;

  /* see superclass */
  public List<ScoredResult> process(TransformRecord record) throws Exception {
    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    final DataContext providerOutputContext = record.getProviderOutputContext();

    // Validate input/output context and inputString
    if (!validate(inputContext, providerOutputContext) || inputString == null
        || inputString.isEmpty()) {
      return new ArrayList<>();
    }

    Logger.getLogger(getClass()).debug("Process input term = " + inputString);
    Logger.getLogger(getClass()).debug("  inputContext = " + inputContext);
    Logger.getLogger(getClass())
        .debug("  providerOutputContext = " + providerOutputContext);

    final ContentServiceJpa service = new ContentServiceJpa();
    final SearchHandler searchHandler =
        service.getSearchHandler(ConfigUtility.DEFAULT);

    try {
      Map<String, ScoredResult> allResults = new HashMap<>();

      for (ScoredResult normResult : record.getNormalizedResults()) {
        infoModel = getInfoModel(normResult.getValue());

        // Bail if the provider doesn't support this model
        // e.g., skip messy cases
        if (!isTermSupported(infoModel)) {
          return new ArrayList<>();
        }

        List<ScoredResult> results =
            performSearch(normResult.getValue(), searchHandler, service);

        /* Log Results */
        Logger.getLogger(getClass()).info(
            "  results for " + normResult.getValue() + " = " + results.size());
        for (final ScoredResult result : results) {
          // Multiple process score by the normalized score to help identify top
          // scores to return when there are more than one instance of a concept
          // matching on different normalized strings
          result.setScore(result.getScore() * normResult.getScore());

          Logger.getLogger(getClass()).info(
              "    concept = (" + result.getScore() + ") " + result.getValue());

          if (!allResults.containsKey(result.getValue()) || (allResults
              .get(result.getValue()).getScore() < result.getScore())) {
            allResults.put(result.getValue(), result);
          }
        }
      }

      ArrayList<ScoredResult> finalResults =
          new ArrayList<ScoredResult>(allResults.values());
      Collections.sort(finalResults);

      return limitResults(finalResults);
    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }

  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(TransformRecord record)
    throws Exception {
    throw new UnsupportedOperationException(
        "No need for identify at this time");
  }

  /* see superclass */
  @Override
  public float getLogBaseValue() {
    return LOG_BASE_VALUE;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    if (preProcessFilter != null) {
      preProcessFilter.close();
    }

    if (postProcessFilter != null) {
      postProcessFilter.close();
    }
  }
}
