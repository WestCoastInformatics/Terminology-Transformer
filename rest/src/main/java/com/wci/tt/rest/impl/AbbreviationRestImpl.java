/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.wci.tt.jpa.services.rest.AbbreviationRest;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueJpa;
import com.wci.umls.server.jpa.services.ProjectServiceJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.ProjectService;
import com.wci.umls.server.services.SecurityService;
import com.wci.umls.server.services.helpers.PushBackReader;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

/**
 * Class implementation the REST Service for Transform routines for
 * {@link TransformServiceRest}.
 * 
 * Includes hibernate tags for MEME database.
 */
@Path("/abbr")
@Api(value = "/abbr")
@SwaggerDefinition(info = @Info(description = "Abbreviation Operations", title = "Abbreviation Operations", version = "1.0.0"))
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class AbbreviationRestImpl extends RootServiceRestImpl
    implements AbbreviationRest {
  
  
  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link AbbreviationRestImpl}.
   *
   * @throws Exception the exception
   */
  public AbbreviationRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
    System.out.println("********* ABBR *************");
  }

  @Override
  public ValidationResult importAbbreviations(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    String type, String authToken) throws Exception {
    // TODO Copy from NdcRestImpl when registering worked out
    return null;
  }

  @Override
  public ValidationResult validateAbbreviationsFile(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    String type, String authToken) throws Exception {
    // TODO Copy from NdcRestImpl when registering worked out
    return null;
  }

  @Override
  public InputStream exportAbbreviationsFile(String type, String authToken)
    throws Exception {
    // TODO Copy from NdcRestImpl when registering worked out
    return null;
  }

  @Override
  public TypeKeyValue addAbbreviation(TypeKeyValueJpa typeKeyValue,
    String authToken) throws Exception {
    // TODO Copy from NdcRestImpl when registering worked out
    return null;
  }

  @Override
  public TypeKeyValue getAbbreviation(Long id, String authToken)
    throws Exception {
    // TODO Copy from NdcRestImpl when registering worked out
    return null;
  }

  @Override
  public void updateAbbreviation(TypeKeyValueJpa typeKeyValue, String authToken)
    throws Exception {
    // TODO Copy from NdcRestImpl when registering worked out
    
  }

  @Override
  public void removeAbbreviation(Long id, String authToken) throws Exception {
    // TODO Copy from NdcRestImpl when registering worked out
    
  }

  @Override
  public TypeKeyValueList findAbbreviations(String query, PfsParameterJpa pfs,
    String authToken) throws Exception {
    // TODO Copy from NdcRestImpl when registering worked out
    return null;
  }

 

}
