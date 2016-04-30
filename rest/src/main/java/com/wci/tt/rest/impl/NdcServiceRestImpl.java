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
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesListModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.RxcuiModel;
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
@Path("/rxnorm")
@Api(value = "/rxnorm", description = "Operations related to NDC, RXCUI, and SPL_SET_ID lookups")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class NdcServiceRestImpl extends RootServiceRestImpl
    implements NdcServiceRest {

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
  @Path("/ndc/{ndc}")
  @GET
  @ApiOperation(value = "Get NDC info", notes = "Gets NDC info and RXCUI history for specified NDC.", response = NdcModel.class)
  public NdcModel getNdcInfo(
    @ApiParam(value = "NDC Input, e.g. '12345678911'", required = true) @PathParam("ndc") String ndc,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass()).info("RESTful POST call (NDC): /ndc/" + ndc);

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "ndc info", UserRole.VIEWER);

      // Configure contexts
      DataContext inputContext = new DataContextJpa();
      inputContext.setType(DataContextType.CODE);
      inputContext.setTerminology("NDC");
      DataContext outputContext = new DataContextJpa();
      outputContext.setType(DataContextType.INFO_MODEL);
      outputContext.setInfoModelClass(NdcModel.class.getName());

      // Obtain results
      final List<ScoredResult> results =
          service.process(ndc, inputContext, outputContext);

      // Send emty value on no results
      if (results.size() == 0) {
        return new NdcModel();
      }

      // Otherwise, assume 1 result
      final ScoredResult result = results.get(0);

      // Translate tuples into JPA object
      final NdcModel ndcModel = new NdcModel().getModel(result.getValue());
      return ndcModel;
    } catch (Exception e) {
      handleException(e, "trying to get ndc info");
      return null;
    } finally {
      service.close();
      securityService.close();
    }
  }

  /* see superclass */
  @Override
  @Path("/rxcui/{rxcui}")
  @GET
  @ApiOperation(value = "Get RXCUI info", notes = "Gets RXCUI info and NDC history for specified RXCUI.", response = NdcModel.class)
  public RxcuiModel getRxcuiInfo(
    @ApiParam(value = "Rxcui Input, e.g. '12345678911'", required = true) @PathParam("rxcui") String rxcui,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful POST call (NDC): /rxcui/" + rxcui);

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "rxcui info", UserRole.VIEWER);

      // Configure contexts
      DataContext inputContext = new DataContextJpa();
      inputContext.setType(DataContextType.CODE);
      inputContext.setTerminology("RXNORM");
      DataContext outputContext = new DataContextJpa();
      outputContext.setType(DataContextType.INFO_MODEL);
      outputContext.setInfoModelClass(RxcuiModel.class.getName());

      // Obtain results
      final List<ScoredResult> results =
          service.process(rxcui, inputContext, outputContext);

      // Send emty value on no results
      if (results.size() == 0) {
        return new RxcuiModel();
      }

      // Otherwise, assume 1 result
      final ScoredResult result = results.get(0);

      // Translate tuples into JPA object
      final RxcuiModel rxcuiModel =
          new RxcuiModel().getModel(result.getValue());
      return rxcuiModel;
    } catch (Exception e) {
      handleException(e, "trying to get rxcui info");
      return null;
    } finally {
      service.close();
      securityService.close();
    }
  }

  /* see superclass */
  @Override
  @Path("/ndc/{ndc}/properties")
  @GET
  @ApiOperation(value = "Get NDC propertes", notes = "Gets NDC properties for specified NDC.", response = NdcPropertiesModel.class)
  public NdcPropertiesModel getNdcProperties(
    @ApiParam(value = "Ndc Input, e.g. '12345678911'", required = true) @PathParam("ndc") String ndc,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful POST call (NDC): /ndc/" + ndc + "/properties");

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get ndc properties",
          UserRole.VIEWER);

      // Configure contexts
      DataContext inputContext = new DataContextJpa();
      inputContext.setType(DataContextType.CODE);
      inputContext.setTerminology("NDC");
      DataContext outputContext = new DataContextJpa();
      outputContext.setType(DataContextType.INFO_MODEL);
      outputContext.setInfoModelClass(NdcPropertiesModel.class.getName());

      // Obtain results
      final List<ScoredResult> results =
          service.process(ndc, inputContext, outputContext);

      // Send emty value on no results
      if (results.size() == 0) {
        return new NdcPropertiesModel();
      }

      // Otherwise, assume 1 result
      final ScoredResult result = results.get(0);

      // Translate tuples into JPA object
      final NdcPropertiesModel ndcPropertiesModel =
          new NdcPropertiesModel().getModel(result.getValue());
      return ndcPropertiesModel;
    } catch (Exception e) {
      handleException(e, "trying to get ndc properties");
      return null;
    } finally {
      service.close();
      securityService.close();
    }
  }

  /* see superclass */
  @Override
  @Path("/spl/{splSetId}/ndc/properties")
  @GET
  @ApiOperation(value = "Get SPL_SET_ID NDC propertes", notes = "Gets NDC properties info for specified SPL_SET_ID.", response = NdcPropertiesModel.class)
  public NdcPropertiesListModel getNdcPropertiesForSplSetId(
    @ApiParam(value = "Ndc Input, e.g. '12345678911'", required = true) @PathParam("splSetId") String splSetId,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful POST call (Ndc): /spl/" + splSetId + "/ndc/properties");

    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken,
          "get ndc properties for SPL_SET_ID", UserRole.VIEWER);

      // Configure contexts
      DataContext inputContext = new DataContextJpa();
      inputContext.setType(DataContextType.CODE);
      inputContext.setTerminology("SPL");
      DataContext outputContext = new DataContextJpa();
      outputContext.setType(DataContextType.INFO_MODEL);
      outputContext.setInfoModelClass(NdcPropertiesListModel.class.getName());

      // Obtain results
      final List<ScoredResult> results =
          service.process(splSetId, inputContext, outputContext);

      // Send emty value on no results
      if (results.size() == 0) {
        return new NdcPropertiesListModel();
      }

      // Otherwise, assume 1 result
      final ScoredResult result = results.get(0);

      // Translate tuples into JPA object
      final NdcPropertiesListModel ndcPropertiesModelList =
          new NdcPropertiesListModel().getModel(result.getValue());
      return ndcPropertiesModelList;
    } catch (Exception e) {
      handleException(e, "trying to get ndc properties for SPL_SET_ID");
      return null;
    } finally {
      service.close();
      securityService.close();
    }
  }
}
