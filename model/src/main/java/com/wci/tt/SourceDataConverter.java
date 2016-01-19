package com.wci.tt;

import com.wci.umls.server.helpers.HasName;

public interface SourceDataConverter extends HasName {

  public void convert(SourceData sourceData) throws Exception;

}
