package com.wci.tt.jpa.services.rest;

import com.wci.tt.jpa.infomodels.NdcModel;

public interface NdcServiceRest {


  /**
   * Process.
   *
   * @param ndc the ndc
   * @param authToken the auth token
   * @return the ndc model
   * @throws Exception the exception
   */
  public NdcModel process(String ndc, String authToken) throws Exception;
  
}
