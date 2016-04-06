/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.jpa.services.rest.NdcServiceRest;
import com.wci.tt.services.CoordinatorService;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.SecurityService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Implementation the REST Service for NDC.
 */
@Path("/ndc")
@Api(value = "/ndc", description = "NDC Operations")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class NdcServiceRestImpl extends RootServiceRestImpl implements NdcServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link NdcServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public NdcServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /* see superclass */
  @Override
  @Path("/process/{ndc}")
  @GET
  @ApiOperation(value = "Process and Convert on all supported input/output data contexts", notes = "Execute the Process and Convert calls for all supported input and output contexts", response = NdcModel.class)
  public NdcModel process(
    @ApiParam(value = "NDC Input, e.g. 'oral tablet'", required = true) @PathParam("ndc") String ndc,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful POST call (Content): /process ndc=" + ndc);

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "process ndc",
          UserRole.ADMINISTRATOR);

      DataContext inputContext = new DataContextJpa();
      DataContext outputContext = new DataContextJpa();

      final List<ScoredResult> results =
          service.process(ndc, inputContext, outputContext);
      ScoredResult result = results.get(0);

      // Translate tuples into JPA object
      final NdcModel ndcModel = new NdcModel();
      ndcModel.setNdc(ndc);
      // TODO: is this correct? sufficient?
      ndcModel.setRxcui(result.getValue());

      return ndcModel;
    } catch (Exception e) {
      handleException(e, "trying to process ndc");
      return null;
    } finally {
      service.close();
      securityService.close();
    }
  }

}
