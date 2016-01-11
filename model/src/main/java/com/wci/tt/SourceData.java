package com.wci.tt;

import java.util.List;

import com.wci.tt.helpers.ConverterStatus;
import com.wci.tt.helpers.HasId;
import com.wci.tt.helpers.HasLastModified;
import com.wci.tt.helpers.HasName;

// TODO: Auto-generated Javadoc
/**
 * The Interface SourceData.
 */
public interface SourceData extends HasId, HasLastModified, HasName {

  /**
   * Sets the source data files.
   *
   * @param sourceDataFiles the source data files
   * @return the publicvoid
   */
  public void setSourceDataFiles(List<SourceDataFile> sourceDataFiles);
  
  /**
   * Gets the source data files.
   *
   * @return the source data files
   */
  public List<SourceDataFile> getSourceDataFiles();
  
  
  /**
   * Sets the converter name.
   *
   * @param converter the new converter name
   */
  public void setConverterName(String converter);
  

  /**
   * Gets the converter name.
   *
   * @return the converter name
   */
  public String getConverterName();
  
  /**
   * Gets the converter name.
   *
   * @return the converter name
   */
  public ConverterStatus getConverterStatus();
  
  /**
   * Sets the converter status.
   *
   * @param converterStatus the new converter status
   */
  public void setConverterStatus(ConverterStatus converterStatus);

  /* see superclass */
  public String getDescription();
  
  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description);
}
