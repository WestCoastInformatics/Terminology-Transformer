/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.wci.tt.Provider;
import com.wci.tt.UserRole;
import com.wci.tt.helpers.ScoredResultList;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.helpers.ScoredResultListJpa;
import com.wci.tt.jpa.services.SecurityServiceJpa;
import com.wci.tt.jpa.services.helper.ProviderUtility;
import com.wci.tt.jpa.services.rest.SourceDataServiceRest;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.tt.services.SecurityService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link SourceDataServiceRest}.
 */
@Path("/transform")
@Api(value = "/transform", description = "Identity, Normalization, and Transofrmation Operations")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class TransformServiceRestImpl extends RootServiceRestImpl
    implements TransformServiceRest {

  /** The security service. */
  private SecurityService securityService;

  public TransformServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  @Override
  @Path("/process/{inputStr}")
  @POST
  public ScoredResultList process(
    @ApiParam(value = "Input text, e.g. 'oral tablet'", required = true) @PathParam("inputStr") String inputStr,
    @ApiParam(value = "Data context, e.g. dataType, semanticType, ...", required = false) DataContextJpa dataContext,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful POST call (Content): /process/" + inputStr);

    try {
      authorizeApp(securityService, authToken, "transform input string",
          UserRole.ADMIN);

      List<Provider> providers = ProviderUtility.getProviders();
      
      ScoredResultList results = new ScoredResultListJpa();
      
      for (Provider provider : providers) {
        ScoredResultList providerResults = provider.processInput(inputStr,
            dataContext == null ? new DataContextJpa() : dataContext);
        
        results.addAll(providerResults);
      }

      return results;
    } catch (Exception e) {
      handleException(e, "trying to transform input string");
      return null;
    } finally {
      securityService.close();
    }

  }

}
