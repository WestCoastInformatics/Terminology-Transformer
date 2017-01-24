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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesListModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.RxcuiModel;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.jpa.services.handlers.DefaultAbbreviationHandler;
import com.wci.tt.jpa.services.rest.NdcServiceRest;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.handlers.AbbreviationHandler;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.SearchResultList;
import com.wci.umls.server.helpers.StringList;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.SearchResultListJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueListJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.ProjectServiceJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.AtomClass;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.ProjectService;
import com.wci.umls.server.services.SecurityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

/**
 * Implementation the REST Service for NDC-RXNORM transformations.
 */
@Path("/rxnorm")
@Api(value = "/rxnorm")
@SwaggerDefinition(info = @Info(description = "Operations related to NDC, RXCUI, and SPL_SET_ID lookups", title = "NDC Operations", version = "1.0.0"))
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
    @ApiParam(value = "NDC value, e.g. '00143314501'", required = true) @PathParam("ndc") String ndc,
    @ApiParam(value = "History flag, e.g. true/false", required = true) @QueryParam("history") Boolean history,
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

      if (history != null && history) {
        inputContext.getParameters().put("history", "true");
      }
      // Obtain results
      final List<ScoredResult> results =
          service.process(ndc.trim(), inputContext, outputContext);

      // Send empty value on no results
      if (results.size() == 0) {
        return new NdcModel();
      }

      // Otherwise, assume 1 result
      final ScoredResult result = results.get(0);

      if (results.size() != 1) {
        throw new Exception("more than one result in get ndc info");
      }

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
    @ApiParam(value = "RXCUI value, e.g. '351772'", required = true) @PathParam("rxcui") String rxcui,
    @ApiParam(value = "History flag, e.g. true/false", required = true) @QueryParam("history") Boolean history,
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

      if (history != null && history) {
        inputContext.getParameters().put("history", "true");
      }

      // Obtain results
      final List<ScoredResult> results =
          service.process(rxcui.trim(), inputContext, outputContext);

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
  @ApiOperation(value = "Get NDC propertes", notes = "Gets detailed properties for specified NDC.", response = NdcPropertiesModel.class)
  public NdcPropertiesModel getNdcProperties(
    @ApiParam(value = "NDC value, e.g. '00143314501'", required = true) @PathParam("ndc") String ndc,
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
          service.process(ndc.trim(), inputContext, outputContext);

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
    @ApiParam(value = "SPL_SET_ID, e.g. '8d24bacb-feff-4c6a-b8df-625e1435387a'", required = true) @PathParam("splSetId") String splSetId,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Ndc): /spl/" + splSetId + "/ndc/properties");

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
      final List<ScoredResult> results = service
          .process(splSetId.toLowerCase().trim(), inputContext, outputContext);

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

  /* see superclass */
  @Override
  @GET
  @Path("/ndc/autocomplete")
  @ApiOperation(value = "Find autocomplete matches for NDC", notes = "Gets a list of search autocomplete matches for the specified NDC code", response = StringList.class)
  public StringList autocomplete(
    @ApiParam(value = "Query, e.g. 'asp'", required = true) @QueryParam("query") String query,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (NDC): /ndc/autoComplete - " + query);
    final CoordinatorServiceJpa coordinatorService =
        new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "autocomplete NDC",
          UserRole.VIEWER);

      if (ConfigUtility.isEmpty(query)) {
        return new StringList();
      }

      String normalizedQuery =
          query.replaceAll("\\*", "0").replaceAll("\\-", "");

      final String TITLE_EDGE_NGRAM_INDEX = "atoms.edgeNGramName";
      final String TITLE_NGRAM_INDEX = "atoms.nGramName";

      final FullTextEntityManager fullTextEntityManager = Search
          .getFullTextEntityManager(coordinatorService.getEntityManager());
      final QueryBuilder titleQB = fullTextEntityManager.getSearchFactory()
          .buildQueryBuilder().forEntity(ConceptJpa.class).get();

      final Query luceneQuery = titleQB.phrase().withSlop(2)
          .onField(TITLE_NGRAM_INDEX).andField(TITLE_EDGE_NGRAM_INDEX)
          .boostedTo(5).andField("atoms.name").boostedTo(5)
          .sentence(normalizedQuery).createQuery();

      // get latest version
      final Query term1 = new TermQuery(new Term("terminology", "RXNORM"));
      final Query term2 = new TermQuery(new Term("version", coordinatorService
          .getTerminologyLatestVersion("RXNORM").getVersion()));
      final BooleanQuery booleanQuery = new BooleanQuery();
      booleanQuery.add(term1, BooleanClause.Occur.MUST);
      booleanQuery.add(term2, BooleanClause.Occur.MUST);
      booleanQuery.add(luceneQuery, BooleanClause.Occur.MUST);

      final FullTextQuery fullTextQuery = fullTextEntityManager
          .createFullTextQuery(booleanQuery, ConceptJpa.class);

      fullTextQuery.setMaxResults(20);

      @SuppressWarnings("unchecked")
      final List<AtomClass> results = fullTextQuery.getResultList();

      final StringList list = new StringList();
      for (final AtomClass result : results) {

        // RXNORM Search if there are any characters
        if (query.matches(".*[a-zA-Z].*")) {
          list.getObjects().add(result.getName());
        }

        else {
          // Find NDCs matching.
          for (final Atom atom : result.getAtoms()) {
            // exclude duplicates
            if (atom.getTermType().equals("NDC")
                && atom.getName().contains(normalizedQuery)
                && !list.contains(result.getName()))
              list.getObjects().add(atom.getName());
          }
        }
      }
      list.setTotalCount(list.getObjects().size());
      // Limit to 20 results
      if (list.getObjects().size() > 0) {
        list.setObjects(list.getObjects().subList(0,
            Math.min(20, list.getObjects().size() - 1)));
      }
      return list;

    } catch (Exception e) {
      handleException(e, "trying to autocomplete NDC");
      return null;
    } finally {
      coordinatorService.close();
      securityService.close();
    }
  }

  /* see superclass */
  @Override
  @POST
  @Path("/rxcui/search")
  @ApiOperation(value = "Find RxNorm concept", notes = "Finds RxNorm concept matches for query", response = StringList.class)
  public SearchResultList findConcepts(
    @ApiParam(value = "Query, e.g. 'aspirin'", required = true) @QueryParam("query") String query,
    @ApiParam(value = "Pfs Parameter, e.g. '{\"startIndex\":0, \"maxResults\":10}'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (NDC): /rxcui/search - " + query + ", " + pfs);
    final ContentServiceJpa contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find RxNorm concepts",
          UserRole.VIEWER);

      if (ConfigUtility.isEmpty(query)) {
        return new SearchResultListJpa();
      }
      return contentService.findConceptSearchResults("RXNORM",
          contentService.getTerminologyLatestVersion("RXNORM").getVersion(),
          Branch.ROOT, query, pfs);

    } catch (Exception e) {
      handleException(e, "trying to find RxNorm concepts");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /* see superclass */
  @Override
  @POST
  @Path("/ndcs")
  @ApiOperation(value = "Get NDC info", notes = "Gets NDC info and RXCUI history for list of NDCs.", response = NdcModel.class, responseContainer = "List")
  public List<NdcModel> getNdcInfoBatch(
    @ApiParam(value = "A list of NDC vlaues , e.g. '[ \"00247100552\", \"00143314501\" ]'", required = true) List<String> ndcs,
    @ApiParam(value = "History flag, e.g. true/false", required = true) @QueryParam("history") Boolean history,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info("RESTful call (NDC): /ndcs - " + ndcs);
    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "ndc batch info",
          UserRole.VIEWER);

      List<NdcModel> list = new ArrayList<>();
      // Configure contexts
      DataContext inputContext = new DataContextJpa();
      inputContext.setType(DataContextType.CODE);
      inputContext.setTerminology("NDC");
      DataContext outputContext = new DataContextJpa();
      outputContext.setType(DataContextType.INFO_MODEL);
      outputContext.setInfoModelClass(NdcModel.class.getName());

      if (history != null && history) {
        inputContext.getParameters().put("history", "true");
      }

      for (String ndc : ndcs) {

        // Obtain results
        final List<ScoredResult> results =
            service.process(ndc.trim(), inputContext, outputContext);

        // Send emty value on no results
        if (results.size() == 0) {
          return new ArrayList<>();
        }

        // Otherwise, assume 1 result
        final ScoredResult result = results.get(0);

        if (results.size() != 1) {
          throw new Exception("more than one result in get ndc info");
        }

        // Translate tuples into JPA object
        final NdcModel ndcModel = new NdcModel().getModel(result.getValue());
        list.add(ndcModel);
      }
      return list;
    } catch (Exception e) {
      handleException(e, "trying to get ndc batch info");
      return null;
    } finally {
      service.close();
      securityService.close();
    }
  }

  /* see superclass */
  @Override
  @POST
  @Path("/rxcuis")
  @ApiOperation(value = "Get RXCUI info", notes = "Gets RXCUI info and NDC history for list of RXCUIs.", response = NdcModel.class, responseContainer = "List")
  public List<RxcuiModel> getRxcuiInfoBatch(
    @ApiParam(value = "A list of RXCUI values, e.g. '[ \"283420\", \"351772\" ]'", required = true) List<String> rxcuis,
    @ApiParam(value = "History flag, e.g. true/false", required = true) @QueryParam("history") Boolean history,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (NDC): /rxcuis - " + rxcuis);
    final CoordinatorService service = new CoordinatorServiceJpa();
    try {
      authorizeApp(securityService, authToken, "rxcui batch info",
          UserRole.VIEWER);

      List<RxcuiModel> list = new ArrayList<>();
      // Configure contexts
      DataContext inputContext = new DataContextJpa();
      inputContext.setType(DataContextType.CODE);
      inputContext.setTerminology("RXNORM");
      DataContext outputContext = new DataContextJpa();
      outputContext.setType(DataContextType.INFO_MODEL);
      outputContext.setInfoModelClass(RxcuiModel.class.getName());

      if (history != null && history) {
        inputContext.getParameters().put("history", "true");
      }

      for (String ndc : rxcuis) {

        // Obtain results
        final List<ScoredResult> results =
            service.process(ndc.trim(), inputContext, outputContext);

        // Send emty value on no results
        if (results.size() == 0) {
          return new ArrayList<>();
        }

        // Otherwise, assume 1 result
        final ScoredResult result = results.get(0);

        // Translate tuples into JPA object
        final RxcuiModel ndcModel =
            new RxcuiModel().getModel(result.getValue());
        list.add(ndcModel);
      }
      return list;
    } catch (Exception e) {
      handleException(e, "trying to get rxcui batch info");
      return null;
    } finally {
      service.close();
      securityService.close();
    }
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
          "import abbreviations", UserRole.VIEWER);
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
          UserRole.ADMINISTRATOR);
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

  @GET
  @Override
  @Produces("application/octet-stream")
  @Path("/export/{type}")
  @ApiOperation(value = "Export abbreviations", notes = "Exports abbreviations for type as comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public InputStream exportAbbreviationsFile(
    @ApiParam(value = "Type of abbreviation, e.g. medAbbr", required = true) @PathParam("type") String type,
    @ApiParam(value = "Flag to export only abbreviations not flagged for review", required = false) @QueryParam("readyOnly") boolean readyOnly,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /find");
    final ProjectService projectService = new ProjectServiceJpa();
    final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
    try {
      authorizeApp(securityService, authToken, "export abbreviations",
          UserRole.USER);
      abbrHandler.setService(projectService);
      return abbrHandler.exportAbbreviationFile(type, readyOnly);
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
      authorizeApp(securityService, authToken, "export abbreviations",
          UserRole.USER);
      abbrHandler.setService(projectService);
      TypeKeyValue abbr = projectService.getTypeKeyValue(id);
      return abbrHandler.getReviewForAbbreviation(abbr);
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
      authorizeApp(securityService, authToken, "export abbreviations",
          UserRole.USER);
      abbrHandler.setService(projectService);
      List<TypeKeyValue> abbrs = new ArrayList<>();
      for (Long id : ids) {
        abbrs.add(projectService.getTypeKeyValue(id));
      }
      return abbrHandler.getReviewForAbbreviations(abbrs);
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
            UserRole.VIEWER);
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
          "add abbreviation", UserRole.VIEWER);
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
          "update abbreviation", UserRole.VIEWER);
      projectService.setLastModifiedBy(username);
      abbrHandler.setService(projectService);
      // TODO Decide whether we want update to change workflow status
      // i.e. should updates be set to NEW or NEEDS_REVIEW?
      // abbrHandler.updateWorkflowStatus(typeKeyValue);
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
          "remove abbreviation", UserRole.VIEWER);
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
  @Path("/find")
  @POST
  @ApiOperation(value = "Finds abbreviations", notes = "Finds abbreviation objects", response = TypeKeyValueJpa.class)
  public TypeKeyValueList findAbbreviations(
    @ApiParam(value = "Query", required = false) @QueryParam("query") String query,
    @ApiParam(value = "Filter type", required = false) @QueryParam("filter") String filter,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Project): /find, " + query + " " + pfs);
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find abbreviations",
          UserRole.VIEWER);

      TypeKeyValueList list = null;
      
      // if filter supplied, retrieve all results and pass to handler
      if (filter != null) {
        PfsParameter lpfs = new PfsParameterJpa(pfs);
        lpfs.setMaxResults(-1);
        lpfs.setStartIndex(-1);
        list = projectService.findTypeKeyValuesForQuery(query, lpfs);
        final AbbreviationHandler abbrHandler = new DefaultAbbreviationHandler();
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
