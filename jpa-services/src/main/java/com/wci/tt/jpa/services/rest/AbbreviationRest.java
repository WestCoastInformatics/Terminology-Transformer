/*

 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.wci.umls.server.ValidationResult;

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
   * @param authToken the auth token
   * @return the input stream
   * @throws Exception the exception
   */
  public InputStream exportAbbreviationsFile(String type, String authToken)
    throws Exception;
}