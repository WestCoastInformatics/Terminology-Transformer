/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.wci.tt.jpa.services.algo.NdcLoaderAlgorithm;
import com.wci.umls.server.SourceData;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.LocalException;
import com.wci.umls.server.jpa.services.SourceDataServiceJpa;
import com.wci.umls.server.jpa.services.handlers.AbstractSourceDataHandler;
import com.wci.umls.server.jpa.services.rest.SecurityServiceRest;
import com.wci.umls.server.rest.impl.SecurityServiceRestImpl;
import com.wci.umls.server.services.SourceDataService;
import com.wci.umls.server.services.helpers.ProgressEvent;
import com.wci.umls.server.services.helpers.ProgressListener;

/**
 * Converter for RxNorm files.
 */
public class NdcSourceDataHandler extends AbstractSourceDataHandler {

  /** The attributes flag. */
  boolean attributesFlag = true;

  /**
   * Instantiates an empty {@link NdcSourceDataHandler}.
   */
  public NdcSourceDataHandler() {
    // n/a
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return "NDC Source Data Loader";
  }

  /**
   * Convert.
   *
   * @throws Exception the exception
   */
  @Override
  public void compute() throws Exception {

    // check pre-requisites
    if (sourceData.getSourceDataFiles().size() == 0) {
      throw new Exception(
          "No source data files specified for source data object "
              + sourceData.getName());
    }
    if (sourceData.getHandler().isEmpty()) {
      throw new Exception(
          "No source data loader specified for source data object "
              + sourceData.getName());
    }

    // find the data directory from the first sourceDataFile
    String inputDir = sourceData.getSourceDataFiles().get(0).getPath();

    if (!new File(inputDir).isDirectory()) {
      throw new LocalException(
          "Source data directory is not a directory: " + inputDir);
    }

    SourceDataService sourceDataService = new SourceDataServiceJpa();
    sourceDataService.updateSourceData(sourceData);

    // Use content service rest because it has "loadRrfTerminology"
    final Properties config = ConfigUtility.getConfigProperties();
    final SecurityServiceRest securityService = new SecurityServiceRestImpl();
    securityService.authenticate(config.getProperty("admin.user"),
        config.getProperty("admin.password")).getAuthToken();

    try {
      sourceData.setStatus(SourceData.Status.LOADING);
      sourceDataService.updateSourceData(sourceData);
      // Load NDC-RXNORM
      final NdcLoaderAlgorithm algorithm = new NdcLoaderAlgorithm();
      algorithm.setTerminology(sourceData.getTerminology());
      algorithm.setVersion(sourceData.getVersion());
      algorithm.setInputDir(inputDir);
      algorithm.setAttributesFlag(attributesFlag);
      algorithm.compute();
      algorithm.close();

      // Clean-up
      sourceData.setStatus(SourceData.Status.LOADING_COMPLETE);
      sourceDataService.updateSourceData(sourceData);

    } catch (Exception e) {
      sourceData.setStatus(SourceData.Status.LOADING_FAILED);
      sourceDataService.updateSourceData(sourceData);
      throw new Exception("Loading source data failed - " + sourceData, e);
    } finally {
      sourceDataService.close();
    }
  }

  /* see superclass */
  @Override
  public void reset() throws Exception {
    // n/a
  }

  /**
   * Fires a {@link ProgressEvent}.
   * @param pct percent done
   * @param note progress note
   */
  public void fireProgressEvent(int pct, String note) {
    ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(getClass()).info("    " + pct + "% " + note);
  }

  /* see superclass */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /* see superclass */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /* see superclass */
  @Override
  public void cancel() {
    throw new UnsupportedOperationException("cannot cancel.");
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void setSourceData(SourceData sourceData) {
    this.sourceData = sourceData;
  }

  /**
   * Sets the attributes flag.
   *
   * @param attributesFlag the attributes flag
   */
  public void setAttributesFlag(boolean attributesFlag) {
    this.attributesFlag = attributesFlag;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a
  }

  @Override
  public boolean checkPreconditions() throws Exception {
    // n/a
    return false;
  }

}
