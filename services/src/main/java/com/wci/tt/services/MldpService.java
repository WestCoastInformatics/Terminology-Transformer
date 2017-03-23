/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services;

import java.util.List;

import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.umls.server.helpers.TypeKeyValue;

/**
 * Generically represents a service for handling top-level
 * terminology-transformation routines.
 */
public interface MldpService extends CoordinatorService {


  /**
   * Process term.
   *
   * @param term the term
   * @return the scored data context tuple list
   * @throws Exception the exception
   */
  public ScoredDataContextTupleList processTerm(TypeKeyValue term) throws Exception;

  
  /**
   * Process term with caching.
   *
   * @param term the term
   * @return the scored data context tuple list
   * @throws Exception the exception
   */
  public ScoredDataContextTupleList processTermWithCaching(TypeKeyValue term) throws Exception;

  /**
   * Process terms.
   *
   * @param terms the terms
   * @throws Exception the exception
   */
  public void processTerms(List<TypeKeyValue> terms) throws Exception;

}
