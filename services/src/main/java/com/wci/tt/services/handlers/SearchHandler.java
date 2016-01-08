/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.util.List;

import javax.persistence.EntityManager;

import com.wci.tt.helpers.Configurable;
import com.wci.tt.helpers.HasLastModified;
import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.model.content.AtomClass;

/**
 * Generically represents an algorithm searching for {@link AtomClass} entities.
 */
public interface SearchHandler extends Configurable {

  /**
   * Returns the query results.
   *
   * @param <T> the
   * @param terminology the terminology
   * @param version the version
   * @param branch the branch
   * @param query the query
   * @param literalField the literal field
   * @param fieldNamesKey the field names key
   * @param clazz the class to search on
   * @param pfs the pfs
   * @param totalCt a container for the total number of results (for making a
   *          List class)
   * @param manager the entity manager
   * @return the query results
   * @throws Exception the exception
   */
  public <T extends HasLastModified> List<T> getQueryResults(
    String terminology, String version, String branch, String query,
    String literalField, Class<?> fieldNamesKey, Class<T> clazz,
    PfsParameter pfs, int[] totalCt, EntityManager manager) throws Exception;

}
