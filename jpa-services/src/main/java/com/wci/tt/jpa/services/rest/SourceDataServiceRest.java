/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.StringList;

/**
 * Represents a security available via a REST service.
 */
public interface SourceDataServiceRest {

  /**
   * Delete file.
   *
   * @param fileName the file name
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void deleteFile(String fileName, String authToken) throws Exception;

  /**
   * Gets the uploaded files details.
   *
   * @param pfsParameter the pfs parameter
   * @param authToken the auth token
   * @return the uploaded files details
   * @throws Exception the exception
   */
  public StringList getUploadedFilesDetails(PfsParameter pfsParameter,
    String authToken) throws Exception;

  /**
   * Save file.
   *
   * @param fileInputStream the file input stream
   * @param contentDispositionHeader the content disposition header
   * @param unzip the unzip
   * @param authToken the auth token
   * @return the string
   * @throws Exception the exception
   */
  public String saveFile(InputStream fileInputStream,
    FormDataContentDisposition contentDispositionHeader, boolean unzip,
    String authToken) throws Exception;
}