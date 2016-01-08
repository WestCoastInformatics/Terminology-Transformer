/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import com.wci.tt.helpers.KeyValuePairLists;
import com.wci.tt.helpers.PrecedenceList;
import com.wci.tt.helpers.TerminologyList;
import com.wci.tt.model.meta.Terminology;

/**
 * Represents a security available via a REST service.
 */
public interface MetadataServiceRest {

  /**
   * Returns all metadata for a terminology and version
   * 
   * @param terminology the terminology
   * @param version the version
   * @param authToken
   * @return the all metadata
   * @throws Exception if anything goes wrong
   */
  public KeyValuePairLists getAllMetadata(String terminology, String version,
    String authToken) throws Exception;

  /**
   * Returns all terminologies with only their latest version
   * @param authToken
   * @return the all terminologies latest versions
   * @throws Exception if anything goes wrong
   */
  public TerminologyList getAllTerminologiesLatestVersions(String authToken)
    throws Exception;

  /**
   * Returns all terminologies and all versions
   * @param authToken
   * @return all terminologies and versions
   * @throws Exception if anything goes wrong
   */

  public TerminologyList getTerminologies(String authToken) throws Exception;

  /**
   * Gets the terminology information for a terminology
   *
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the terminology information
   * @throws Exception the exception
   */
  public Terminology getTerminology(String terminology, String version,
    String authToken) throws Exception;

  /**
   * Gets the default precedence list.
   *
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the default precedence list
   * @throws Exception the exception
   */
  public PrecedenceList getDefaultPrecedenceList(String terminology,
    String version, String authToken) throws Exception;
}
