/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import com.wci.tt.helpers.ScoredDataContextList;
import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.tt.jpa.helpers.DataContextJpa;
import com.wci.tt.jpa.helpers.DataContextListJpa;

/**
 * Lists a transform routines available via a REST service.
 */
public interface TransformServiceRest {

  /**
   * Identify all Providers supported data contexts.
   *
   * @param inputStr the input string
   * @param dataContext the data context
   * @param authToken the authentication token
   * @return the scored data context list
   * @throws Exception the exception
   */
  ScoredDataContextList identify(String inputStr, DataContextJpa dataContext,
    String authToken) throws Exception;

  /**
   * Process and Convert on all supported input/output data contexts.
   *
   * @param inputStr the input string
   * @param inputOutputContexts the input output contexts
   * @param authToken the authentication token
   * @return the scored data context tuple list
   * @throws Exception the exception
   */
  ScoredDataContextTupleList process(String inputStr,
    DataContextListJpa inputOutputContexts, String authToken) throws Exception;

}