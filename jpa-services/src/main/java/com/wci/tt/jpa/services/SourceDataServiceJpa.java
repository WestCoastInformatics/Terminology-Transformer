/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import java.util.List;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;
import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.SourceDataFileList;
import com.wci.tt.helpers.SourceDataList;
import com.wci.tt.helpers.StringList;
import com.wci.tt.jpa.SourceDataFileJpa;
import com.wci.tt.jpa.SourceDataJpa;
import com.wci.tt.jpa.helpers.SourceDataFileListJpa;
import com.wci.tt.jpa.helpers.SourceDataListJpa;
import com.wci.tt.services.SecurityService;
import com.wci.tt.services.SourceDataService;

// TODO: Auto-generated Javadoc
/**
 * Reference implementation of the {@link SecurityService}.
 */
public class SourceDataServiceJpa extends RootServiceJpa
    implements SourceDataService {

  /**
   * Instantiates a new source data service jpa.
   *
   * @throws Exception the exception
   */
  public SourceDataServiceJpa() throws Exception {
    super();
  }
  
  /* see superclass */
  @Override
  @SuppressWarnings("unchecked")
  public SourceDataList getSourceDatas() {
    Logger.getLogger(getClass()).debug("SourceData Service - get sourceDats");
    javax.persistence.Query query =
        manager.createQuery("select a from SourceDataJpa a");
    try {
      List<SourceData> sourceDatas = query.getResultList();
      SourceDataList sourceDataList = new SourceDataListJpa();
      sourceDataList.setObjects(sourceDatas);
      sourceDataList.setTotalCount(sourceDataList.getCount());
      for (SourceData sourceData : sourceDataList.getObjects()) {
        handleLazyInitialization(sourceData);
      }
      return sourceDataList;
    } catch (NoResultException e) {
      return null;
    }
  }

  /* see superclass */
  @Override
  public SourceData getSourceData(Long sourceDataId) throws Exception {
    Logger.getLogger(getClass())
        .debug("Source Data Service - get source data " + sourceDataId);
    return getHasLastModified(sourceDataId, SourceDataJpa.class);
  }

  /* see superclass */
  @Override
  public SourceData addSourceData(SourceData sourceData) throws Exception {
    Logger.getLogger(getClass())
        .debug("Source Data Service - add source data " + sourceData.getName());
   addHasLastModified(sourceData);
   
   return sourceData;
  }

  /* see superclass */
  @Override
  public void updateSourceData(SourceData sourceData) throws Exception {
    Logger.getLogger(getClass())
        .debug("Source Data Service - update source data " + sourceData.getName());
    updateHasLastModified(sourceData);
  }

  /* see superclass */
  @Override
  public void removeSourceData(Long sourceDataId) throws Exception {
    Logger.getLogger(getClass())
        .debug("Source Data Service - remove source data " + sourceDataId);
    removeHasLastModified(sourceDataId, SourceDataJpa.class);
  }
  
  /* see superclass */
  @Override
  public SourceDataList findSourceDatasForQuery(String query, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).info(
        "SourceData Service - find searchDatas " + query);

    int[] totalCt = new int[1];
    @SuppressWarnings("unchecked")
    List<SourceData> list =
        (List<SourceData>) getQueryResults(query == null || query.isEmpty()
            ? "id:[* TO *]" : query, SourceDataJpa.class, SourceDataJpa.class, pfs,
            totalCt);
    SourceDataList result = new SourceDataListJpa();
    result.setTotalCount(totalCt[0]);
    result.setObjects(list);
    for (SourceData searchData : result.getObjects()) {
      handleLazyInitialization(searchData);
    }
    return result;
  }
  
  /* see superclass */
  @Override
  @SuppressWarnings("unchecked")
  public SourceDataFileList getSourceDataFiles() {
    Logger.getLogger(getClass()).debug("SourceData Service - get sourceDataFiles");
    javax.persistence.Query query =
        manager.createQuery("select a from SourceDataFileJpa a");
    try {
      List<SourceDataFile> sourceDataFiles = query.getResultList();
      SourceDataFileList sourceDataFileList = new SourceDataFileListJpa();
      sourceDataFileList.setObjects(sourceDataFiles);
      sourceDataFileList.setTotalCount(sourceDataFileList.getCount());
      for (SourceDataFile sourceDataFile : sourceDataFileList.getObjects()) {
        handleLazyInitialization(sourceDataFile);
      }
      return sourceDataFileList;
    } catch (NoResultException e) {
      return null;
    }
  }

  /* see superclass */
  @Override
  public SourceDataFile getSourceDataFile(Long sourceDataFileId)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("Source Data Service - get source data file " + sourceDataFileId);
    return getHasLastModified(sourceDataFileId, SourceDataFileJpa.class);
  }

  /* see superclass */
  @Override
  public SourceDataFile addSourceDataFile(SourceDataFile sourceDataFile)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Source Data Service - add source data file " + sourceDataFile.getName());
    return addHasLastModified(sourceDataFile);
  }

  /* see superclass */
  @Override
  public void updateSourceDataFile(SourceDataFile sourceDataFile)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Source Data Service - update source data file " + sourceDataFile.getName());
    updateHasLastModified(sourceDataFile);

  }

  /* see superclass */
  @Override
  public void removeSourceDataFile(Long sourceDataFileId) throws Exception {
    Logger.getLogger(getClass())
        .debug("Source Data Service - remove source data " + sourceDataFileId);
    removeHasLastModified(sourceDataFileId, SourceDataFileJpa.class);
  }
  
  /**
   * Find source data files for query.
   *
   * @param query the query
   * @param pfs the pfs
   * @return the source data file list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public SourceDataFileList findSourceDataFilesForQuery(String query, PfsParameter pfs) throws Exception {
    Logger.getLogger(getClass()).info(
        "SourceDataFile Service - find searchDataFiles "  + query);

    int[] totalCt = new int[1];
    @SuppressWarnings("unchecked")
    List<SourceDataFile> list =
        (List<SourceDataFile>) getQueryResults(query == null || query.isEmpty()
            ? "id:[* TO *]" : query, SourceDataFileJpa.class, SourceDataFileJpa.class, pfs,
            totalCt);
    SourceDataFileList result = new SourceDataFileListJpa();
    result.setTotalCount(totalCt[0]);
    result.setObjects(list);
    for (SourceDataFile searchDataFile : result.getObjects()) {
      handleLazyInitialization(searchDataFile);
    }
    return result;
  }
  
  @Override
  public StringList getConverterNames() {
    StringList stringList = new StringList();
    
    // TODO Discover via reflection
    stringList.addObject("com.wci.tt.jpa.converters.RxNormConverter");
    
    return stringList;
  }

  /**
   * Handle lazy initialization.
   *
   * @param searchDataFile the search data file
   */
  private void handleLazyInitialization(SourceDataFile searchDataFile) {
    /// do nothing as yets    
  }

  /**
   * Handle lazy initialization.
   *
   * @param searchData the search data
   */
  private void handleLazyInitialization(SourceData searchData) {
    for (SourceDataFile sdf : searchData.getSourceDataFiles()) {
      handleLazyInitialization(sdf);
    }
  }

}
