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


  public ScoredDataContextTupleList processTerm(TypeKeyValue term) throws Exception;

  /**
   * Process terms.
   *
   * @param terms the terms
   * @throws Exception the exception
   */
  public void processTerms(List<TypeKeyValue> terms) throws Exception;

}
