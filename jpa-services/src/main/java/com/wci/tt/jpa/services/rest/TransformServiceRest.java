/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import com.wci.tt.helpers.ScoredResultList;
import com.wci.tt.jpa.helpers.DataContextJpa;

// TODO: Auto-generated Javadoc
/**
 * Represents a security available via a REST service.
 */
public interface TransformServiceRest {

  /**
   * Process a set of concepts returned from identification.
   *
   * @param concepts the concepts
   * @param dataContext the data context
   * @param authToken the auth token
   * @throws Exception
   */
  public ScoredResultList process(String inputStr, DataContextJpa dataContext,
    String authToken) throws Exception;

}