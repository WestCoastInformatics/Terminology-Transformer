package com.wci.tt;

import com.wci.tt.helpers.HasId;
import com.wci.tt.helpers.HasName;

public interface SourceDataConverter extends HasName {

  void convert(SourceData sourceData, String terminology, String version,
    String inputDir) throws Exception;

}
