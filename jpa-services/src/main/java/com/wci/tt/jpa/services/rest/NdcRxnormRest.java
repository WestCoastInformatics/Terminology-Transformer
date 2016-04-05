package com.wci.tt.jpa.services.rest;

import com.wci.tt.jpa.infomodels.NdcModel;

public interface NdcRxnormRest {

  /**
   * Load terminology ndc.
   *
   * @param terminology the terminology
   * @param version the version
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void loadTerminologyNdc(String terminology, String version,
    String inputDir, String authToken) throws Exception;
  
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
