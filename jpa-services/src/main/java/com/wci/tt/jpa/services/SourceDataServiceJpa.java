/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import org.apache.log4j.Logger;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;
import com.wci.tt.jpa.SourceDataFileJpa;
import com.wci.tt.jpa.SourceDataJpa;
import com.wci.tt.services.SecurityService;
import com.wci.tt.services.SourceDataService;

/**
 * Reference implementation of the {@link SecurityService}.
 */
public class SourceDataServiceJpa extends RootServiceJpa implements
    SourceDataService {

  public SourceDataServiceJpa() throws Exception {
    super();
  }

  /* see superclass */
  @Override
  public SourceData addSourceData(SourceData sourceData)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - add source data " + sourceData.getName());
    return addHasLastModified(sourceData);
  }

  /* see superclass */
  @Override
  public void updateSourceData(SourceData sourceData) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - add source data " + sourceData.getName());
    updateHasLastModified(sourceData);
  }

  /* see superclass */
  @Override
  public void removeSourceData(Long sourceDataId) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - remove source data " + sourceDataId);
    removeHasLastModified(sourceDataId, SourceDataJpa.class);
  }
  
  /* see superclass */
  @Override
  public SourceDataFile addSourceDataFile(SourceDataFile sourceDataFile)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - add source data file " + sourceDataFile.getName());
    return addHasLastModified(sourceDataFile);
  }

  /* see superclass */
  @Override
  public void updateSourceDataFile(SourceDataFile sourceDataFile) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - add source data file " + sourceDataFile.getName());
    updateHasLastModified(sourceDataFile);
  }

  /* see superclass */
  @Override
  public void removeSourceDataFile(Long sourceDataFileId) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - remove source data " + sourceDataFileId);
    removeHasLastModified(sourceDataFileId, SourceDataFileJpa.class);
  }


}
