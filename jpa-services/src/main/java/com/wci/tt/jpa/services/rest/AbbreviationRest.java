/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import java.io.InputStream;
import java.util.List;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueJpa;

/**
 * Lists a transform routines available via a REST service.
 */
public interface AbbreviationRest {

  /**
   * Import abbreviations.
   *
   * @param contentDispositionHeader the content disposition header
   * @param in the in
   * @param type the type
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult importAbbreviations(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    String type, String authToken) throws Exception;

  /**
   * Validate abbreviations file.
   *
   * @param contentDispositionHeader the content disposition header
   * @param in the in
   * @param type the type
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateAbbreviationsFile(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    String type, String authToken) throws Exception;

  /**
   * Export abbreviations file.
   *
   * @param type the type
   * @param readyOnly the ready only flag
   * @param authToken the auth token
   * @return the input stream
   * @throws Exception the exception
   */
  public InputStream exportAbbreviationsFile(String type, String delimiter,
    boolean readyOnly, String authToken) throws Exception;

  /**
   * Adds the abbreviation.
   *
   * @param typeKeyValue the type key value
   * @param authToken the auth token
   * @return the type key value
   * @throws Exception the exception
   */
  public TypeKeyValue addAbbreviation(TypeKeyValueJpa typeKeyValue,
    String authToken) throws Exception;

  /**
   * Returns the abbreviation.
   *
   * @param id the id
   * @param authToken the auth token
   * @return the abbreviation
   * @throws Exception the exception
   */
  public TypeKeyValue getAbbreviation(Long id, String authToken)
    throws Exception;

  /**
   * Update abbreviation.
   *
   * @param typeKeyValue the type key value
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateAbbreviation(TypeKeyValueJpa typeKeyValue, String authToken)
    throws Exception;

  /**
   * Removes the abbreviation.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeAbbreviation(Long id, String authToken) throws Exception;

  /**
   * Find abbreviations.
   *
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the type key value list
   * @throws Exception the exception
   */
  public TypeKeyValueList findAbbreviations(String query, String filter,
    PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Compute review statuses.
   *
   * @param type the type
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void computeReviewStatuses(String type, String authToken)
    throws Exception;

  /**
   * Returns the review for abbreviation.
   *
   * @param id the id
   * @param authToken the auth token
   * @return the review for abbreviation
   * @throws Exception the exception
   */
  public TypeKeyValueList getReviewForAbbreviation(Long id, String authToken)
    throws Exception;

  /**
   * Returns the review for abbreviations.
   *
   * @param abbrList the abbr list
   * @param authToken the auth token
   * @return the review for abbreviations
   * @throws Exception the exception
   */
  public TypeKeyValueList getReviewForAbbreviations(List<Long> ids,
    String authToken) throws Exception;

  /**
   * Export abbreviations file.
   *
   * @param type the type
   * @param acceptNew the accept new
   * @param readyOnly the ready only
   * @param authToken the auth token
   * @return the input stream
   * @throws Exception the exception
   */
  public InputStream exportAbbreviationsFile(String type, boolean acceptNew,
    boolean readyOnly, String authToken) throws Exception;

}