/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModelList;
import com.wci.tt.jpa.infomodels.RxcuiModel;

/**
 * REST interface for handling NDC-RXNORM transformations.
 */
public interface NdcServiceRest {

  /**
   * Process the ndc and return rxcui.
   *
   * @param ndc the ndc
   * @param authToken the auth token
   * @return the ndc model
   * @throws Exception the exception
   */
  public NdcModel processNdc(String ndc, String authToken) throws Exception;

  /**
   * Process rxcui and return ndc codes.
   *
   * @param rxcui the rxcui
   * @param authToken the auth token
   * @return the rxcui model
   * @throws Exception the exception
   */
  public RxcuiModel processRxcui(String rxcui, String authToken) throws Exception;

  /**
   * Get Ndc properties.
   *
   * @param ndc the ndc
   * @param authToken the auth token
   * @return the ndc model
   * @throws Exception the exception
   */
  public NdcPropertiesModel getNdcProperties(String ndc, String authToken) throws Exception;

  /**
   * Gets the ndc properties for spl set id.
   *
   * @param inputString the input string
   * @param authToken the auth token
   * @return the ndc properties for spl set id
   * @throws Exception the exception
   */
  public NdcPropertiesModelList getNdcPropertiesForSplSetId(String inputString,
    String authToken) throws Exception;

}
