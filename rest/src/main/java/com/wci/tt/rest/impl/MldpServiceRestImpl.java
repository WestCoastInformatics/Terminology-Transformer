/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.tt.jpa.services.MldpServiceJpa;
import com.wci.tt.jpa.services.algo.TerminologySimpleCsvLoaderAlgorithm;
import com.wci.tt.jpa.services.handlers.DefaultAbbreviationHandler;
import com.wci.tt.jpa.services.handlers.TermHandler;
import com.wci.tt.jpa.services.rest.MldpServiceRest;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.tt.services.MldpService;
import com.wci.tt.services.handlers.AbbreviationHandler;
import com.wci.umls.server.Project;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueListJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.ProjectServiceJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.workflow.WorkflowStatus;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.ContentService;
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
@Path("/mldp")
@Api(value = "/mldp")
@SwaggerDefinition(info = @Info(description = "MLDP-specific Operations", title = "MLDP-specific Operations", version = "1.0.0"))
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class MldpServiceRestImpl extends RootServiceRestImpl
    implements MldpServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link MldpServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public MldpServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  @Override
  @Path("/abbr/import")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Import abbreviations", notes = "Import abbreviations for a project from tab-delimited file", response = TypeKeyValueJpa.class)
  public ValidationResult importAbbreviations(
    @ApiParam(value = "Form data header", required = true) @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @ApiParam(value = "Content of definition file", required = true) @FormDataParam("file") InputStream in,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /abbr/import/" + projectId);
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "import abbreviations", UserRole.USER);
      Project project = projectService.getProject(projectId);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      return abbrHandler.importAbbreviationFile(project.getTerminology(), in);
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
  @Path("/abbr/import/validate")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Validate abbreviations import file", notes = "Validates abbreviations from comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public ValidationResult validateAbbreviationsFile(
    @ApiParam(value = "Form data header", required = true) @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @ApiParam(value = "Content of definition file", required = true) @FormDataParam("file") InputStream in,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /abbr/import/validate");
    final ProjectService projectService = new ProjectServiceJpa();
    final Project project = projectService.getProject(projectId);
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      authorizeProject(projectService, projectId, securityService, authToken,
          "validate abbreviations file", UserRole.USER);
      abbrHandler.setService(projectService);
      return abbrHandler.validateAbbreviationFile(project.getTerminology(), in);

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
  @Path("/abbr/export")
  @ApiOperation(value = "Export abbreviations", notes = "Exports abbreviations for type as comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public InputStream exportAbbreviationsFile(
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Flag to accept all new abbreviations, e.g. \t", required = false) @QueryParam("acceptNew") boolean acceptNew,
    @ApiParam(value = "Flag to export only abbreviations not flagged for review", required = false) @QueryParam("readyOnly") boolean readyOnly,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /abbr/export/" + projectId);
    final ProjectService projectService = new ProjectServiceJpa();
    final Project project = projectService.getProject(projectId);
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "export abbreviations", UserRole.USER);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      return abbrHandler.exportAbbreviationFile(project.getTerminology(),
          acceptNew, readyOnly);
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
  @Path("/abbr/review/compute")
  @POST
  @ApiOperation(value = "Compute abbreviations review status", notes = "Recomputes review statuses for abbreviations of specified type")
  public void computeReviewStatuses(
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP): /abbr/review/compute");
    final ProjectService projectService = new ProjectServiceJpa();
    final Project project = projectService.getProject(projectId);
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username =
          authorizeProject(projectService, projectId, securityService,
              authToken, "compute reviews for abbreviations", UserRole.USER);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      abbrHandler.computeAbbreviationStatuses(project.getTerminology());
    } catch (Exception e) {
      handleException(e, "trying to compute reviews for abbreviations");

    } finally {
      // NOTE: No need to close, but included for future safety
      abbrHandler.close();
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("abbr/review/{id}")
  @GET
  @ApiOperation(value = "Retrieve review list for abbreviation", notes = "Retrieve list of abbreviations requiring review for a abbreviation by id")
  public TypeKeyValueList getReviewForAbbreviation(
    @ApiParam(value = "Id of abbreviation, e.g. 1", required = true) @PathParam("id") Long id,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, GET): /abbr/review/" + id);
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      authorizeProject(projectService, projectId, securityService, authToken,
          "get review for abbreviation", UserRole.USER);
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
  @Path("/abbr/review")
  @POST
  @ApiOperation(value = "Retrieve review list for abbreviations", notes = "Retrieve list of abbreviations requiring review for a list of abbreviations ids")
  public TypeKeyValueList getReviewForAbbreviations(
    @ApiParam(value = "List of abbreviation ids", required = true) List<Long> ids,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /abbr/review");
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      authorizeProject(projectService, projectId, securityService, authToken,
          "get review for abbreviations", UserRole.USER);
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
  @Path("/abbr/{id}")
  @GET
  @ApiOperation(value = "Get a abbreviation", notes = "Gets a abbreviation object by id", response = TypeKeyValueJpa.class)
  public TypeKeyValue getAbbreviation(
    @ApiParam(value = "The abbreviation id, e.g. 1") @PathParam("id") Long id,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    {
      Logger.getLogger(getClass())
          .info("RESTful call (MLDP, Get): /abbr/ " + id);
      final ProjectService projectService = new ProjectServiceJpa();
      try {
        authorizeProject(projectService, projectId, securityService, authToken,
            "get abbreviation", UserRole.USER);
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
  @Path("/abbr/add")
  @PUT
  @ApiOperation(value = "Add a abbreviation", notes = "Adds a abbreviation object", response = TypeKeyValueJpa.class)
  public TypeKeyValue addAbbreviation(
    @ApiParam(value = "The abbreviation to add") TypeKeyValueJpa typeKeyValue,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, PUT): /abbr/add " + typeKeyValue);
    final ProjectService projectService = new ProjectServiceJpa();
    final Project project = projectService.getProject(projectId);
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "add abbreviation", UserRole.USER);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      typeKeyValue.setType(abbrHandler.getAbbrType(project.getTerminology()));
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
  @Path("/abbr/update")
  @POST
  @ApiOperation(value = "Update a abbreviation", notes = "Updates a abbreviation object", response = TypeKeyValueJpa.class)

  public void updateAbbreviation(
    @ApiParam(value = "The abbreviation to add") TypeKeyValueJpa typeKeyValue,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (MLDP, POST): /abbr/update " + typeKeyValue.toString());
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "update abbreviation", UserRole.USER);
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
  @Path("/abbr/remove/{id}")
  @DELETE
  @ApiOperation(value = "Removes a abbreviation", notes = "Removes a abbreviation object by id", response = TypeKeyValueJpa.class)
  public void removeAbbreviation(
    @ApiParam(value = "The abbreviation to remove") @PathParam("id") Long id,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, DELETE): /abbr/remove " + id);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "remove abbreviation", UserRole.USER);
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
  @Path("/abbr/remove")
  @POST
  @ApiOperation(value = "Removes abbreviations", notes = "Removes abbreviation objects for id list", response = TypeKeyValueJpa.class)
  public void removeAbbreviations(
    @ApiParam(value = "The abbreviation to remove") List<Long> ids,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /abbr/remove " + ids);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "remove abbreviation", UserRole.USER);
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
  @Path("/abbr/find")
  @POST
  @ApiOperation(value = "Finds abbreviations", notes = "Finds abbreviation objects", response = TypeKeyValueJpa.class)
  public TypeKeyValueList findAbbreviations(
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Query", required = false) @QueryParam("query") String query,
    @ApiParam(value = "Filter type", required = false) @QueryParam("filter") String filter,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (MLDPr): /abbr/find, "
        + query + ", " + filter + ", " + pfs);
    final ProjectService projectService = new ProjectServiceJpa();

    try {
      authorizeProject(projectService, projectId, securityService, authToken,
          "find abbreviations", UserRole.USER);

      TypeKeyValueList list = null;
      final Project project = projectService.getProject(projectId);
      final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();

      // use query restriction to restrict by computed type
      PfsParameter lpfs = new PfsParameterJpa(pfs);
      lpfs.setQueryRestriction((pfs.getQueryRestriction() == null
          || pfs.getQueryRestriction().isEmpty() ? ""
              : pfs.getQueryRestriction() + " AND ")
          + "type:\"" + abbrHandler.getAbbrType(project.getTerminology())
          + "\"");

      // if filter supplied, retrieve all results and pass to handler
      if (filter != null) {
        lpfs.setMaxResults(-1);
        lpfs.setStartIndex(-1);
        list = projectService.findTypeKeyValuesForQuery(query, lpfs);
        abbrHandler.setService(projectService);
        list = abbrHandler.filterResults(list, filter, pfs);
      } else {
        list = projectService.findTypeKeyValuesForQuery(query, lpfs);
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

  //
  // Concept import/export/validation
  //
  @Override
  @Path("/concept/import")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Import concepts", notes = "Import concepts from CSV file", response = TypeKeyValueJpa.class)
  public ValidationResult importConcepts(
    @ApiParam(value = "Form data header", required = true) @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @ApiParam(value = "Content of concepts file", required = true) @FormDataParam("file") InputStream in,
    @ApiParam(value = "Project id, e.g. 3", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Whether to keep file ids or assign new", required = false) @QueryParam("keepIds") boolean keepIds,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /concept/import");
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      final Project project = projectService.getProject(projectId);
      final String userName = authorizeProject(projectService, projectId,
          securityService, authToken, "import concepts", UserRole.USER);

      final TerminologySimpleCsvLoaderAlgorithm algo =
          new TerminologySimpleCsvLoaderAlgorithm();
      algo.setAssignIdentifiersFlag(true);
      algo.setInputStream(in);
      algo.setLastModifiedBy(userName);
      algo.setKeepFileIdsFlag(keepIds);
      algo.setTerminology(project.getTerminology());
      algo.setVersion(project.getVersion());
      algo.setProject(project);
      algo.setWorkflowStatus(WorkflowStatus.NEW);
      algo.compute();
      return algo.getValidationResult();
    } catch (

    Exception e) {
      handleException(e, "trying to import concepts ");
      return null;
    } finally {
      // NOTE: No need to close, but included for future safety
      projectService.close();
      securityService.close();
    }
  }

  @POST
  @Override
  @Produces("application/octet-stream")
  @Path("/concept/export")
  @ApiOperation(value = "Export concepts", notes = "Exports concepts for terminology as comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public InputStream exportConcepts(
    @ApiParam(value = "Project id, e.g. 3", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Flag to accept all new concepts, e.g. \t", required = false) @QueryParam("acceptNew") boolean acceptNew,
    @ApiParam(value = "Flag to export only concepts not flagged for review", required = false) @QueryParam("readyOnly") boolean readyOnly,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /concept/export");
    final ProjectService projectService = new ProjectServiceJpa();

    try {
      final Project project = projectService.getProject(projectId);
      final String userName = authorizeProject(projectService, projectId,
          securityService, authToken, "export concepts", UserRole.USER);

      final TerminologySimpleCsvLoaderAlgorithm algo =
          new TerminologySimpleCsvLoaderAlgorithm();
      algo.setLastModifiedBy(userName);
      return algo.export(project.getTerminology(), project.getVersion(),
          project.getBranch(), acceptNew, readyOnly);
    } catch (Exception e) {
      handleException(e, "trying to export concepts");
      return null;
    } finally {
      // NOTE: No need to close, but included for future safety

      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/concept/workflow")
  @POST
  @ApiOperation(value = "Mark concepts with workflow status", notes = "Marks concepts for workflow status given a list of ids", response = TypeKeyValueJpa.class)
  public void putConceptsInWorkflow(
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "The list of concept ids", required = true) List<Long> conceptIds,
    @ApiParam(value = "The workflow status, e.g. REVIEW_NEEDED", required = false) @QueryParam("workflowStatus") WorkflowStatus workflowStatus,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /concept/workflow " + workflowStatus
            + ", " + conceptIds);
    final ContentService contentService = new ContentServiceJpa();
    try {
      final String userName =
          authorizeProject(contentService, projectId, securityService,
              authToken, "put concepts in workflow", UserRole.USER);

      final Project project = contentService.getProject(projectId);
      contentService.setMolecularActionFlag(false);
      contentService.setLastModifiedBy(userName);
      contentService.setTransactionPerOperation(false);
      contentService.beginTransaction();

      List<Long> lconceptIds = null;
      if (conceptIds != null) {
        lconceptIds = conceptIds;
      } else {
        lconceptIds = contentService.getAllConceptIds(project.getTerminology(),
            project.getVersion(), project.getBranch());
      }

      for (Long id : lconceptIds) {

        final Concept concept = contentService.getConcept(id);
        if (concept == null) {
          throw new Exception("Concept not found");
        }
        if (!concept.getTerminology().equals(project.getTerminology())) {
          throw new Exception("Concept not in project");
        }
        concept.setWorkflowStatus(workflowStatus);
        contentService.updateConcept(concept);
      }
      contentService.commit();

    } catch (Exception e) {
      handleException(e, "trying to add abbreviation ");
      contentService.rollback();
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/concept/workflow/clear")
  @POST
  @ApiOperation(value = "Mark concepts with workflow status", notes = "Marks concepts for workflow status given a list of ids", response = TypeKeyValueJpa.class)
  public void clearReviewWorkflowForProject(
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /concept/workflow/clear");
    final ContentService contentService = new ContentServiceJpa();
    try {
      final String userName =
          authorizeProject(contentService, projectId, securityService,
              authToken, "remove concepts from project", UserRole.USER);

      final Project project = contentService.getProject(projectId);
      contentService.setMolecularActionFlag(false);
      contentService.setLastModifiedBy(userName);
      contentService.setTransactionPerOperation(false);
      contentService.beginTransaction();

      final List<Long> conceptIds = contentService.getAllConceptIds(
          project.getTerminology(), project.getVersion(), project.getBranch());

      for (Long id : conceptIds) {

        final Concept concept = contentService.getConcept(id);
        if (concept == null) {
          throw new Exception("Concept not found");
        }
        if (!concept.getTerminology().equals(project.getTerminology())) {
          throw new Exception("Concept not in project");
        }
        if (concept.getWorkflowStatus().equals(WorkflowStatus.NEEDS_REVIEW)) {
          concept.setWorkflowStatus(WorkflowStatus.NEW);
          contentService.updateConcept(concept);
        }
      }
      contentService.commit();

    } catch (

    Exception e) {
      handleException(e, "trying clear workflow status for concepts in review");
      contentService.rollback();
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  //
  // Term functions
  //

  @Override
  @Path("/term/find")
  @POST
  @ApiOperation(value = "Finds abbreviations", notes = "Finds abbreviation objects", response = TypeKeyValueJpa.class)
  public TypeKeyValueList findTerms(
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Query", required = false) @QueryParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDPr): /term/find, " + query + ", " + ", " + pfs);
    final ProjectService projectService = new ProjectServiceJpa();

    try {
      authorizeProject(projectService, projectId, securityService, authToken,
          "find abbreviations", UserRole.USER);

      TypeKeyValueList list = null;
      final Project project = projectService.getProject(projectId);
      final AbbreviationHandler termHandler = new TermHandler();

      // use query restriction to restrict by computed type
      PfsParameter lpfs = new PfsParameterJpa(pfs);
      lpfs.setQueryRestriction((pfs.getQueryRestriction() == null
          || pfs.getQueryRestriction().isEmpty() ? ""
              : pfs.getQueryRestriction() + " AND ")
          + "type:\"" + termHandler.getAbbrType(project.getTerminology())
          + "\"");

      list = projectService.findTypeKeyValuesForQuery(query, lpfs);

      return list;
    } catch (Exception e) {
      handleException(e, "trying to find abbreviations ");
      return null;
    } finally {
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/term/import")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Import abbreviations", notes = "Import abbreviations for a project from tab-delimited file", response = TypeKeyValueJpa.class)
  public ValidationResult importTerms(
    @ApiParam(value = "Form data header", required = true) @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @ApiParam(value = "Content of terms file", required = true) @FormDataParam("file") InputStream in,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /term/import/" + projectId);
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler handler = new TermHandler();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "import terms", UserRole.USER);
      Project project = projectService.getProject(projectId);

      final ContentService contentService = new ContentServiceJpa();
      final Set<Long> conceptIds = new HashSet<>(
          contentService.getAllConceptIds(project.getTerminology(),
              project.getVersion(), project.getBranch()));

      System.out.println(conceptIds.size());
      System.out.println(conceptIds);

      projectService.setLastModifiedBy(username);
      handler.setService(projectService);
      return handler.importAbbreviationFile(project.getTerminology(), in);

    } catch (

    Exception e) {
      handleException(e, "trying to import terms ");
      return null;
    } finally {
      // NOTE: No need to close, but included for future safety
      handler.close();
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/term/{id}")
  @GET
  @ApiOperation(value = "Get a term", notes = "Gets a term object by id", response = TypeKeyValueJpa.class)
  public TypeKeyValue getTerm(
    @ApiParam(value = "The term id, e.g. 1") @PathParam("id") Long id,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    {
      Logger.getLogger(getClass())
          .info("RESTful call (MLDP, Get): /term/ " + id);
      final ProjectService projectService = new ProjectServiceJpa();
      try {
        authorizeProject(projectService, projectId, securityService, authToken,
            "get term", UserRole.USER);
        return projectService.getTypeKeyValue(id);
      } catch (Exception e) {
        handleException(e, "trying to get term ");
        return null;
      } finally {
        projectService.close();
        securityService.close();
      }
    }
  }

  @Override
  @Path("/term/add")
  @PUT
  @ApiOperation(value = "Add a term", notes = "Adds a term object", response = TypeKeyValueJpa.class)
  public TypeKeyValue addTerm(
    @ApiParam(value = "The term to add") TypeKeyValueJpa typeKeyValue,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, PUT): /term/add " + typeKeyValue);
    final ProjectService projectService = new ProjectServiceJpa();
    final Project project = projectService.getProject(projectId);
    final TermHandler termHandler = new TermHandler();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "add term", UserRole.USER);
      projectService.setLastModifiedBy(username);
      termHandler.setService(projectService);
      typeKeyValue.setType(termHandler.getAbbrType(project.getTerminology()));
      termHandler.updateWorkflowStatus(typeKeyValue);
      return projectService.addTypeKeyValue(typeKeyValue);
    } catch (Exception e) {
      handleException(e, "trying to add term ");
      return null;
    } finally {
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/term/update")
  @POST
  @ApiOperation(value = "Update a term", notes = "Updates a term object", response = TypeKeyValueJpa.class)

  public void updateTerm(
    @ApiParam(value = "The term to add") TypeKeyValueJpa typeKeyValue,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (MLDP, POST): /term/update " + typeKeyValue.toString());
    final ProjectService projectService = new ProjectServiceJpa();
    final TermHandler termHandler = new TermHandler();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "update term", UserRole.USER);
      projectService.setLastModifiedBy(username);
      termHandler.setService(projectService);
      // TODO Decide whether we want update to change workflow status
      // i.e. should updates be set to NEW or NEEDS_REVIEW?
      termHandler.updateWorkflowStatus(typeKeyValue);
      projectService.updateTypeKeyValue(typeKeyValue);
    } catch (Exception e) {
      handleException(e, "trying to update term ");
    } finally {
      projectService.close();
      securityService.close();
    }

  }

  @Override
  @Path("/term/remove/{id}")
  @DELETE
  @ApiOperation(value = "Removes a term", notes = "Removes a term object by id", response = TypeKeyValueJpa.class)
  public void removeTerm(
    @ApiParam(value = "The term to remove") @PathParam("id") Long id,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, DELETE): /term/remove " + id);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "remove term", UserRole.USER);
      projectService.setLastModifiedBy(username);
      projectService.removeTypeKeyValue(id);
    } catch (Exception e) {
      handleException(e, "trying to remove term ");

    } finally {
      projectService.close();
      securityService.close();
    }

  }

  @Override
  @Path("/term/remove")
  @POST
  @ApiOperation(value = "Removes terms", notes = "Removes term objects for id list", response = TypeKeyValueJpa.class)
  public void removeTerms(
    @ApiParam(value = "The term to remove") List<Long> ids,
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /term/remove " + ids);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      final String username = authorizeProject(projectService, projectId,
          securityService, authToken, "remove term", UserRole.USER);
      projectService.setLastModifiedBy(username);
      projectService.setTransactionPerOperation(false);
      projectService.beginTransaction();
      for (Long id : ids) {
        projectService.removeTypeKeyValue(id);
      }
      projectService.commit();
    } catch (Exception e) {
      handleException(e, "trying to remove term ");

    } finally {
      projectService.close();
      securityService.close();
    }

  }

  @Override
  @Path("term/process")
  @POST
  @ApiOperation(value = "Process a term", notes = "Process a term and apply workflow status")
  public ScoredDataContextTupleList processTerm(
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "The term object", required = true) TypeKeyValueJpa term,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (MLDP, GET): /term/process" + projectId + " " + term);
    final ProjectService projectService = new ProjectServiceJpa();
    final MldpService mldpService = new MldpServiceJpa();
    try {
      final String userName = authorizeProject(projectService, projectId,
          securityService, authToken, "process term", UserRole.USER);
      mldpService.setLastModifiedBy(userName);
      // Get input/output contexts from JPA
      return mldpService.processTerm(term);

    } catch (Exception e) {
      handleException(e, "trying to process term");
      return null;
    } finally {
      // NOTE: No need to close, but included for future safety
      mldpService.close();
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("term/process/batch")
  @POST
  @ApiOperation(value = "Process batch of terms", notes = "Process batch of terms for a project")
  public void processTermBatch(
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "Workflow status restriction", required = false) @QueryParam("status") WorkflowStatus status,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, GET): /term/process/all" + projectId);
    final ProjectService projectService = new ProjectServiceJpa();
    final MldpService mldpService = new MldpServiceJpa();
    try {
      final String userName = authorizeProject(projectService, projectId,
          securityService, authToken, "process terms", UserRole.USER);
      final Project project = projectService.getProject(projectId);

      // Get input/output contexts from JPA
      final TypeKeyValueList allTerms = mldpService.findTypeKeyValuesForQuery(
          "type:" + project.getTerminology() + "-TERM", null);

      TypeKeyValueList termsToProcess = new TypeKeyValueListJpa();

      // if status specified, filter
      if (status != null) {
        for (final TypeKeyValue term : termsToProcess.getObjects()) {
          if (term.getWorkflowStatus().equals(status)) {
            termsToProcess.getObjects().add(term);
          }
        }
      } else {
        termsToProcess = allTerms;
      }

      mldpService.setLastModifiedBy(userName);
      mldpService.processTerms(termsToProcess.getObjects());

    } catch (Exception e) {
      handleException(e, "trying to process terms");
    } finally {
      // NOTE: No need to close, but included for future safety
      mldpService.close();
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/term/workflow")
  @POST
  @ApiOperation(value = "Mark terms with workflow status", notes = "Marks terms for workflow status given a list of ids", response = TypeKeyValueJpa.class)
  public void putTermsInWorkflow(
    @ApiParam(value = "The project id, e.g. 1", required = true) @QueryParam("projectId") Long projectId,
    @ApiParam(value = "The list of term ids", required = true) List<Long> termIds,
    @ApiParam(value = "The workflow status, e.g. REVIEW_NEEDED", required = false) @QueryParam("workflowStatus") WorkflowStatus workflowStatus,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (MLDP, POST): /term/workflow " + workflowStatus
            + ", " + termIds);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      final String userName = authorizeProject(projectService, projectId,
          securityService, authToken, "put terms in workflow", UserRole.USER);

      final Project project = projectService.getProject(projectId);
      projectService.setMolecularActionFlag(false);
      projectService.setLastModifiedBy(userName);
      projectService.setTransactionPerOperation(false);
      projectService.beginTransaction();

      final TermHandler handler = new TermHandler();

      List<Long> ltermIds = null;
      if (termIds != null) {
        ltermIds = termIds;
      } else {
        TypeKeyValueList terms = projectService.findTypeKeyValuesForQuery(
            "type:" + handler.getAbbrType(project.getTerminology()), null);
      }

      for (Long id : ltermIds) {

        final TypeKeyValue term = projectService.getTypeKeyValue(id);
        if (term == null) {
          throw new Exception("Term not found");
        }
        if (!term.getType().equals(handler.getAbbrType(project.getTerminology()))) {
          throw new Exception("Term type " + term.getType() + " not valid for project");
        }
        term.setWorkflowStatus(workflowStatus);
        projectService.updateTypeKeyValue(term);
      }
      projectService.commit();

    } catch (Exception e) {
      handleException(e, "trying to add abbreviation ");
      projectService.rollback();
    } finally {
      projectService.close();
      securityService.close();
    }
  }

}
