/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredDataContextJpa;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.handlers.ProviderHandler;

/**
 * Default implementation of {@link ProviderHandler}.
 * 
 * This provider converts a normalized NDC code into an RXNORM code 
 * (with history information).
 * 
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
    // Takes any name, returns a code
    DataContextMatcher inputMatcher = new DataContextMatcher();
    inputMatcher.configureContext(DataContextType.CODE, null, null, null, null,
        "NDC", null);
    DataContextMatcher outputMatcher = new DataContextMatcher();
    outputMatcher.configureContext(DataContextType.CODE, null, null, null, null,
        "RXNORM", null);
    addMatcher(inputMatcher, outputMatcher);

  }

  /* see superclass */
  @Override
  public String getName() {
    return "NDC Provider Handler";
  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(TransformRecord record)
    throws Exception {

    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    // Simply return context passed in for this "naive" case. As such, the score
    // is set to '1'.
    List<ScoredDataContext> scoredContexts = new ArrayList<ScoredDataContext>();

    // Check whether inputString is an NDC code
    // TODO:
    if (true) {
      // If so, we know it is the supported input type.
      ScoredDataContext scoredContext = new ScoredDataContextJpa(inputContext);
      scoredContext.setScore(1);
      scoredContexts.add(scoredContext);
    }
    return scoredContexts;
  }

  /* see superclass */
  @Override
  public List<ScoredResult> process(TransformRecord record) throws Exception {

    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    final DataContext providerOutputContext = record.getProviderOutputContext();

    // Validate input/output context
    validate(inputContext, providerOutputContext);

    // Set up return value
    final List<ScoredResult> results = new ArrayList<ScoredResult>();

    // Attempt to find the RXNORM CUI (or CUIs) from the NDC code
    if (true) {
      final ScoredResult result = new ScoredResultJpa();
      result.setValue(inputString);
      result.setScore(1);
      results.add(result);
    }

    return results;
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    super.setProperties(p);
  }

  /* see superclass */
  @Override
  public void addFeedback(String inputString, DataContext context,
    String feedbackString, DataContext outputContext) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void removeFeedback(String inputString, DataContext context,
    DataContext outputContext) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public float getLogBaseValue() {
    return 0;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }

  /* see superclass */
  @Override
  public boolean isPreCheckValid(TransformRecord record) {
    // Initial setup until specific rules defined
    return true;
  }

  /* see superclass */
  @Override
  public Map<String, Float> filterResults(
    Map<String, Float> providerEvidenceMap, TransformRecord record) {
    // Initial setup until specific rules defined
    return providerEvidenceMap;
  }
}
