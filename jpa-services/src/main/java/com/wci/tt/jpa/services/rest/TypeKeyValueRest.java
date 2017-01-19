/*

 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;

/**
 * Lists a transform routines available via a REST service.
 */
public interface TypeKeyValueRest {

  public TypeKeyValue addTypeKeyValue(TypeKeyValue typeKeyValue,
    String authToken) throws Exception;

  public TypeKeyValue getTypeKeyValue(Long id, String authToken) throws Exception;

  public void updateTypeKeyValue(TypeKeyValue typeKeyValue, String authToken) throws Exception;

  public void removeTypeKeyValue(Long id, String authToken) throws Exception;

  public TypeKeyValueList findTypeKeyValues(String query, PfsParameterJpa pfs,
    String authToken) throws Exception;
}