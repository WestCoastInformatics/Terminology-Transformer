/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;
import com.wci.tt.helpers.SourceDataFileList;
import com.wci.tt.helpers.SourceDataList;
import com.wci.tt.jpa.SourceDataFileJpa;
import com.wci.tt.jpa.SourceDataJpa;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.StringList;

/**
 * Represents a security available via a REST service.
 */
public interface SourceDataServiceRest {

  /**
   * Removes the source data file.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeSourceDataFile(Long id, String authToken) throws Exception;

  /**
   * Find source data files for query.
   *
   * @param query the query
   * @param pfsParameter the pfs parameter
   * @param authToken the auth token
   * @return the source data file list
   * @throws Exception the exception
   */
  public SourceDataFileList findSourceDataFilesForQuery(String query,
    PfsParameter pfsParameter, String authToken) throws Exception;

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
  public SourceDataFileList uploadSourceDataFile(InputStream fileInputStream,
    FormDataContentDisposition contentDispositionHeader, boolean unzip,
    String authToken) throws Exception;

  /**
   * Adds the source data file.
   *
   * @param sourceDataFile the source data file
   * @param authToken the auth token
   * @return the source data file
   * @throws Exception
   */
  public SourceDataFile addSourceDataFile(SourceDataFileJpa sourceDataFile,
    String authToken) throws Exception;

  /**
   * Update source data file.
   *
   * @param sourceDataFile the source data file
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateSourceDataFile(SourceDataFileJpa sourceDataFile,
    String authToken) throws Exception;

  /**
   * Removes the source data.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeSourceData(Long id, String authToken) throws Exception;

  /**
   * Find source data source data objects for query.
   *
   * @param query the query
   * @param pfsParameter the pfs parameter
   * @param authToken the auth token
   * @return the source data source data list
   * @throws Exception the exception
   */
  public SourceDataList findSourceDataForQuery(String query,
    PfsParameter pfsParameter, String authToken) throws Exception;

  /**
   * Save source data.
   *
   * @param sourceData the source data
   * @param authToken the auth token
   * @return the string
   * @throws Exception the exception
   */
  public SourceData addSourceData(SourceDataJpa sourceData, String authToken)
    throws Exception;

  /**
   * Update source data.
   *
   * @param sourceData the source data
   * @param authToken the auth token
   * @throws Exception
   */
  public void updateSourceData(SourceDataJpa sourceData, String authToken)
    throws Exception;

  /**
   * Gets the loader names.
   *
   * @param authToken the auth token
   * @return the loader names
   * @throws Exception the exception
   */
  public StringList getLoaderNames(String authToken) throws Exception;
}