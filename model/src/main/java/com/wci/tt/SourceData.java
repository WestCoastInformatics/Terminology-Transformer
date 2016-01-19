package com.wci.tt;

import java.util.List;

import com.wci.umls.server.helpers.HasId;
import com.wci.umls.server.helpers.HasLastModified;
import com.wci.umls.server.helpers.HasName;

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

  /* see superclass */
  public String getDescription();

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(String description);
}
