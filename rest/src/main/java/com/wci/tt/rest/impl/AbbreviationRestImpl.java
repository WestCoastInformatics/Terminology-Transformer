/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.wci.tt.jpa.services.handlers.DefaultAbbreviationHandler;
import com.wci.tt.jpa.services.rest.AbbreviationRest;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.tt.services.handlers.AbbreviationHandler;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.PfsParameter;
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

  //
  // TODO All of these should be moved once register problem figured out
  //

  @Override
  @Path("/import/{type}")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Import abbreviations", notes = "Import abbreviations of single type from comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public ValidationResult importAbbreviations(
    @ApiParam(value = "Form data header", required = true) @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @ApiParam(value = "Content of definition file", required = true) @FormDataParam("file") InputStream in,
    @ApiParam(value = "Type of abbreviation, e.g. medAbbr", required = true) @PathParam("type") String type,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /import");
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username = authorizeApp(securityService, authToken,
          "import abbreviations", UserRole.USER);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      return abbrHandler.importAbbreviationFile(type, in);
    } catch (

    Exception e) {
      handleException(e, "trying to import abbreviations ");
      return null;
    } finally {
      // NOTE: No need to close, but included for future safety
      abbrHandler.close();
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/import/{type}/validate")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Validate abbreviations import file", notes = "Validates abbreviations from comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public ValidationResult validateAbbreviationsFile(
    @ApiParam(value = "Form data header", required = true) @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @ApiParam(value = "Content of definition file", required = true) @FormDataParam("file") InputStream in,
    @ApiParam(value = "Type of abbreviation, e.g. medAbbr", required = true) @PathParam("type") String type,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /find");
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      authorizeApp(securityService, authToken, "validate abbreviations file",
          UserRole.USER);
      abbrHandler.setService(projectService);
      return abbrHandler.validateAbbreviationFile(type, in);

    } catch (

    Exception e) {
      handleException(e, "trying to validate abbreviations file ");
      return null;
    } finally {
      // NOTE: No need to close, but included for future safety
      abbrHandler.close();
      projectService.close();
      securityService.close();
    }
  }

  @POST
  @Override
  @Produces("application/octet-stream")
  @Path("/export/{type}")
  @ApiOperation(value = "Export abbreviations", notes = "Exports abbreviations for type as comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public InputStream exportAbbreviationsFile(
    @ApiParam(value = "Type of abbreviation, e.g. medAbbr", required = true) @PathParam("type") String type,
    @ApiParam(value = "Flag to accept all new abbreviations, e.g. \t", required = false) @QueryParam("acceptNew") boolean acceptNew,
    @ApiParam(value = "Flag to export only abbreviations not flagged for review", required = false) @QueryParam("readyOnly") boolean readyOnly,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /find");
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username = authorizeApp(securityService, authToken, "export abbreviations",
          UserRole.USER);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      return abbrHandler.exportAbbreviationFile(type, acceptNew, readyOnly);
    } catch (Exception e) {
      handleException(e, "trying to export abbreviations");
      return null;
    } finally {
      // NOTE: No need to close, but included for future safety
      abbrHandler.close();
      projectService.close();
      securityService.close();
    }

  }

  @Override
  @Path("/review/{type}/compute")
  @POST
  @ApiOperation(value = "Compute abbreviations review status", notes = "Recomputes review statuses for abbreviations of specified type")
  public void computeReviewStatuses(
    @ApiParam(value = "Type of abbreviation, e.g. medAbbr", required = true) @PathParam("type") String type,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /find");
    final ProjectService projectService = new ProjectServiceJpa();

    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username = authorizeApp(securityService, authToken,
          "export abbreviations", UserRole.USER);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      abbrHandler.computeAbbreviationStatuses(type);
    } catch (Exception e) {
      handleException(e, "trying to export abbreviations");

    } finally {
      // NOTE: No need to close, but included for future safety
      abbrHandler.close();
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/review/{id}")
  @GET
  @ApiOperation(value = "Retrieve review list for abbreviation", notes = "Retrieve list of abbreviations requiring review for a abbreviation by id")
  public TypeKeyValueList getReviewForAbbreviation(
    @ApiParam(value = "Id of abbreviation, e.g. 1", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /find");
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      authorizeApp(securityService, authToken, "get review for abbreviation",
          UserRole.USER);
      abbrHandler.setService(projectService);
      TypeKeyValue abbr = projectService.getTypeKeyValue(id);
      return abbrHandler.getReviewForAbbreviation(abbr);
    } catch (Exception e) {
      handleException(e, "trying to get review for abbreviation");
      return null;
    } finally {
      // NOTE: No need to close, but included for future safety
      abbrHandler.close();
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/review")
  @POST
  @ApiOperation(value = "Retrieve review list for abbreviations", notes = "Retrieve list of abbreviations requiring review for a list of abbreviations ids")
  public TypeKeyValueList getReviewForAbbreviations(
    @ApiParam(value = "List of abbreviation ids", required = true) List<Long> ids,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /find");
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      authorizeApp(securityService, authToken, "get review for abbreviations",
          UserRole.USER);
      abbrHandler.setService(projectService);
      List<TypeKeyValue> abbrs = new ArrayList<>();
      for (Long id : ids) {
        abbrs.add(projectService.getTypeKeyValue(id));
      }
      return abbrHandler.getReviewForAbbreviations(abbrs);
    } catch (Exception e) {
      handleException(e, "trying to get review for abbreviations");
      return null;
    } finally {
      // NOTE: No need to close, but included for future safety
      abbrHandler.close();
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/{id}")
  @GET
  @ApiOperation(value = "Get a abbreviation", notes = "Gets a abbreviation object by id", response = TypeKeyValueJpa.class)
  public TypeKeyValue getAbbreviation(
    @ApiParam(value = "The abbreviation id, e.g. 1") @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    {
      Logger.getLogger(getClass()).info("RESTful call (Project, Get): / " + id);
      final ProjectService projectService = new ProjectServiceJpa();
      try {
        authorizeApp(securityService, authToken, "get abbreviation",
            UserRole.USER);
        return projectService.getTypeKeyValue(id);
      } catch (Exception e) {
        handleException(e, "trying to get abbreviation ");
        return null;
      } finally {
        projectService.close();
        securityService.close();
      }
    }
  }

  @Override
  @Path("/add")
  @PUT
  @ApiOperation(value = "Add a abbreviation", notes = "Adds a abbreviation object", response = TypeKeyValueJpa.class)
  public TypeKeyValue addAbbreviation(
    @ApiParam(value = "The abbreviation to add") TypeKeyValueJpa typeKeyValue,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Project, PUT): / " + typeKeyValue);
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username = authorizeApp(securityService, authToken,
          "add abbreviation", UserRole.USER);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      abbrHandler.updateWorkflowStatus(typeKeyValue);
      return projectService.addTypeKeyValue(typeKeyValue);
    } catch (Exception e) {
      handleException(e, "trying to add abbreviation ");
      return null;
    } finally {
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/update")
  @POST
  @ApiOperation(value = "Update a abbreviation", notes = "Updates a abbreviation object", response = TypeKeyValueJpa.class)

  public void updateAbbreviation(
    @ApiParam(value = "The abbreviation to add") TypeKeyValueJpa typeKeyValue,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Project, TypeKeyValue): /update "
            + typeKeyValue.toString());
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username = authorizeApp(securityService, authToken,
          "update abbreviation", UserRole.USER);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      // TODO Decide whether we want update to change workflow status
      // i.e. should updates be set to NEW or NEEDS_REVIEW?
      abbrHandler.updateWorkflowStatus(typeKeyValue);
      projectService.updateTypeKeyValue(typeKeyValue);
    } catch (Exception e) {
      handleException(e, "trying to update abbreviation ");
    } finally {
      projectService.close();
      securityService.close();
    }

  }

  @Override
  @Path("/remove/{id}")
  @DELETE
  @ApiOperation(value = "Removes a abbreviation", notes = "Removes a abbreviation object by id", response = TypeKeyValueJpa.class)
  public void removeAbbreviation(
    @ApiParam(value = "The abbreviation to remove") @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Project/TypeKeyValue): /remove " + id);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      final String username = authorizeApp(securityService, authToken,
          "remove abbreviation", UserRole.USER);
      projectService.setLastModifiedBy(username);
      projectService.removeTypeKeyValue(id);
    } catch (Exception e) {
      handleException(e, "trying to remove abbreviation ");

    } finally {
      projectService.close();
      securityService.close();
    }

  }
  
  @Override
  @Path("/remove")
  @POST
  @ApiOperation(value = "Removes a abbreviation", notes = "Removes a abbreviation object by id", response = TypeKeyValueJpa.class)
  public void removeAbbreviations(
    @ApiParam(value = "The abbreviation to remove") List<Long> ids,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Project/TypeKeyValue): /remove " + ids);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      final String username = authorizeApp(securityService, authToken,
          "remove abbreviation", UserRole.USER);
      projectService.setLastModifiedBy(username);
      projectService.setTransactionPerOperation(false);
      projectService.beginTransaction();
      for (Long id : ids) {
        projectService.removeTypeKeyValue(id);
      }
      projectService.commit();
    } catch (Exception e) {
      handleException(e, "trying to remove abbreviation ");

    } finally {
      projectService.close();
      securityService.close();
    }

  }

  @Override
  @Path("/find")
  @POST
  @ApiOperation(value = "Finds abbreviations", notes = "Finds abbreviation objects", response = TypeKeyValueJpa.class)
  public TypeKeyValueList findAbbreviations(
    @ApiParam(value = "Query", required = false) @QueryParam("query") String query,
    @ApiParam(value = "Filter type", required = false) @QueryParam("filter") String filter,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (Project): /find, " + query + ", " + filter + ", " + pfs);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find abbreviations",
          UserRole.USER);

      TypeKeyValueList list = null;

      // if filter supplied, retrieve all results and pass to handler
      if (filter != null) {
        PfsParameter lpfs = new PfsParameterJpa(pfs);
        lpfs.setMaxResults(-1);
        lpfs.setStartIndex(-1);
        list = projectService.findTypeKeyValuesForQuery(query, lpfs);
        final AbbreviationHandler abbrHandler =
            new DefaultAbbreviationHandler();
        abbrHandler.setService(projectService);
        list = abbrHandler.filterResults(list, filter, pfs);
      } else {
        list = projectService.findTypeKeyValuesForQuery(query, pfs);
      }

      return list;
    } catch (Exception e) {
      handleException(e, "trying to find abbreviations ");
      return null;
    } finally {
      projectService.close();
      securityService.close();
    }
  }
 

}
