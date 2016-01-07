package com.wci.tt;

import java.util.List;

import com.wci.tt.helpers.HasId;
import com.wci.tt.helpers.HasLastModified;
import com.wci.tt.helpers.HasName;

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
}
