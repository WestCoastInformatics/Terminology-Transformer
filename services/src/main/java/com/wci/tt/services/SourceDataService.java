/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;

/**
 * Generically represents a service for accessing {@link Project} information.
 */
public interface SourceDataService extends RootService {

  /**
   * Adds the source data file.
   *
   * @param sourceDataFile the source data file
   */
  public SourceDataFile addSourceDataFile(SourceDataFile sourceDataFile) throws Exception;

  /**
   * Update source data file.
   *
   * @param sourceDataFile the source data file
   */
  public void updateSourceDataFile(SourceDataFile sourceDataFile) throws Exception;

  /**
   * Removes the source data file.
   *
   * @param id the id
   */
  public void removeSourceDataFile(Long id) throws Exception;

  /**
   * Adds the source data.
   *
   * @param sourceData the source data
   * @return 
   */
  public SourceData addSourceData(SourceData sourceData) throws Exception;

  /**
   * Update source data.
   *
   * @param sourceData the source data
   */
  public void updateSourceData(SourceData sourceData) throws Exception;

  /**
   * Removes the source data.
   *
   * @param id the id
   */
  public void removeSourceData(Long id) throws Exception;

}