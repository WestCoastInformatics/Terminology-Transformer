/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.util.List;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.umls.server.helpers.Configurable;

/**
 * Interface responsible for functionality associated with a given
 * representation of one or more data contexts.
 * 
 * Multiple providers may represent aspects of aspects of a data context (such
 * as terminology).
 * 
 * Furthermore, multiple providers may represent the same exact data context.
 */
public interface ProviderHandler extends Configurable {

  /**
   * Ensures input context is supported. If it is, returns all output contexts
   * the identify & process methods support. Returns an empty list if the input
   * context is not supported.
   * 
   * 
   * Returns a list because provider may handle multiple output data contexts.
   * 
   * Note: Often overridden by AbstractAcceptsHandler.
   *
   * @param inputContext the input context
   * @return the list
   * @throws Exception the exception
   */
  public List<DataContext> accepts(DataContext inputContext) throws Exception;

  /**
   * Identifies probability of the input string being of the type defined by the
   * provider. an empty list if identify finds nothing.
   * 
   * Returns a list of ranked possible dataContexts within the provider with an
   * associated probability score.
   * 
   * Method able to handle NULL dataContexts.
   *
   * @param inputStr the input string
   * @param context the context
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredDataContext> identify(String inputStr, DataContext context)
    throws Exception;

  /**
   * Produces list of scored (by probability) results of as specified by the
   * output context for the (likely normalized) input string based on known
   * input context. Returns an empty list if there are no processing results.
   * 
   * Returns empty list for any DataContext for which accepts returns an empty
   * list.
   *
   * @param inputStr the input string
   * @param inputContext the input context
   * @param outputContext the output context
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredResult> process(String inputStr, DataContext inputContext,
    DataContext outputContext) throws Exception;

  /**
   * Returns the quality factor (0-1) for this provider.
   *
   * @return the quality
   */
  public float getQuality();

}
