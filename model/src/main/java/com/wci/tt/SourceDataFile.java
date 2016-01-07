package com.wci.tt;

import com.wci.tt.helpers.HasId;
import com.wci.tt.helpers.HasLastModified;
import com.wci.tt.helpers.HasName;

// TODO: Auto-generated Javadoc
/**
 * The Interface SourceDataFile.
 */
public interface SourceDataFile extends HasId, HasLastModified, HasName {

  /**
   * Gets the path.
   *
   * @return the path
   */
  public String getPath();

  /**
   * Sets the path.
   *
   * @param path the new path
   */
  public void setPath(String path);

  /**
   * Gets the source data name.
   *
   * @return the source data name
   */
  public String getSourceDataName();

  /**
   * Sets the source data name.
   *
   * @param sourceDataName the new source data name
   */
  public void setSourceDataName(String sourceDataName);

  /**
   * Gets the size.
   *
   * @return the size
   */
  public Long getSize();

  /**
   * Sets the size.
   *
   * @param size the new size
   */
  public void setSize(Long size);

}
