/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.wci.tt.DataContext;
import com.wci.tt.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredDataContextJpa;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.services.handlers.ProviderHandler;

/**
 * Default implementation of {@link ProviderHandler}.
 * 
 * This class demonstrates a "naive" implementation of the Provider.
 * 
 * Class created to prove that supporting functionality works, not to provide
 * meaningful results.
 * 
 * Default Provider doesn't handle specific contexts, but rather supports all
 * contexts. Thus, the setter methods of AbstractContextHandler should not be
 * called.
 */
public class DefaultProviderHandler extends AbstractContextHandler implements
    ProviderHandler {

  /**
   * Instantiates an empty {@link DefaultProviderHandler}.
   */
  public DefaultProviderHandler() {
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // N/A
  }

  /* see superclass */
  @Override
  public String getName() {
    return "Default Provider handler";
  }

  /* see superclass */
  @Override
  public List<DataContext> accepts(DataContext context) throws Exception {
    // DefaultHandler supports any context passed in
    List<DataContext> contexts = new ArrayList<DataContext>();

    contexts.add(context);

    return contexts;
  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(String inputStr, DataContext context)
    throws Exception {
    // Simply return context passed in for this "naive" case. As such, the score
    // is set to '1'.
    List<ScoredDataContext> scoredContexts = new ArrayList<ScoredDataContext>();

    ScoredDataContext scoredContext = new ScoredDataContextJpa();

    scoredContext.setCustomer(context.getCustomer());
    scoredContext.setSemanticType(context.getSemanticType());
    scoredContext.setSpecialty(context.getSpecialty());
    scoredContext.setTerminology(context.getTerminology());
    scoredContext.setType(context.getType());
    scoredContext.setVersion(context.getVersion());
    scoredContext.setScore(1);

    scoredContexts.add(scoredContext);

    return scoredContexts;
  }

  @Override
  public List<ScoredResult> process(String inputStr, DataContext inputContext,
    DataContext outputContext) throws Exception {
    // Simply return data passed in for this "naive" case. As such, the score is
    // set to '1'.
    List<ScoredResult> results = new ArrayList<ScoredResult>();

    ScoredResult result = new ScoredResultJpa();

    result.setValue(inputStr);
    result.setScore(1);

    return results;
  }
}
