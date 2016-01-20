/*
 *    Copyright 2016 West Coast Informatics, LLC
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

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredDataContextList;
import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.tt.jpa.helpers.DataContextJpa;
import com.wci.tt.jpa.helpers.DataContextListJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextListJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleListJpa;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.tt.services.CoordinatorService;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.SecurityService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Class implementation the REST Service for Transform routines for
 * {@link TransformServiceRest}. 
 * 
 * Includes hibernate tags for MEME database.
 */
@Path("/transform")
@Api(value = "/transform", description = "Transformation Operations")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class TransformServiceRestImpl extends RootServiceRestImpl implements
    TransformServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link TransformServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public TransformServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /* see superclass */
  @Override
  @Path("/identify/{inputStr}")
  @POST
  @ApiOperation(value = "Identify all Providers supported data contexts", notes = "Identifies all data contexts that are supported for every Provider based on config.properties", response = ScoredDataContextListJpa.class)
  public ScoredDataContextList identify(
    @ApiParam(value = "Input text, e.g. 'oral tablet'", required = true) @PathParam("inputStr") String inputStr,
    @ApiParam(value = "Data context, e.g. Defined Customer and/or Terminology, ...", required = true) DataContextJpa context,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (Content): /identify inputStr=" + inputStr
            + ", context=" + context);

    try {
      authorizeApp(securityService, authToken, "transform input string",
          UserRole.ADMINISTRATOR);

      CoordinatorService service = new CoordinatorServiceJpa();
      List<ScoredDataContext> contexts = service.identify(inputStr, context);

      // Translate contexts into JPA object
      ScoredDataContextList result = new ScoredDataContextListJpa();

      result.setObjects(contexts);
      result.setTotalCount(contexts.size());

      return result;
    } catch (Exception e) {
      handleException(e, "trying to transform input string");
      return null;
    } finally {
      securityService.close();
    }

  }

  /* see superclass */
  @Override
  @Path("/process/{inputStr}")
  @POST
  @ApiOperation(value = "Process and Convert on all supported input/output data contexts", notes = "Execute the Process and Convert calls for all supported input and output contexts", response = ScoredDataContextTupleListJpa.class)
  public ScoredDataContextTupleList process(
    @ApiParam(value = "Input text, e.g. 'oral tablet'", required = true) @PathParam("inputStr") String inputStr,
    @ApiParam(value = "Input and Output Data context, e.g. List of Defined Customer and/or Terminology", required = true) DataContextListJpa inputOutputContexts,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (Content): /process inputStr=" + inputStr
            + ", inputOutputContexts=" + inputOutputContexts);

    try {
      authorizeApp(securityService, authToken, "transform input string",
          UserRole.ADMINISTRATOR);

      // Get input/output contexts from JPA
      DataContext inputContext = inputOutputContexts.getObjects().get(0);
      DataContext outputContext = inputOutputContexts.getObjects().get(1);

      CoordinatorService service = new CoordinatorServiceJpa();
      List<ScoredDataContextTuple> tuples =
          service.process(inputStr, inputContext, outputContext);

      // Translate tuples into JPA object
      ScoredDataContextTupleList result = new ScoredDataContextTupleListJpa();

      result.setObjects(tuples);
      result.setTotalCount(tuples.size());

      return result;
    } catch (Exception e) {
      handleException(e, "trying to transform input string");
      return null;
    } finally {
      securityService.close();
    }
  }
}
