/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.filters;

import java.util.List;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.umls.server.helpers.Configurable;

/**
 * The Interface PostProcessingFilter.
 */
public interface PostProcessingFilter extends Configurable {

  /**
   * Identifies if the post-processing filter is applicable to the current
   * process. This is achieved by ensuring that the process's output data
   * context is supported by this filter.
   *
   * @param context the context
   * @return true, if successful
   */
  public boolean accepts(DataContext context);

  /**
   * Reviews all processed and converted results to filter out those results
   * that are faulty based on the remaining results.
   * 
   * This is done to ensure that the final result returned to the client is
   * correct. In doing so, this prevents bad results from being combined with
   * good results thus returning incorrect results.
   *
   * @param originalTerm the original term
   * @param normalizedTerms the normalized terms
   * @param results the results
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredResult> filterResults(String originalTerm,
    List<ScoredResult> normalizedTerms, List<ScoredResult> results)
      throws Exception;

}
