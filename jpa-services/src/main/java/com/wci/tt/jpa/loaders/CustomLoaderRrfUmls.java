package com.wci.tt.jpa.loaders;

import java.io.File;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataConverter;
import com.wci.tt.jpa.services.SourceDataServiceJpa;
import com.wci.tt.services.SourceDataService;
import com.wci.umls.server.helpers.LocalException;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.services.ContentService;

/**
 * Converter for RxNorm files
 */
public class CustomLoaderRrfUmls implements SourceDataConverter {

  /** The name. */
  private String name;

  /**
   * Gets the name.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Convert.
   * @throws Exception
   */
  @Override
  public void convert(SourceData sourceData) throws Exception {

    // check pre-requisites
    if (sourceData.getSourceDataFiles().size() == 0) {
      throw new Exception(
          "No source data files specified for source data object "
              + sourceData.getName());
    }
    if (sourceData.getConverterName().isEmpty()) {
      throw new Exception(
          "No source data converter specified for source data object "
              + sourceData.getName());
    }

    // find the data directory from the first sourceDataFile
    String inputDir = sourceData.getSourceDataFiles().get(0).getPath();

    if (new File(inputDir).isDirectory()) {
      throw new LocalException(
          "Source data directory is not a directory: " + inputDir);
    }

    SourceDataService sourceDataService = new SourceDataServiceJpa();
    sourceDataService.updateSourceData(sourceData);

    ContentService contentService = new ContentServiceJpa();
    try {
      // load RRF multiple-part terminology, with terminology/version of data
      // files as terminology, source data name as version
      // goal is to allow easy searching of content based on source files, as
      // well as terminology/version
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      // TODO
      // contentService.loadRrfTerminology(sourceData.getName(),
      // sdf.format(new Date()), false, inputDir);
    } catch (Exception e) {
      Logger.getLogger(this.getClass())
          .error("Error converting source data for " + sourceData.getName()
              + " using converter " + this.getName());
      // TODO:
      // sourceData.setConverterStatus(ConverterStatus.FAILED);
    } finally {
      sourceDataService.updateSourceData(sourceData);
      contentService.close();
      sourceDataService.close();
    }
  }
}
