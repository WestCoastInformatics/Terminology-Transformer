/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import java.io.InputStream;
import java.util.List;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesListModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.RxcuiModel;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.SearchResultList;
import com.wci.umls.server.helpers.StringList;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;

/**
 * REST interface for handling NDC-RXNORM transformations.
 */
public interface NdcServiceRest {

  /**
   * Process the ndc and return rxcui.
   *
   * @param ndc the ndc
   * @param history a flag indicating whether to include history
   * @param authToken the auth token
   * @return the ndc model
   * @throws Exception the exception
   */
  public NdcModel getNdcInfo(String ndc, Boolean history, String authToken)
    throws Exception;

  /**
   * Process rxcui and return ndc codes.
   *
   * @param rxcui the rxcui
   * @param history a flag indicating whether to include history
   * @param authToken the auth token
   * @return the rxcui model
   * @throws Exception the exception
   */
  public RxcuiModel getRxcuiInfo(String rxcui, Boolean history,
    String authToken) throws Exception;

  /**
   * Get Ndc properties.
   *
   * @param ndc the ndc
   * @param authToken the auth token
   * @return the ndc model
   * @throws Exception the exception
   */
  public NdcPropertiesModel getNdcProperties(String ndc, String authToken)
    throws Exception;

  /**
   * Gets the ndc properties for spl set id.
   *
   * @param inputString the input string
   * @param authToken the auth token
   * @return the ndc properties for spl set id
   * @throws Exception the exception
   */
  public NdcPropertiesListModel getNdcPropertiesForSplSetId(String inputString,
    String authToken) throws Exception;

  /**
   * Autocomplete.
   *
   * @param inputString the input string
   * @param authToken the auth token
   * @return the string list
   * @throws Exception the exception
   */
  public StringList autocomplete(String inputString, String authToken)
    throws Exception;

  /**
   * Find concepts by query.
   *
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  public SearchResultList findConcepts(String query, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Gets the ndc info batch.
   *
   * @param ndcs the ndcs
   * @param history the history
   * @param authToken the auth token
   * @return the ndc info batch
   * @throws Exception the exception
   */
  public List<NdcModel> getNdcInfoBatch(List<String> ndcs, Boolean history,
    String authToken) throws Exception;

  /**
   * Gets the rxcui info batch.
   *
   * @param rxcuis the rxcuis
   * @param history the history
   * @param authToken the auth token
   * @return the rxcui info batch
   * @throws Exception the exception
   */
  public List<RxcuiModel> getRxcuiInfoBatch(List<String> rxcuis,
    Boolean history, String authToken) throws Exception;
  
  // TODO Remove these once AbbreviationRestImpl picked up
  
  /**
   * Import abbreviations.
   *
   * @param contentDispositionHeader the content disposition header
   * @param in the in
   * @param type the type
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult importAbbreviations(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    String type, String authToken) throws Exception;

  /**
   * Validate abbreviations file.
   *
   * @param contentDispositionHeader the content disposition header
   * @param in the in
   * @param type the type
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateAbbreviationsFile(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    String type, String authToken) throws Exception;

  /**
   * Export abbreviations file.
   *
   * @param type the type
   * @param authToken the auth token
   * @return the input stream
   * @throws Exception the exception
   */
  public InputStream exportAbbreviationsFile(String type, String authToken)
    throws Exception;

}
