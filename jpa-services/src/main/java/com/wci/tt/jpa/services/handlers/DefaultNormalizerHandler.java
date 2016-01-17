package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.services.handlers.NormalizerHandler;

/**
 * Default implementation of {@link NormalizerHandler}.
 * 
 * This class demonstrates a "naive" implementation of the Normalizer.
 * 
 * Class created to prove that supporting functionality works, not to provide
 * meaningful results.
 */
public class DefaultNormalizerHandler implements NormalizerHandler {
  /**
   * Instantiates an empty {@link DefaultNormalizerHandler}.
   *
   * @throws Exception the exception
   */
  public DefaultNormalizerHandler() {
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // N/A
  }

  /* see superclass */
  @Override
  public String getName() {
    return "Default Normalizer handler";
  }

  /* see superclass */
  @Override
  public List<ScoredResult> normalize(String inputStr, DataContext context)
    throws Exception {
    // Simply return data passed in for this "naive" case. As such, the score is
    // set to '1'.
    List<ScoredResult> results = new ArrayList<ScoredResult>();

    ScoredResult r = new ScoredResultJpa();

    r.setValue(inputStr);
    r.setScore(1);

    results.add(r);

    return results;
  }
}
