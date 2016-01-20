/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.util.List;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.umls.server.helpers.Configurable;

/**
 * Interface responsible for normalizing an input string of customer data
 * against its normalization knowledge in hopes of simplifying the process of
 * matching content.
 * 
 * The normalizations that take place are assisted by the data context
 * associated with the input string.
 * 
 * Results returned are ranked by probability per possible transformation as
 * more than one option may be available within the normalization knowledge
 */
public interface NormalizerHandler extends Configurable {

  /**
   * Returns normalized form of input string with a probability-score based on
   * context
   * 
   * Results placed in list based in ordered fashion with top score first
   * because there may be multiple way to interpret the inputStr.
   *
   * @param inputStr the input string
   * @param context the context
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredResult> normalize(String inputStr, DataContext context)
    throws Exception;
}
