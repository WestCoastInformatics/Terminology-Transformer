/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.client;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.wci.tt.jpa.services.rest.AbbreviationRest;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueJpa;
import com.wci.umls.server.rest.client.RootClientRest;

/**
 * Class calling the REST Service for Abbreviation handling for
 * {@link AbbreviationServiceRest}.
 */
public class AbbreviationRestClient extends RootClientRest
    implements AbbreviationRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link AbbreviationRestClient} from the specified
   * parameters.
   *
   * @param config the config
   */
  public AbbreviationRestClient(Properties config) {
    this.config = config;
  }

  @Override
  public ValidationResult importAbbreviations(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    String type, String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ValidationResult validateAbbreviationsFile(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    String type, String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public InputStream exportAbbreviationsFile(String type, boolean readyOnly,
    boolean acceptNew, String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TypeKeyValue addAbbreviation(TypeKeyValueJpa typeKeyValue,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TypeKeyValue getAbbreviation(Long id, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateAbbreviation(TypeKeyValueJpa typeKeyValue, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeAbbreviation(Long id, String authToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public TypeKeyValueList findAbbreviations(String query, String filter,
    PfsParameterJpa pfs, String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void computeReviewStatuses(String type, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public TypeKeyValueList getReviewForAbbreviation(Long id, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TypeKeyValueList getReviewForAbbreviations(List<Long> ids,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void removeAbbreviations(List<Long> ids, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }

}
