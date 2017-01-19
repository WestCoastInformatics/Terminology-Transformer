/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.tt.jpa.services.rest.TypeKeyValueRest;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueJpa;
import com.wci.umls.server.jpa.services.ProjectServiceJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.ProjectService;
import com.wci.umls.server.services.SecurityService;

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
@Path("/tkv")
@Api(value = "/tkv")
@SwaggerDefinition(info = @Info(description = "Type Key Value Operations", title = "Type Key Value Operations", version = "1.0.0"))
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class TypeKeyValueRestImpl extends RootServiceRestImpl
    implements TypeKeyValueRest {
  
  static {
    System.out.println("DIE ECLIPSE DIE");
  }

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link TypeKeyValueRestImpl}.
   *
   * @throws Exception the exception
   */
  public TypeKeyValueRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  @Override
  @Path("/add")
  @PUT
  @ApiOperation(value = "Add a type key value", notes = "Adds a type key value object", response = TypeKeyValueJpa.class)
  public TypeKeyValue addTypeKeyValue(
    @ApiParam(value = "The type key value to add") TypeKeyValue tkv,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV, PUT): / " + tkv);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find projects",
          UserRole.VIEWER);
      return projectService.addTypeKeyValue(tkv);
    } catch (Exception e) {
      handleException(e, "trying to get projects ");
      return null;
    } finally {
      projectService.close();   
      securityService.close();
    }
  }

  @Override
  @Path("/{id}")
  @GET
  @ApiOperation(value = "Get a type key value", notes = "Gets a type key value object by id", response = TypeKeyValueJpa.class)
  public TypeKeyValue getTypeKeyValue(
    @ApiParam(value = "The type key value id, e.g. 1") @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {
    {
      Logger.getLogger(getClass()).info("RESTful call (TKV, Get): / " + id);
      final ProjectService projectService = new ProjectServiceJpa();
      try {
        authorizeApp(securityService, authToken, "find projects",
            UserRole.VIEWER);
        return projectService.getTypeKeyValue(id);
      } catch (Exception e) {
        handleException(e, "trying to get projects ");
        return null;
      } finally {
        projectService.close();   
        securityService.close();
      }
    }
  }

  @Override
  @Path("/update")
  @POST
  @ApiOperation(value = "Update a type key value", notes = "Updates a type key value object", response = TypeKeyValueJpa.class)

  public void updateTypeKeyValue(
    @ApiParam(value = "The type key value to add") TypeKeyValue tkv,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV, Update): /update " + tkv.toString());
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find projects",
          UserRole.VIEWER);
      projectService.updateTypeKeyValue(tkv);
    } catch (Exception e) {
      handleException(e, "trying to get projects ");
     
    } finally {
      projectService.close();   
      securityService.close();
    }

  }

  @Override
  @Path("/remove/{id}")
  @DELETE
  @ApiOperation(value = "Removes a type key value", notes = "Removes a type key value object by id", response = TypeKeyValueJpa.class)

  public void removeTypeKeyValue(
    @ApiParam(value = "The type key value to remove") @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /remove " + id);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find projects",
          UserRole.VIEWER);
      projectService.removeTypeKeyValue(id);
    } catch (Exception e) {
      handleException(e, "trying to get projects ");
     
    } finally {
      projectService.close();   
      securityService.close();
    }

  }

  @Override
  @Path("/find}")
  @GET
  @ApiOperation(value = "Removes a type key value", notes = "Removes a type key value object by id", response = TypeKeyValueJpa.class)
  public TypeKeyValueList findTypeKeyValues(
    @ApiParam(value = "Query", required = false) @QueryParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken) throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /find, " + pfs);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find projects",
          UserRole.VIEWER);
      return projectService.findTypeKeyValuesForQuery(query, pfs);
    } catch (Exception e) {
      handleException(e, "trying to get projects ");
      return null;
    } finally {
      projectService.close();   
      securityService.close();
    }
  }

}
