/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;
import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.SourceDataFileList;
import com.wci.tt.helpers.SourceDataList;

// TODO: Auto-generated Javadoc
/**
 * Generically represents a service for accessing {@link Project} information.
 */
public interface SourceDataService extends RootService {

  /**
   * Gets the source data file.
   *
   * @param sourceDataFileId the source data file id
   * @return the source data file
   * @throws Exception the exception
   */
  public SourceDataFile getSourceDataFile(Long sourceDataFileId)
    throws Exception;

  /**
   * Adds the source data file.
   *
   * @param sourceDataFile the source data file
   * @return the source data file
   * @throws Exception the exception
   */
  public SourceDataFile addSourceDataFile(SourceDataFile sourceDataFile)
    throws Exception;

  /**
   * Update source data file.
   *
   * @param sourceDataFile the source data file
   * @throws Exception the exception
   */
  public void updateSourceDataFile(SourceDataFile sourceDataFile)
    throws Exception;

  /**
   * Removes the source data file.
   *
   * @param id the id
   * @throws Exception the exception
   */
  public void removeSourceDataFile(Long id) throws Exception;

  /**
   * Find source data files for query.
   *
   * @param query the query
   * @param pfs the pfs
   * @return the source data file list
   * @throws Exception the exception
   */
  public SourceDataFileList findSourceDataFilesForQuery(String query,
    PfsParameter pfs) throws Exception;
  

  /**
   * Gets the source data files.
   *
   * @return the source data files
   */
  public SourceDataFileList getSourceDataFiles();

  /**
   * Gets the source data.
   *
   * @param sourceDataId the source data id
   * @return the source data
   * @throws Exception the exception
   */
  public SourceData getSourceData(Long sourceDataId) throws Exception;

  /**
   * Adds the source data.
   *
   * @param sourceData the source data
   * @return the source data
   * @throws Exception the exception
   */
  public SourceData addSourceData(SourceData sourceData) throws Exception;

  /**
   * Update source data.
   *
   * @param sourceData the source data
   * @throws Exception the exception
   */
  public void updateSourceData(SourceData sourceData) throws Exception;

  /**
   * Removes the source data.
   *
   * @param id the id
   * @throws Exception the exception
   */
  public void removeSourceData(Long id) throws Exception;

  /**
   * Find source datas for query.
   *
   * @param query the query
   * @param pfs the pfs
   * @return the source data list
   * @throws Exception the exception
   */
  public SourceDataList findSourceDatasForQuery(String query, PfsParameter pfs)
    throws Exception;

}