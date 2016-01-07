/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.SourceDataFileList;

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
  public void deleteSourceDataFile(Long sourceDataFileId, String authToken) throws Exception;

  /**
   * Find source data files for query.
   *
   * @param query the query
   * @param pfsParameter the pfs parameter
   * @param authToken the auth token
   * @return the source data file list
   * @throws Exception 
   */
  public SourceDataFileList findSourceDataFilesForQuery(String query, PfsParameter pfsParameter, String authToken) throws Exception;
  
  /**
   * Gets all source data files
   *
   * @param pfsParameter the pfs parameter
   * @param authToken the auth token
   * @return the uploaded files details
   * @throws Exception the exception
   */
  public SourceDataFileList getSourceDataFiles(
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
  public String addSourceDataFile(InputStream fileInputStream,
    FormDataContentDisposition contentDispositionHeader, boolean unzip,
    String authToken) throws Exception;
}