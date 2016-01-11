package com.wci.tt.jpa.converters;

import org.apache.log4j.Logger;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataConverter;
import com.wci.tt.helpers.ConverterStatus;
import com.wci.tt.jpa.services.ContentServiceJpa;
import com.wci.tt.jpa.services.SourceDataServiceJpa;
import com.wci.tt.services.ContentService;
import com.wci.tt.services.SourceDataService;

/**
 * Converter for RxNorm files
 */
public class RrfUmlsConverter implements SourceDataConverter {

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
  public void convert(SourceData sourceData, String terminologyAndVersion)
    throws Exception {

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
    Class clazz = Class.forName(sourceData.getConverterName());
 
    
    // find the data directory from the first sourceDataFile

    SourceDataService sourceDataService = new SourceDataServiceJpa();
    sourceData.setConverterStatus(ConverterStatus.CONVERTING);
    sourceDataService.updateSourceData(sourceData);

    ContentService contentService = new ContentServiceJpa();
    try {
      // load RRF multiple-part terminology, with terminology/version of data
      // files as terminology, source data name as version
      // goal is to allow easy searching of content based on source files, as
      // well as terminology/version
      contentService.loadRrfTerminology(terminology + "_" + version,
          sourceData.getName(), false, inputDir);
      sourceData.setConverterStatus(ConverterStatus.CONVERTED);
    } catch (Exception e) {
      Logger.getLogger(this.getClass())
          .error("Error converting source data for " + sourceData.getName()
              + " using converter " + this.getName());

      sourceData.setConverterStatus(ConverterStatus.FAILED);
    } finally {
      sourceDataService.updateSourceData(sourceData);
      contentService.close();
      sourceDataService.close();
    }
  }

  @Override
  public void convert(String terminology, String version) throws Exception {
    // TODO Auto-generated method stub

  }
}
