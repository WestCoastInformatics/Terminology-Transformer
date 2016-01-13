/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.io.File;

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

import com.wci.tt.UserRole;
import com.wci.tt.helpers.ConfigUtility;
import com.wci.tt.helpers.SearchResultList;
import com.wci.tt.helpers.StringList;
import com.wci.tt.helpers.content.Branch;
import com.wci.tt.helpers.content.CodeList;
import com.wci.tt.helpers.content.ConceptList;
import com.wci.tt.helpers.content.DescriptorList;
import com.wci.tt.helpers.content.RelationshipList;
import com.wci.tt.helpers.content.SubsetList;
import com.wci.tt.helpers.content.SubsetMemberList;
import com.wci.tt.helpers.content.Tree;
import com.wci.tt.helpers.content.TreeList;
import com.wci.tt.helpers.content.TreePositionList;
import com.wci.tt.jpa.algo.LabelSetMarkedParentAlgorithm;
import com.wci.tt.jpa.algo.LuceneReindexAlgorithm;
import com.wci.tt.jpa.algo.RemoveTerminologyAlgorithm;
import com.wci.tt.jpa.algo.RrfFileSorter;
import com.wci.tt.jpa.algo.RrfLoaderAlgorithm;
import com.wci.tt.jpa.algo.RrfReaders;
import com.wci.tt.jpa.algo.TransitiveClosureAlgorithm;
import com.wci.tt.jpa.algo.TreePositionAlgorithm;
import com.wci.tt.jpa.content.CodeJpa;
import com.wci.tt.jpa.content.ConceptJpa;
import com.wci.tt.jpa.content.ConceptRelationshipJpa;
import com.wci.tt.jpa.content.DescriptorJpa;
import com.wci.tt.jpa.content.LexicalClassJpa;
import com.wci.tt.jpa.content.StringClassJpa;
import com.wci.tt.jpa.helpers.PfsParameterJpa;
import com.wci.tt.jpa.helpers.PfscParameterJpa;
import com.wci.tt.jpa.helpers.SearchResultListJpa;
import com.wci.tt.jpa.helpers.content.CodeListJpa;
import com.wci.tt.jpa.helpers.content.ConceptListJpa;
import com.wci.tt.jpa.helpers.content.DescriptorListJpa;
import com.wci.tt.jpa.helpers.content.RelationshipListJpa;
import com.wci.tt.jpa.helpers.content.SubsetListJpa;
import com.wci.tt.jpa.helpers.content.SubsetMemberListJpa;
import com.wci.tt.jpa.helpers.content.TreeJpa;
import com.wci.tt.jpa.helpers.content.TreeListJpa;
import com.wci.tt.jpa.helpers.content.TreePositionListJpa;
import com.wci.tt.jpa.services.ContentServiceJpa;
import com.wci.tt.jpa.services.MetadataServiceJpa;
import com.wci.tt.jpa.services.SecurityServiceJpa;
import com.wci.tt.jpa.services.rest.ContentServiceRest;
import com.wci.tt.model.content.Code;
import com.wci.tt.model.content.ComponentHasAttributes;
import com.wci.tt.model.content.ComponentHasAttributesAndName;
import com.wci.tt.model.content.Concept;
import com.wci.tt.model.content.ConceptRelationship;
import com.wci.tt.model.content.ConceptSubset;
import com.wci.tt.model.content.Descriptor;
import com.wci.tt.model.content.LexicalClass;
import com.wci.tt.model.content.Relationship;
import com.wci.tt.model.content.StringClass;
import com.wci.tt.model.content.Subset;
import com.wci.tt.model.content.SubsetMember;
import com.wci.tt.model.content.TreePosition;
import com.wci.tt.model.meta.IdType;
import com.wci.tt.model.meta.Terminology;
import com.wci.tt.services.ContentService;
import com.wci.tt.services.MetadataService;
import com.wci.tt.services.SecurityService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

// TODO: Auto-generated Javadoc
/**
 * REST implementation for {@link ContentServiceRest}..
 */
@Path("/content")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Api(value = "/content", description = "Operations to retrieve RF2 content for a terminology.")
public class ContentServiceRestImpl extends RootServiceRestImpl implements
    ContentServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link ContentServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public ContentServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /**
   * Lucene reindex.
   *
   * @param indexedObjects the indexed objects
   * @param authToken the auth token
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/reindex")
  @ApiOperation(value = "Reindexes specified objects", notes = "Recomputes lucene indexes for the specified comma-separated objects")
  public void luceneReindex(
    @ApiParam(value = "Comma-separated list of objects to reindex, e.g. ConceptJpa (optional)", required = false) String indexedObjects,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful POST call (Content): /reindex "
            + (indexedObjects == null ? "with no objects specified"
                : "with specified objects " + indexedObjects));

    // Track system level information
    long startTimeOrig = System.nanoTime();
    LuceneReindexAlgorithm algo = new LuceneReindexAlgorithm();
    try {
      authorizeApp(securityService, authToken, "reindex", UserRole.ADMIN);
      algo.setIndexedObjects(indexedObjects);
      algo.compute();
      algo.close();
      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to reindex");
    } finally {
      algo.close();
      securityService.close();
    }

  }

  /**
   * Compute transitive closure.
   *
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/terminology/closure/compute/{terminology}/{version}")
  @ApiOperation(value = "Computes terminology transitive closure", notes = "Computes transitive closure for the latest version of the specified terminology")
  public void computeTransitiveClosure(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)

  throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (Content): /terminology/closure/compute/"
            + terminology + "/" + version);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
    MetadataService service = new MetadataServiceJpa();
    try {
      authorizeApp(securityService, authToken, "compute transitive closure",
          UserRole.ADMIN);

      // Compute transitive closure
      Logger.getLogger(getClass()).info(
          "  Compute transitive closure for  " + terminology + "/" + version);
      algo.setTerminology(terminology);
      algo.setVersion(version);
      algo.setIdType(service.getTerminology(terminology, version)
          .getOrganizingClassType());
      algo.reset();
      algo.compute();

      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to compute transitive closure");
    } finally {
      algo.close();
      service.close();
      securityService.close();
    }
  }

  /**
   * Compute tree positions.
   *
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/terminology/treepos/compute/{terminology}/{version}")
  @ApiOperation(value = "Computes terminology tree positions", notes = "Computes tree positions for the latest version of the specified terminology")
  public void computeTreePositions(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)

  throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (Content): /terminology/treepos/compute/"
            + terminology + "/" + version);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    TreePositionAlgorithm algo = new TreePositionAlgorithm();
    MetadataService service = new MetadataServiceJpa();
    try {
      authorizeApp(securityService, authToken, "compute tree positions ",
          UserRole.ADMIN);

      // Compute tree positions
      Logger.getLogger(getClass()).info(
          "  Compute tree positions for " + terminology + "/" + version);
      algo.setTerminology(terminology);
      algo.setVersion(version);
      algo.setIdType(service.getTerminology(terminology, version)
          .getOrganizingClassType());
      algo.setCycleTolerant(true);
      // compute "semantic types" for concept hierarchies
      if (algo.getIdType() == IdType.CONCEPT) {
        algo.setComputeSemanticType(true);
      }

      algo.reset();
      algo.compute();

      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to compute tree positions");
    } finally {
      algo.close();
      service.close();
      securityService.close();
    }
  }

  /**
   * Load terminology rrf.
   *
   * @param terminology the terminology
   * @param version the version
   * @param singleMode the single mode
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @PUT
  @Path("/terminology/load/rrf/{singleMode}/{terminology}/{version}")
  @Consumes(MediaType.TEXT_PLAIN)
  @ApiOperation(value = "Load all terminologies from an RRF directory", notes = "Loads terminologies from an RRF directory for specified terminology and version")
  public void loadTerminologyRrf(
    @ApiParam(value = "Terminology, e.g. UMLS", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Single mode, e.g. false", required = true) @PathParam("singleMode") boolean singleMode,
    @ApiParam(value = "RRF input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info(
            "RESTful POST call (Content): /terminology/load/rrf/umls/"
                + terminology + "/" + version + " from input directory "
                + inputDir);

    // Track system level information
    long startTimeOrig = System.nanoTime();
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "load RRF", UserRole.ADMIN);

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Sort files - not really needed because files are already sorted
      Logger.getLogger(getClass()).info("  Sort RRF Files");
      RrfFileSorter sorter = new RrfFileSorter();
      sorter.setRequireAllFiles(true);
      // File outputDir = new File(inputDirFile, "/RRF-sorted-temp/");
      // sorter.sortFiles(inputDirFile, outputDir);
      String releaseVersion = sorter.getFileVersion(inputDirFile);
      Logger.getLogger(getClass()).info("  releaseVersion = " + releaseVersion);

      // Open readers - just open original RRF
      RrfReaders readers = new RrfReaders(inputDirFile);
      readers.openOriginalReaders();

      // Load RRF
      RrfLoaderAlgorithm algorithm = new RrfLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setVersion(version);
      algorithm.setSingleMode(singleMode);
      algorithm.setReleaseVersion(releaseVersion);
      algorithm.setReaders(readers);
      algorithm.compute();
      algorithm.close();
      algorithm = null;

      // Compute transitive closure
      // Obtain each terminology and run transitive closure on it with the
      // correct id type
      // Refresh caches after metadata has changed in loader
      contentService.refreshCaches();
      for (Terminology t : contentService.getTerminologyLatestVersions()
          .getObjects()) {
        // Only compute for organizing class types
        if (t.getOrganizingClassType() != null) {
          TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
          algo.setTerminology(t.getTerminology());
          algo.setVersion(t.getVersion());
          algo.setIdType(t.getOrganizingClassType());
          // some terminologies may have cycles, allow these for now.
          algo.setCycleTolerant(true);
          algo.compute();
          algo.close();
        }
      }

      // Compute tree positions
      // Refresh caches after metadata has changed in loader
      for (Terminology t : contentService.getTerminologyLatestVersions()
          .getObjects()) {
        // Only compute for organizing class types
        if (t.getOrganizingClassType() != null) {
          TreePositionAlgorithm algo = new TreePositionAlgorithm();
          algo.setTerminology(t.getTerminology());
          algo.setVersion(t.getVersion());
          algo.setIdType(t.getOrganizingClassType());
          // some terminologies may have cycles, allow these for now.
          algo.setCycleTolerant(true);
          // compute "semantic types" for concept hierarchies
          if (t.getOrganizingClassType() == IdType.CONCEPT) {
            algo.setComputeSemanticType(true);
          }
          algo.compute();
          algo.close();
        }
      }

      // Compute label sets - after transitive closure
      // for each subset, compute the label set
      for (Terminology t : contentService.getTerminologyLatestVersions()
          .getObjects()) {
        for (Subset subset : contentService.getConceptSubsets(
            t.getTerminology(), t.getVersion(), Branch.ROOT).getObjects()) {
          final ConceptSubset conceptSubset = (ConceptSubset) subset;
          if (conceptSubset.isLabelSubset()) {
            Logger.getLogger(getClass()).info(
                "  Create label set for subset = " + subset);
            LabelSetMarkedParentAlgorithm algo3 =
                new LabelSetMarkedParentAlgorithm();
            algo3.setSubset(conceptSubset);
            algo3.compute();
            algo3.close();
          }
        }
      }
      // Clean-up

      ConfigUtility
          .deleteDirectory(new File(inputDirFile, "/RRF-sorted-temp/"));

      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load terminology from RRF directory");
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Load terminology rf2 delta.
   *
   * @param terminology the terminology
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @PUT
  @Path("/terminology/load/rf2/delta/{terminology}")
  @Consumes(MediaType.TEXT_PLAIN)
  @ApiOperation(value = "Loads terminology RF2 delta from directory", notes = "Loads terminology RF2 delta from directory for specified terminology and version")
  public void loadTerminologyRf2Delta(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "RF2 input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (Content): /terminology/load/rf2/delta/"
            + terminology + " from input directory " + inputDir);
  }

  /**
   * Load terminology rf2 snapshot.
   *
   * @param terminology the terminology
   * @param version the version
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @PUT
  @Path("/terminology/load/rf2/snapshot/{terminology}/{version}")
  @Consumes({
    MediaType.TEXT_PLAIN
  })
  @ApiOperation(value = "Loads terminology RF2 snapshot from directory", notes = "Loads terminology RF2 snapshot from directory for specified terminology and version")
  public void loadTerminologyRf2Snapshot(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "RF2 input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info(
            "RESTful POST call (Content): /terminology/load/rf2/snapshot/"
                + terminology + "/" + version + " from input directory "
                + inputDir);

  }

  /**
   * Load terminology rf2 full.
   *
   * @param terminology the terminology
   * @param version the version
   * @param inputDir the input dir
   * @param authToken the auth token
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @PUT
  @Path("/terminology/load/rf2/full/{terminology}/{version}")
  @Consumes({
    MediaType.TEXT_PLAIN
  })
  @ApiOperation(value = "Loads terminology RF2 full from directory", notes = "Loads terminology RF2 full from directory for specified terminology and version")
  public void loadTerminologyRf2Full(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "RF2 input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass())
        .info(
            "RESTful POST call (Content): /terminology/load/rf2/full/"
                + terminology + "/" + version + " from input directory "
                + inputDir);
  }


  @Override
  @PUT
  @Path("/terminology/load/claml/{terminology}/{version}")
  @Consumes({
    MediaType.TEXT_PLAIN
  })
  @ApiOperation(value = "Loads ClaML terminology from file", notes = "Loads terminology from ClaML file, assigning specified version")
  public void loadTerminologyClaml(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "ClaML input file", required = true) String inputFile,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (Content): /terminology/load/claml/" + terminology
            + "/" + version + " from input file " + inputFile);
  }

  /**
   * Load terminology owl.
   *
   * @param terminology the terminology
   * @param version the version
   * @param inputFile the input file
   * @param authToken the auth token
   * @throws Exception the exception
   */
  @Override
  @PUT
  @Path("/terminology/load/owl/{terminology}/{version}")
  @Consumes({
    MediaType.TEXT_PLAIN
  })
  @ApiOperation(value = "Loads Owl terminology from file", notes = "Loads terminology from Owl file, assigning specified version")
  public void loadTerminologyOwl(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Owl input file", required = true) String inputFile,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful POST call (Content): /terminology/load/owl/" + terminology
            + "/" + version + " from input file " + inputFile);

    /*
    // Track system level information
    long startTimeOrig = System.nanoTime();

    OwlLoaderAlgorithm algo = new OwlLoaderAlgorithm();
    TransitiveClosureAlgorithm algo2 = new TransitiveClosureAlgorithm();
    TreePositionAlgorithm algo3 = new TreePositionAlgorithm();
    try {
      authorizeApp(securityService, authToken, "loading owl",
          UserRole.ADMIN);

      // Load snapshot
      Logger.getLogger(getClass()).info("Load Owl data from " + inputFile);
      algo.setTerminology(terminology);
      algo.setVersion(version);
      algo.setInputFile(inputFile);
      algo.compute();

      MetadataService service = new MetadataServiceJpa();
      service.refreshCaches();

      // Let service begin its own transaction
      Logger.getLogger(getClass()).info("Start computing transtive closure");
      algo2.setIdType(IdType.CONCEPT);
      algo2.setCycleTolerant(false);
      algo2.setTerminology(terminology);
      algo2.setVersion(version);
      algo2.compute();
      algo2.close();

      // compute tree positions
      algo3.setCycleTolerant(false);
      algo3.setIdType(IdType.CONCEPT);
      algo3.setComputeSemanticType(true);
      algo3.setTerminology(terminology);
      algo3.setVersion(version);
      algo3.compute();
      algo3.close();

      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");

    } catch (Exception e) {
      handleException(e, "trying to load terminology from Owl file");
    } finally {
      algo.close();
      algo2.close();
      securityService.close();
    }
  */
  }

  /**
   * Removes the terminology.
   *
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return true, if successful
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @DELETE
  @Path("/terminology/remove/{terminology}/{version}")
  @ApiOperation(value = "Remove a terminology", notes = "Removes all elements for a specified terminology and version")
  public boolean removeTerminology(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful GET call (Content): /terminology/remove/" + terminology + "/"
            + version);

    // Track system level information
    long startTimeOrig = System.nanoTime();

    RemoveTerminologyAlgorithm algo = new RemoveTerminologyAlgorithm();
    MetadataService service = new MetadataServiceJpa();
    try {
      authorizeApp(securityService, authToken, "remove terminology",
          UserRole.ADMIN);

      // Remove terminology
      Logger.getLogger(getClass()).info(
          "  Remove terminology for  " + terminology + "/" + version);
      algo.setTerminology(terminology);
      algo.setVersion(version);
      algo.compute();
      algo.close();

      // Final logging messages
      Logger.getLogger(getClass()).info(
          "      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("done ...");
      return true;

    } catch (Exception e) {
      handleException(e, "trying to remove terminology");
      return false;
    } finally {
      algo.close();
      service.close();
      securityService.close();
    }
  }

  /**
   * Gets the concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the concept
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/cui/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get concept by id, terminology, and version", notes = "Get the root branch concept matching the specified parameters", response = ConceptJpa.class)
  public Concept getConcept(
    @ApiParam(value = "Concept terminology id, e.g. C0000039", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. UMLS", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version + "/"
            + terminologyId);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "retrieve the concept",
          UserRole.VIEWER);

      Concept concept =
          contentService.getConcept(terminologyId, terminology, version,
              Branch.ROOT);

      if (concept != null) {
        contentService.getGraphResolutionHandler(terminology).resolve(concept);
        concept.setAtoms(contentService.getComputePreferredNameHandler(
            concept.getTerminology()).sortByPreference(concept.getAtoms()));
      }
      return concept;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a concept");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find concepts for query.
   *
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfsc the pfsc
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui/{terminology}/{version}")
  @ApiOperation(value = "Find concepts matching a search query", notes = "Gets a list of search results that match the lucene query for the root branch", response = SearchResultListJpa.class)
  public SearchResultList findConceptsForQuery(
    @ApiParam(value = "Terminology, e.g. UMLS", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query, e.g. 'aspirin'", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFSC Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfscParameterJpa pfsc,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version
            + "?query=" + queryStr + " with PFS parameter "
            + (pfsc == null ? "empty" : pfsc.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find concepts by query",
          UserRole.VIEWER);
      SearchResultList sr =
          contentService.findConceptsForQuery(terminology, version,
              Branch.ROOT, queryStr, pfsc);
      return sr;

    } catch (Exception e) {
      handleException(e, "trying to find the concepts by query");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find concepts for general query.
   *
   * @param query the query
   * @param jql the jql
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui")
  @ApiOperation(value = "Find concepts matching a lucene or jql search query", notes = "Gets a list of search results that match the lucene or jql query for the root branch", response = SearchResultListJpa.class)
  public SearchResultList findConceptsForGeneralQuery(
    @ApiParam(value = "Lucene Query", required = true) @QueryParam("query") String query,
    @ApiParam(value = "HQL Query", required = true) @QueryParam("jql") String jql,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;
    String jqlStr = jql == null ? "" : jql;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui?" + "query=" + queryStr + "&jql="
            + jqlStr + " with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find concepts by query",
          UserRole.VIEWER);

      SearchResultList sr =
          contentService.findConceptsForGeneralQuery(queryStr, jqlStr,
              Branch.ROOT, pfs);
      return sr;

    } catch (Exception e) {
      handleException(e, "trying to find the concepts by query");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find codes for general query.
   *
   * @param query the query
   * @param jql the jql
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/code")
  @ApiOperation(value = "Find codes matching a lucene or jql search query", notes = "Gets a list of search results that match the lucene or jql query for the root branch", response = SearchResultListJpa.class)
  public SearchResultList findCodesForGeneralQuery(
    @ApiParam(value = "Lucene Query", required = true) @QueryParam("query") String query,
    @ApiParam(value = "HQL Query", required = true) @QueryParam("jql") String jql,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;
    String jqlStr = jql == null ? "" : jql;
    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code?" + "query=" + queryStr + "&jql="
            + jqlStr + " with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find codes by query",
          UserRole.VIEWER);

      SearchResultList sr =
          contentService.findCodesForGeneralQuery(queryStr, jqlStr,
              Branch.ROOT, pfs);
      return sr;

    } catch (Exception e) {
      handleException(e, "trying to find the codes by query");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Autocomplete concepts.
   *
   * @param terminology the terminology
   * @param version the version
   * @param searchTerm the search term
   * @param authToken the auth token
   * @return the string list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/cui/{terminology}/{version}/autocomplete/{searchTerm}")
  @ApiOperation(value = "Find autocomplete matches for concept searches", notes = "Gets a list of search autocomplete matches for the specified search term", response = StringList.class)
  public StringList autocompleteConcepts(
    @ApiParam(value = "Terminology, e.g. UMLS", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Search term, e.g. 'sul'", required = true) @PathParam("searchTerm") String searchTerm,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version
            + "/autocomplete/" + searchTerm);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find concepts by query",
          UserRole.VIEWER);

      return contentService.autocompleteConcepts(terminology, version,
          searchTerm);

    } catch (Exception e) {
      handleException(e, "trying to autocomplete for concepts");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Gets the descriptor.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the descriptor
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/dui/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get descriptor by id, terminology, and version", notes = "Get the root branch descriptor matching the specified parameters", response = DescriptorJpa.class)
  public Descriptor getDescriptor(
    @ApiParam(value = "Descriptor terminology id, e.g. D003933", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Descriptor terminology name, e.g. MSH", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Descriptor terminology version, e.g. 2015_2014_09_08", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version + "/"
            + terminologyId);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "retrieve the descriptor",
          UserRole.VIEWER);

      Descriptor descriptor =
          contentService.getDescriptor(terminologyId, terminology, version,
              Branch.ROOT);

      if (descriptor != null) {
        contentService.getGraphResolutionHandler(terminology).resolve(
            descriptor);
        descriptor.setAtoms(contentService.getComputePreferredNameHandler(
            descriptor.getTerminology())
            .sortByPreference(descriptor.getAtoms()));

      }
      return descriptor;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a descriptor");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find descriptors for query.
   *
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfsc the pfsc
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/dui/{terminology}/{version}")
  @ApiOperation(value = "Find descriptors matching a search query", notes = "Gets a list of search results that match the lucene query for the root branch", response = SearchResultListJpa.class)
  public SearchResultList findDescriptorsForQuery(
    @ApiParam(value = "Descriptor terminology name, e.g. MSH", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Descriptor terminology version, e.g. 2015_2014_09_08", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query, e.g. 'aspirin'", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFSC Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfscParameterJpa pfsc,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version
            + "?query=" + queryStr + " with PFS parameter "
            + (pfsc == null ? "empty" : pfsc.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find descriptors by query",
          UserRole.VIEWER);

      SearchResultList sr =
          contentService.findDescriptorsForQuery(terminology, version,
              Branch.ROOT, queryStr, pfsc);
      return sr;

    } catch (Exception e) {
      handleException(e, "trying to find the descriptors by query");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find descriptors for general query.
   *
   * @param query the query
   * @param jql the jql
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/dui/")
  @ApiOperation(value = "Find descriptors matching a lucene or jql search query", notes = "Gets a list of search results that match the lucene or jql query for the root branch", response = SearchResultListJpa.class)
  public SearchResultList findDescriptorsForGeneralQuery(
    @ApiParam(value = "Lucene Query", required = true) @QueryParam("query") String query,
    @ApiParam(value = "HQL Query", required = true) @QueryParam("jql") String jql,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;
    String jqlStr = jql == null ? "" : jql;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui" + "?query=" + queryStr + "&jql="
            + jqlStr + " with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find concepts by query",
          UserRole.VIEWER);

      SearchResultList sr =
          contentService.findDescriptorsForGeneralQuery(queryStr, jqlStr,
              Branch.ROOT, pfs);
      return sr;

    } catch (Exception e) {
      handleException(e, "trying to find the concepts by query");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Autocomplete descriptors.
   *
   * @param terminology the terminology
   * @param version the version
   * @param searchTerm the search term
   * @param authToken the auth token
   * @return the string list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/dui/{terminology}/{version}/autocomplete/{searchTerm}")
  @ApiOperation(value = "Find autocomplete matches for descriptor searches", notes = "Gets a list of search autocomplete matches for the specified search term", response = StringList.class)
  public StringList autocompleteDescriptors(
    @ApiParam(value = "Terminology, e.g. MSH", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2015_2014_09_08", required = true) @PathParam("version") String version,
    @ApiParam(value = "Search term, e.g. 'sul'", required = true) @PathParam("searchTerm") String searchTerm,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version
            + "/autocomplete/" + searchTerm);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find descriptors by query",
          UserRole.VIEWER);

      return contentService.autocompleteDescriptors(terminology, version,
          searchTerm);

    } catch (Exception e) {
      handleException(e, "trying to autocomplete for descriptors");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Gets the code.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the code
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/code/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get code by id, terminology, and version", notes = "Get the root branch code matching the specified parameters", response = CodeJpa.class)
  public Code getCode(
    @ApiParam(value = "Code terminology id, e.g. U002135", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Code terminology name, e.g. MTH", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Code terminology version, e.g. 2014AB", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version + "/"
            + terminologyId);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "retrieve the code",
          UserRole.VIEWER);

      Code code =
          contentService.getCode(terminologyId, terminology, version,
              Branch.ROOT);

      if (code != null) {
        contentService.getGraphResolutionHandler(terminology).resolve(code);
        code.setAtoms(contentService.getComputePreferredNameHandler(
            code.getTerminology()).sortByPreference(code.getAtoms()));

      }
      return code;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a code");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find codes for query.
   *
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfsc the pfsc
   * @param authToken the auth token
   * @return the search result list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/code/{terminology}/{version}")
  @ApiOperation(value = "Find codes matching a search query", notes = "Gets a list of search results that match the lucene query for the root branch", response = SearchResultListJpa.class)
  public SearchResultList findCodesForQuery(
    @ApiParam(value = "Code terminology name, e.g. MTH", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Code terminology version, e.g. 2014AB", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query, e.g. 'aspirin'", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFSC Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfscParameterJpa pfsc,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version
            + "?query=" + queryStr + " with PFS parameter "
            + (pfsc == null ? "empty" : pfsc.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find codes by query",
          UserRole.VIEWER);

      SearchResultList sr =
          contentService.findCodesForQuery(terminology, version, Branch.ROOT,
              queryStr, pfsc);
      return sr;

    } catch (Exception e) {
      handleException(e, "trying to find the codes by query");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Autocomplete codes.
   *
   * @param terminology the terminology
   * @param version the version
   * @param searchTerm the search term
   * @param authToken the auth token
   * @return the string list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/code/{terminology}/{version}/autocomplete/{searchTerm}")
  @ApiOperation(value = "Find autocomplete matches for code searches", notes = "Gets a list of search autocomplete matches for the specified search term", response = StringList.class)
  public StringList autocompleteCodes(
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Search term, e.g. 'sul'", required = true) @PathParam("searchTerm") String searchTerm,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version
            + "/autocomplete/" + searchTerm);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find code by query",
          UserRole.VIEWER);
      return contentService.autocompleteCodes(terminology, version, searchTerm);

    } catch (Exception e) {
      handleException(e, "trying to autocomplete for codes");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Gets the lexical class.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the lexical class
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/lui/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get lexical class by id, terminology, and version", notes = "Get the root branch lexical class matching the specified parameters", response = LexicalClassJpa.class)
  public LexicalClass getLexicalClass(
    @ApiParam(value = "Lexical class terminology id, e.g. L0356926", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Lexical class terminology name, e.g. UMLS", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Lexical class terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /lui/" + terminology + "/" + version + "/"
            + terminologyId);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "retrieve the lexical class",
          UserRole.VIEWER);

      LexicalClass lexicalClass =
          contentService.getLexicalClass(terminologyId, terminology, version,
              Branch.ROOT);

      if (lexicalClass != null) {
        contentService.getGraphResolutionHandler(terminology).resolve(
            lexicalClass);
        lexicalClass.setAtoms(contentService.getComputePreferredNameHandler(
            lexicalClass.getTerminology()).sortByPreference(
            lexicalClass.getAtoms()));

      }
      return lexicalClass;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a lexicalClass");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Gets the string class.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the string class
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/sui/{terminology}/{version}/{terminologyId}")
  @ApiOperation(value = "Get string class by id, terminology, and version", notes = "Get the root branch string class matching the specified parameters", response = StringClassJpa.class)
  public StringClass getStringClass(
    @ApiParam(value = "String class terminology id, e.g. S0356926", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "String class terminology name, e.g. UMLS", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "String class terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /sui/" + terminology + "/" + version + "/"
            + terminologyId);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "retrieve the string class",
          UserRole.VIEWER);

      StringClass stringClass =
          contentService.getStringClass(terminologyId, terminology, version,
              Branch.ROOT);

      if (stringClass != null) {
        contentService.getGraphResolutionHandler(terminology).resolve(
            stringClass);
        stringClass.setAtoms(contentService.getComputePreferredNameHandler(
            stringClass.getTerminology()).sortByPreference(
            stringClass.getAtoms()));
      }
      return stringClass;
    } catch (Exception e) {
      handleException(e, "trying to retrieve a stringClass");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find ancestor concepts.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param parentsOnly the parents only
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the concept list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui/{terminology}/{version}/{terminologyId}/ancestors/{parentsOnly}")
  @ApiOperation(value = "Find ancestor concepts", notes = "Gets a list of ancestor concepts", response = ConceptListJpa.class)
  public ConceptList findAncestorConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Children only flag, e.g. true", required = true) @PathParam("parentsOnly") boolean parentsOnly,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version + "/"
            + terminologyId + "/ancestors with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find ancestor concepts",
          UserRole.VIEWER);

      ConceptList list =
          contentService.findAncestorConcepts(terminologyId, terminology,
              version, parentsOnly, Branch.ROOT, pfs);

      for (Concept concept : list.getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(concept);
      }

      return list;

    } catch (Exception e) {
      handleException(e, "trying to find the ancestor concepts");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find descendant concepts.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param childrenOnly the children only
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the concept list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui/{terminology}/{version}/{terminologyId}/descendants/{childrenOnly}")
  @ApiOperation(value = "Find descendant concepts", notes = "Gets a list of descendant concepts", response = ConceptListJpa.class)
  public ConceptList findDescendantConcepts(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Children only flag, e.g. true", required = true) @PathParam("childrenOnly") boolean childrenOnly,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version + "/"
            + terminologyId + "/descendants with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find descendant concepts",
          UserRole.VIEWER);

      ConceptList list =
          contentService.findDescendantConcepts(terminologyId, terminology,
              version, childrenOnly, Branch.ROOT, pfs);

      for (Concept concept : list.getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(concept);
      }

      return list;

    } catch (Exception e) {
      handleException(e, "trying to find the descendant concepts");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find ancestor descriptors.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param parentsOnly the parents only
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the descriptor list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/dui/{terminology}/{version}/{terminologyId}/ancestors/{parentsOnly}")
  @ApiOperation(value = "Find ancestor descriptors", notes = "Gets a list of ancestor descriptors", response = DescriptorListJpa.class)
  public DescriptorList findAncestorDescriptors(
    @ApiParam(value = "Descriptor terminology id, e.g. D003423", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology, e.g. MSH", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2015_2014_09_08", required = true) @PathParam("version") String version,
    @ApiParam(value = "Children only flag, e.g. true", required = true) @PathParam("parentsOnly") boolean parentsOnly,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version
            + terminologyId + "/ancestors with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find ancestor descriptors",
          UserRole.VIEWER);

      DescriptorList list =
          contentService.findAncestorDescriptors(terminologyId, terminology,
              version, parentsOnly, Branch.ROOT, pfs);

      for (Descriptor descriptor : list.getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(
            descriptor);
      }

      return list;
    } catch (Exception e) {
      handleException(e, "trying to find the ancestor descriptors");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find descendant descriptors.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param childrenOnly the children only
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the descriptor list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/dui/{terminology}/{version}/{terminologyId}/descendants/{childrenOnly}")
  @ApiOperation(value = "Find descendant descriptors", notes = "Gets a list of descendant descriptors", response = DescriptorListJpa.class)
  public DescriptorList findDescendantDescriptors(
    @ApiParam(value = "Descriptor terminology id, e.g. D002342", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology, e.g. MSH", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2015_2014_09_08", required = true) @PathParam("version") String version,
    @ApiParam(value = "Children only flag, e.g. true", required = true) @PathParam("childrenOnly") boolean childrenOnly,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version
            + terminologyId + "/descendants with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find descendant descriptors",
          UserRole.VIEWER);

      DescriptorList list =
          contentService.findDescendantDescriptors(terminologyId, terminology,
              version, childrenOnly, Branch.ROOT, pfs);

      for (Descriptor descriptor : list.getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(
            descriptor);
      }

      return list;
    } catch (Exception e) {
      handleException(e, "trying to find the descendant descriptors");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find ancestor codes.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param parentsOnly the parents only
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the code list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/code/{terminology}/{version}/{terminologyId}/ancestors/{parentsOnly}")
  @ApiOperation(value = "Find ancestor codes", notes = "Gets a list of ancestor codes", response = CodeListJpa.class)
  public CodeList findAncestorCodes(
    @ApiParam(value = "Code terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Children only flag, e.g. true", required = true) @PathParam("parentsOnly") boolean parentsOnly,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version
            + terminologyId + "/ancestors with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find ancestor codes",
          UserRole.VIEWER);
      CodeList list =
          contentService.findAncestorCodes(terminologyId, terminology, version,
              parentsOnly, Branch.ROOT, pfs);

      for (Code code : list.getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(code);
      }

      return list;
    } catch (Exception e) {
      handleException(e, "trying to find the ancestor codes");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find descendant codes.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param childrenOnly the children only
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the code list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/code/{terminology}/{version}/{terminologyId}/descendants/{childrenOnly}")
  @ApiOperation(value = "Find descendant codes", notes = "Gets a list of descendant codes", response = CodeListJpa.class)
  public CodeList findDescendantCodes(
    @ApiParam(value = "Code terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Terminology, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Children only flag, e.g. true", required = true) @PathParam("childrenOnly") boolean childrenOnly,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version
            + terminologyId + "/descendants with PFS parameter "
            + (pfs == null ? "empty" : pfs.toString()));
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find descendant codes",
          UserRole.VIEWER);

      CodeList list =
          contentService.findDescendantCodes(terminologyId, terminology,
              version, childrenOnly, Branch.ROOT, pfs);

      for (Code code : list.getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(code);
      }

      return list;
    } catch (Exception e) {
      handleException(e, "trying to find the descendant codes");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Gets the subset members for concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the subset members for concept
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/cui/{terminology}/{version}/{terminologyId}/members")
  @ApiOperation(value = "Get subset members with this terminologyId", notes = "Get the subset members with the given concept id", response = SubsetMemberListJpa.class)
  public SubsetMemberList getSubsetMembersForConcept(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version + "/"
            + terminologyId + "/members");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken,
          "retrieve subset members for the concept", UserRole.VIEWER);

      SubsetMemberList list =
          contentService.getSubsetMembersForConcept(terminologyId, terminology,
              version, Branch.ROOT);

      for (SubsetMember<? extends ComponentHasAttributesAndName, ? extends Subset> member : list
          .getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(member);
      }
      return list;

    } catch (Exception e) {
      handleException(e, "trying to retrieve subset members for a concept");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Gets the subset members for atom.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the subset members for atom
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/aui/{terminology}/{version}/{terminologyId}/members")
  @ApiOperation(value = "Get subset members with this terminologyId", notes = "Get the subset members with the given atom id", response = SubsetMemberListJpa.class)
  public SubsetMemberList getSubsetMembersForAtom(
    @ApiParam(value = "Atom terminology id, e.g. 102751015", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Atom terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Atom terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /aui/" + terminology + "/" + version + "/"
            + terminologyId + "/members");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken,
          "retrieve subset members for the atom", UserRole.VIEWER);

      SubsetMemberList list =
          contentService.getSubsetMembersForAtom(terminologyId, terminology,
              version, Branch.ROOT);

      for (SubsetMember<? extends ComponentHasAttributesAndName, ? extends Subset> member : list
          .getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(member);
      }
      return list;

    } catch (Exception e) {
      handleException(e, "trying to retrieve subset members for a atom");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find relationships for concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the relationship list
   * @throws Exception the exception
   */
  /* see superclass */
  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  @Override
  @POST
  @Path("/cui/{terminology}/{version}/{terminologyId}/relationships")
  @ApiOperation(value = "Get relationships with this terminologyId", notes = "Get the relationships with the given concept id", response = RelationshipListJpa.class)
  public RelationshipList findRelationshipsForConcept(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query for searching relationships, e.g. concept id or concept name", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version + "/"
            + terminologyId + "/relationships?query=" + query);
    String queryStr = query == null ? "" : query;

    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken,
          "retrieve relationships for the concept", UserRole.VIEWER);

      RelationshipList list =
          contentService.findRelationshipsForConcept(terminologyId,
              terminology, version, Branch.ROOT, queryStr, false, pfs);

      // Use graph resolver
      for (Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes> rel : list
          .getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(rel);
      }

      // For any relationships to anonymous concepts, we need to push that up to
      // the current level and set relationship groups and inferred/stated
      RelationshipList result = new RelationshipListJpa();
      int group = 0;
      for (Relationship rel : list.getObjects()) {
        ConceptRelationship rel2 = (ConceptRelationship) rel;
        if (rel2.getTo().isAnonymous()) {

          // count how many relationships there are
          int ct = 0;
          for (ConceptRelationship innerRel : rel2.getTo().getRelationships()) {
            // this is only for grouped role relationships
            if (!innerRel.isHierarchical()) {
              ct++;
            }
          }
          // if >1, then group them
          if (ct > 1) {
            group++;
          }
          for (ConceptRelationship innerRel : rel2.getTo().getRelationships()) {
            // this is only for grouped role relationships
            if (!innerRel.isHierarchical()) {
              ConceptRelationship innerRel2 =
                  new ConceptRelationshipJpa(innerRel, true);
              innerRel2.setFrom(rel2.getFrom());
              innerRel2.setStated(rel2.isStated());
              innerRel2.setInferred(rel2.isInferred());
              // If >1 rels in anonymous concept, group them
              if (ct > 1) {
                innerRel2.setGroup(String.valueOf(group));
              }
              result.getObjects().add(innerRel2);
            }
          }

        } else {
          result.getObjects().add(rel);
        }
      }
      result.setTotalCount(list.getTotalCount());

      return result;

    } catch (Exception e) {
      handleException(e, "trying to retrieve relationships for a concept");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find deep relationships for concept.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the relationship list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui/{terminology}/{version}/{terminologyId}/relationships/deep")
  @ApiOperation(value = "Get deep relationships with this terminologyId", notes = "Get the relationships for the concept and also for any other atoms, concepts, descirptors, or codes in its graph for the specified concept id", response = RelationshipListJpa.class)
  public RelationshipList findDeepRelationshipsForConcept(
    @ApiParam(value = "Concept terminology id, e.g. C0000039", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. UMLS", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version + "/"
            + terminologyId + "/relationships/deep");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken,
          "retrieve deep relationships for the concept", UserRole.VIEWER);

      return contentService.findDeepRelationshipsForConcept(terminologyId,
          terminology, version, Branch.ROOT, false, pfs);

    } catch (Exception e) {
      handleException(e, "trying to retrieve deep relationships for a concept");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find relationships for descriptor.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the relationship list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/dui/{terminology}/{version}/{terminologyId}/relationships")
  @ApiOperation(value = "Get relationships with this terminologyId", notes = "Get the relationships with the given descriptor id", response = RelationshipListJpa.class)
  public RelationshipList findRelationshipsForDescriptor(
    @ApiParam(value = "Descriptor terminology id, e.g. D042033", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Descriptor terminology name, e.g. MSH", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Descriptor terminology version, e.g. 2015_2014_09_08", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query for searching relationships, e.g. concept id or concept name", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    String queryStr = query == null ? "" : query;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version + "/"
            + terminologyId + "/relationships?query=" + queryStr);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken,
          "retrieve relationships for the descriptor", UserRole.VIEWER);

      RelationshipList list =
          contentService.findRelationshipsForDescriptor(terminologyId,
              terminology, version, Branch.ROOT, queryStr, false, pfs);

      // Use graph resolver
      for (Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes> rel : list
          .getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(rel);
      }

      return list;

    } catch (Exception e) {
      handleException(e, "trying to retrieve relationships for a descriptor");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find relationships for code.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the relationship list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/code/{terminology}/{version}/{terminologyId}/relationships")
  @ApiOperation(value = "Get relationships with this terminologyId", notes = "Get the relationships with the given code id", response = RelationshipListJpa.class)
  public RelationshipList findRelationshipsForCode(
    @ApiParam(value = "Code terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Code terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Code terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query for searching relationships, e.g. concept id or concept name", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    String queryStr = query == null ? "" : query;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version + "/"
            + terminologyId + "/relationships?query=" + queryStr);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken,
          "retrieve relationships for the code", UserRole.VIEWER);

      RelationshipList list =
          contentService.findRelationshipsForCode(terminologyId, terminology,
              version, Branch.ROOT, queryStr, false, pfs);

      for (Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes> rel : list
          .getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(rel);
      }
      return list;

    } catch (Exception e) {
      handleException(e, "trying to retrieve relationships for a code");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Gets the atom subsets.
   *
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the atom subsets
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/aui/subset/all/{terminology}/{version}")
  @ApiOperation(value = "Get atom subsets", notes = "Get the atom level subsets", response = SubsetListJpa.class)
  public SubsetList getAtomSubsets(
    @ApiParam(value = "Atom terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Atom terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /aui/" + terminology + "/" + version
            + "/subsets");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "retrieve atom subsets",
          UserRole.VIEWER);

      SubsetList list =
          contentService.getAtomSubsets(terminology, version, Branch.ROOT);
      for (int i = 0; i < list.getCount(); i++) {
        contentService.getGraphResolutionHandler(terminology).resolve(
            list.getObjects().get(i));
      }
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve atom subsets");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Gets the concept subsets.
   *
   * @param terminology the terminology
   * @param version the version
   * @param authToken the auth token
   * @return the concept subsets
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @GET
  @Path("/cui/subset/all/{terminology}/{version}")
  @ApiOperation(value = "Get concept subsets", notes = "Get the concept level subsets", response = SubsetListJpa.class)
  public SubsetList getConceptSubsets(
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. latest", required = true) @PathParam("version") String version,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version
            + "/subsets");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "retrieve concept subsets",
          UserRole.VIEWER);
      SubsetList list =
          contentService.getConceptSubsets(terminology, version, Branch.ROOT);
      for (int i = 0; i < list.getCount(); i++) {
        contentService.getGraphResolutionHandler(terminology).resolve(
            list.getObjects().get(i));
      }
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve concept subsets");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find atom subset members.
   *
   * @param subsetId the subset id
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the subset member list
   * @throws Exception the exception
   */
  /* see superclass */
  @Produces({
      MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
  })
  @Override
  @POST
  @Path("/aui/subset/{subsetId}/{terminology}/{version}/members")
  @ApiOperation(value = "Find atom subset members", notes = "Get the members for the indicated atom subset", response = SubsetMemberListJpa.class)
  public SubsetMemberList findAtomSubsetMembers(
    @ApiParam(value = "Subset id, e.g. 341823003", required = true) @PathParam("subsetId") String subsetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query, e.g. 'iron'", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /aui/subset/" + subsetId + "/" + terminology
            + "/" + version + "/members?query=" + queryStr);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find atom subset members",
          UserRole.VIEWER);

      SubsetMemberList list =
          contentService.findAtomSubsetMembers(subsetId, terminology, version,
              Branch.ROOT, queryStr, pfs);
      for (SubsetMember<? extends ComponentHasAttributesAndName, ? extends Subset> member : list
          .getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(member);
      }
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve atom subsets");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find concept subset members.
   *
   * @param subsetId the subset id
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the subset member list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui/subset/{subsetId}/{terminology}/{version}/members")
  @ApiOperation(value = "Find concept subset members", notes = "Get the members for the indicated concept subset", response = SubsetMemberListJpa.class)
  public SubsetMemberList findConceptSubsetMembers(
    @ApiParam(value = "Subset id, e.g. 341823003", required = true) @PathParam("subsetId") String subsetId,
    @ApiParam(value = "Terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query, e.g. 'iron'", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/subset/" + subsetId + "/" + terminology
            + "/" + version + "/members?query=" + queryStr);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find concept subset members",
          UserRole.VIEWER);

      SubsetMemberList list =
          contentService.findConceptSubsetMembers(subsetId, terminology,
              version, Branch.ROOT, queryStr, pfs);
      for (SubsetMember<? extends ComponentHasAttributesAndName, ? extends Subset> member : list
          .getObjects()) {
        contentService.getGraphResolutionHandler(terminology).resolve(member);
      }
      return list;
    } catch (Exception e) {
      handleException(e, "trying to retrieve concept subsets");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find concept trees.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui/{terminology}/{version}/{terminologyId}/trees")
  @ApiOperation(value = "Get trees with this terminologyId", notes = "Get the trees with the given concept id", response = TreeListJpa.class)
  public TreeList findConceptTrees(
    @ApiParam(value = "Concept terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version + "/"
            + terminologyId + "/trees");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "retrieve trees for the concept ",
          UserRole.VIEWER);

      TreePositionList list =
          contentService.findTreePositionsForConcept(terminologyId,
              terminology, version, Branch.ROOT, pfs);

      final TreeList treeList = new TreeListJpa();
      for (final TreePosition<? extends ComponentHasAttributesAndName> treepos : list
          .getObjects()) {
        final Tree tree = contentService.getTreeForTreePosition(treepos);
        treeList.addObject(tree);
      }
      treeList.setTotalCount(list.getTotalCount());
      return treeList;

    } catch (Exception e) {
      handleException(e, "trying to retrieve trees for a concept");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find descriptor trees.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/dui/{terminology}/{version}/{terminologyId}/trees/")
  @ApiOperation(value = "Get trees with this terminologyId", notes = "Get the trees with the given descriptor id", response = TreeListJpa.class)
  public TreeList findDescriptorTrees(
    @ApiParam(value = "Descriptor terminology id, e.g. D002943", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Descriptor terminology name, e.g. MSH", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Descriptor terminology version, e.g. 2015_2014_09_08", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version + "/"
            + terminologyId + "/trees");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken,
          "retrieve trees for the descriptor ", UserRole.VIEWER);

      TreePositionList list =
          contentService.findTreePositionsForDescriptor(terminologyId,
              terminology, version, Branch.ROOT, pfs);

      final TreeList treeList = new TreeListJpa();
      for (final TreePosition<? extends ComponentHasAttributesAndName> treepos : list
          .getObjects()) {
        final Tree tree = contentService.getTreeForTreePosition(treepos);

        treeList.addObject(tree);
      }
      treeList.setTotalCount(list.getTotalCount());
      return treeList;

    } catch (Exception e) {
      handleException(e, "trying to trees relationships for a descriptor");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find code trees.
   *
   * @param terminologyId the terminology id
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/code/{terminology}/{version}/{terminologyId}/trees")
  @ApiOperation(value = "Get trees with this terminologyId", notes = "Get the trees with the given code id", response = TreeListJpa.class)
  public TreeList findCodeTrees(
    @ApiParam(value = "Code terminology id, e.g. 102751005", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "Code terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Code terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version + "/"
            + terminologyId + "/trees");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "retrieve trees for the code",
          UserRole.VIEWER);

      TreePositionList list =
          contentService.findTreePositionsForCode(terminologyId, terminology,
              version, Branch.ROOT, pfs);
      final TreeList treeList = new TreeListJpa();
      for (final TreePosition<? extends ComponentHasAttributesAndName> treepos : list
          .getObjects()) {
        final Tree tree = contentService.getTreeForTreePosition(treepos);

        treeList.addObject(tree);
      }
      treeList.setTotalCount(list.getTotalCount());
      return treeList;

    } catch (Exception e) {
      handleException(e, "trying to retrieve trees for a code");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find concept tree for query.
   *
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui/{terminology}/{version}/trees")
  @ApiOperation(value = "Find concept trees matching the query", notes = "Finds all merged trees matching the specified parameters", response = TreeJpa.class)
  public Tree findConceptTreeForQuery(
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query search term, e.g. 'vitamin'", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version
            + "/trees?query=" + query);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find trees for the concept",
          UserRole.VIEWER);

      TreePositionList list =
          contentService.findConceptTreePositionsForQuery(terminology, version,
              Branch.ROOT, queryStr, pfs);

      // dummy variables for construction of artificial root
      Tree dummyTree = new TreeJpa();
      dummyTree.setTerminology(terminology);
      dummyTree.setVersion(version);
      dummyTree.setNodeTerminologyId("dummy id");
      dummyTree.setNodeName("Root");
      dummyTree.setTotalCount(list.getTotalCount());

      // initialize the return tree with dummy root and set total count
      Tree returnTree = new TreeJpa(dummyTree);

      for (final TreePosition<? extends ComponentHasAttributesAndName> treepos : list
          .getObjects()) {

        // get tree for tree position
        final Tree tree = contentService.getTreeForTreePosition(treepos);

        // construct a new dummy-root tree for merging with existing tree
        Tree treeForTreePos = new TreeJpa(dummyTree);

        // add retrieved tree to dummy root level
        treeForTreePos.addChild(tree);

        // merge into the top-level dummy tree
        returnTree.mergeTree(treeForTreePos, pfs != null ? pfs.getSortField()
            : null);
      }

      // if only one child, dummy root not necessary
      if (returnTree.getChildren().size() == 1) {
        Tree tree = returnTree.getChildren().get(0);
        tree.setTotalCount(returnTree.getTotalCount());
        return tree;
      }

      // otherwise return the populated dummy root tree
      return returnTree;

    } catch (Exception e) {
      handleException(e, "trying to find trees for a query");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find descriptor tree for query.
   *
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/dui/{terminology}/{version}/trees")
  @ApiOperation(value = "Find descriptor trees matching the query", notes = "Finds all merged trees matching the specified parameters", response = TreeJpa.class)
  public Tree findDescriptorTreeForQuery(
    @ApiParam(value = "Descriptor terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Descriptor terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query search term, e.g. 'vitamin'", required = true) @QueryParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version
            + "/trees?query=" + query);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find trees for the descriptor",
          UserRole.VIEWER);

      TreePositionList list =
          contentService.findDescriptorTreePositionsForQuery(terminology,
              version, Branch.ROOT, queryStr, pfs);

      // dummy variables for construction of artificial root
      Tree dummyTree = new TreeJpa();
      dummyTree.setTerminology(terminology);
      dummyTree.setVersion(version);
      dummyTree.setNodeTerminologyId("dummy id");
      dummyTree.setNodeName("Root");
      dummyTree.setTotalCount(list.getTotalCount());

      // initialize the return tree with dummy root and set total count
      Tree returnTree = new TreeJpa(dummyTree);

      for (final TreePosition<? extends ComponentHasAttributesAndName> treepos : list
          .getObjects()) {

        // get tree for tree position
        final Tree tree = contentService.getTreeForTreePosition(treepos);

        // construct a new dummy-root tree for merging with existing tree
        Tree treeForTreePos = new TreeJpa(dummyTree);

        // add retrieved tree to dummy root level
        treeForTreePos.addChild(tree);

        // merge into the top-level dummy tree
        returnTree.mergeTree(treeForTreePos, pfs != null ? pfs.getSortField()
            : null);
      }

      // if only one child, dummy root not necessary
      if (returnTree.getChildren().size() == 1) {
        Tree tree = returnTree.getChildren().get(0);
        tree.setTotalCount(returnTree.getTotalCount());
        return tree;
      }

      // otherwise return the populated dummy root tree
      return returnTree;

    } catch (Exception e) {
      handleException(e, "trying to find trees for a query");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find code tree for query.
   *
   * @param terminology the terminology
   * @param version the version
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/code/{terminology}/{version}/trees")
  @ApiOperation(value = "Find code trees matching the query", notes = "Finds all merged trees matching the specified parameters", response = TreeJpa.class)
  public Tree findCodeTreeForQuery(
    @ApiParam(value = "Code terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Code terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Query search term, e.g. 'vitamin'", required = true) @PathParam("query") String query,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    // Fix query
    String queryStr = query == null ? "" : query;
    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version
            + "/trees?query=" + query);
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find trees for the code",
          UserRole.VIEWER);

      TreePositionList list =
          contentService.findCodeTreePositionsForQuery(terminology, version,
              Branch.ROOT, queryStr, pfs);

      // dummy variables for construction of artificial root
      Tree dummyTree = new TreeJpa();
      dummyTree.setTerminology(terminology);
      dummyTree.setVersion(version);
      dummyTree.setNodeTerminologyId("dummy id");
      dummyTree.setNodeName("Root");
      dummyTree.setTotalCount(list.getTotalCount());

      // initialize the return tree with dummy root and set total count
      Tree returnTree = new TreeJpa(dummyTree);

      for (final TreePosition<? extends ComponentHasAttributesAndName> treepos : list
          .getObjects()) {

        // get tree for tree position
        final Tree tree = contentService.getTreeForTreePosition(treepos);

        // construct a new dummy-root tree for merging with existing tree
        Tree treeForTreePos = new TreeJpa(dummyTree);

        // add retrieved tree to dummy root level
        treeForTreePos.addChild(tree);

        // merge into the top-level dummy tree
        returnTree.mergeTree(treeForTreePos, pfs != null ? pfs.getSortField()
            : null);
      }

      // if only one child, dummy root not necessary
      if (returnTree.getChildren().size() == 1) {
        Tree tree = returnTree.getChildren().get(0);
        tree.setTotalCount(returnTree.getTotalCount());
        return tree;
      }

      // otherwise return the populated dummy root tree
      return returnTree;

    } catch (Exception e) {
      handleException(e, "trying to find trees for a query");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }

  }

  /**
   * Find concept tree children.
   *
   * @param terminology the terminology
   * @param version the version
   * @param terminologyId the terminology id
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui/{terminology}/{version}/{terminologyId}/trees/children")
  @ApiOperation(value = "Find children trees for a concept", notes = "Returns paged children trees for a concept. Note: not ancestorPath-sensitive", response = TreeJpa.class)
  public TreeList findConceptTreeChildren(
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Concept terminologyId, e.g. C0000061", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version + "/"
            + terminologyId + "/" + "/trees/children");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find trees for the code",
          UserRole.VIEWER);

      // the TreeList to return
      TreeList childTrees = new TreeListJpa();

      // instantiate child tree positions array, used to construct trees
      TreePositionList childTreePositions =
          contentService.findConceptTreePositionChildren(terminologyId,
              terminology, version, Branch.ROOT, pfs);

      // for each tree position, construct a tree
      for (TreePosition<? extends ComponentHasAttributesAndName> childTreePosition : childTreePositions
          .getObjects()) {
        Tree childTree = new TreeJpa(childTreePosition);
        childTrees.addObject(childTree);
      }

      childTrees.setTotalCount(childTreePositions.getTotalCount());
      return childTrees;

    } catch (Exception e) {
      handleException(e, "trying to find tree children");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find code tree children.
   *
   * @param terminology the terminology
   * @param version the version
   * @param terminologyId the terminology id
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/code/{terminology}/{version}/{terminologyId}/trees/children")
  @ApiOperation(value = "Find children trees for a code", notes = "Returns paged children trees for a code. Note: not ancestorPath-sensitive", response = TreeJpa.class)
  public TreeList findCodeTreeChildren(
    @ApiParam(value = "Code terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Code terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Code terminologyId, e.g. C0000061", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version + "/"
            + terminologyId + "/" + "/trees/children");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find trees for the code",
          UserRole.VIEWER);

      // the TreeList to return
      TreeList childTrees = new TreeListJpa();

      // instantiate child tree positions array, used to construct trees
      TreePositionList childTreePositions =
          contentService.findCodeTreePositionChildren(terminologyId,
              terminology, version, Branch.ROOT, pfs);

      // for each tree position, construct a tree
      for (TreePosition<? extends ComponentHasAttributesAndName> childTreePosition : childTreePositions
          .getObjects()) {
        Tree childTree = new TreeJpa(childTreePosition);
        childTrees.addObject(childTree);
      }

      childTrees.setTotalCount(childTreePositions.getTotalCount());
      return childTrees;

    } catch (Exception e) {
      handleException(e, "trying to find tree children");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find descriptor tree children.
   *
   * @param terminology the terminology
   * @param version the version
   * @param terminologyId the terminology id
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/dui/{terminology}/{version}/{terminologyId}/trees/children")
  @ApiOperation(value = "Find children trees for a descriptor", notes = "Returns paged children trees for a descriptor. Note: not ancestorPath-sensitive", response = TreeJpa.class)
  public TreeList findDescriptorTreeChildren(
    @ApiParam(value = "Descriptor terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Descriptor terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "Descriptor terminologyId, e.g. D0000061", required = true) @PathParam("terminologyId") String terminologyId,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version + "/"
            + terminologyId + "/" + "/trees/children");
    ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "find trees for the descriptor",
          UserRole.VIEWER);

      // the TreeList to return
      TreeList childTrees = new TreeListJpa();

      // instantiate child tree positions array, used to construct trees
      TreePositionList childTreePositions =
          contentService.findDescriptorTreePositionChildren(terminologyId,
              terminology, version, Branch.ROOT, pfs);

      // for each tree position, construct a tree
      for (TreePosition<? extends ComponentHasAttributesAndName> childTreePosition : childTreePositions
          .getObjects()) {
        Tree childTree = new TreeJpa(childTreePosition);
        childTrees.addObject(childTree);
      }

      childTrees.setTotalCount(childTreePositions.getTotalCount());
      return childTrees;

    } catch (Exception e) {
      handleException(e, "trying to find tree children");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find concept tree roots.
   *
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/cui/{terminology}/{version}/trees/roots")
  @ApiOperation(value = "Find root trees for a concept-based terminology", notes = "Returns paged root trees for a concept-based terminology.", response = TreeJpa.class)
  public Tree findConceptTreeRoots(
    @ApiParam(value = "Concept terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Concept terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /cui/" + terminology + "/" + version + "/"
            + "/trees/roots");
    ContentService contentService = new ContentServiceJpa();

    try {
      authorizeApp(securityService, authToken, "find trees for the code",
          UserRole.VIEWER);

      // instantiate root tree positions array, used to construct trees
      TreePositionList rootTreePositions = new TreePositionListJpa();

      // get tree positions where ancestor path is empty
      rootTreePositions =
          contentService.findConceptTreePositionsForQuery(terminology, version,
              Branch.ROOT, "-ancestorPath:[* TO *]", pfs);

      Tree rootTree = null;
      // if a terminology with a single root concept
      if (rootTreePositions.getCount() == 1) {

        // construct root tree from single root
        rootTree = new TreeJpa(rootTreePositions.getObjects().get(0));
        rootTree.setTotalCount(rootTreePositions.getTotalCount());

        // get the children tree positions
        TreePositionList childTreePositions =
            contentService.findConceptTreePositionChildren(
                rootTree.getNodeTerminologyId(), terminology, version,
                Branch.ROOT, pfs);

        // construct and add children
        for (TreePosition<? extends ComponentHasAttributesAndName> childTreePosition : childTreePositions
            .getObjects()) {
          Tree childTree = new TreeJpa(childTreePosition);
          rootTree.addChild(childTree);
        }
      }

      // otherwise, no single root concept
      else {
        // create a dummy tree position to serve as root
        rootTree = new TreeJpa();
        rootTree.setTerminology(terminology);
        rootTree.setVersion(version);
        rootTree.setNodeName("Root");
        rootTree.setTotalCount(1);

        // construct and add children
        for (TreePosition<? extends ComponentHasAttributesAndName> rootTreePosition : rootTreePositions
            .getObjects()) {
          Tree childTree = new TreeJpa(rootTreePosition);
          rootTree.addChild(childTree);
        }
      }

      return rootTree;
    } catch (Exception e) {
      handleException(e, "trying to find root trees");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find descriptor tree roots.
   *
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/dui/{terminology}/{version}/trees/roots")
  @ApiOperation(value = "Find root trees for a descriptor-based terminology", notes = "Returns paged root trees for a descriptor-based terminology.", response = TreeJpa.class)
  public Tree findDescriptorTreeRoots(
    @ApiParam(value = "Descriptor terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Descriptor terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /dui/" + terminology + "/" + version + "/"
            + "/trees/roots");
    ContentService contentService = new ContentServiceJpa();

    try {
      authorizeApp(securityService, authToken, "find trees for the code",
          UserRole.VIEWER);

      // instantiate root tree positions array, used to construct trees
      TreePositionList rootTreePositions = new TreePositionListJpa();

      // get tree positions where ancestor path is empty
      rootTreePositions =
          contentService.findDescriptorTreePositionsForQuery(terminology,
              version, Branch.ROOT, "-ancestorPath:[* TO *]", pfs);

      Tree rootTree = null;

      // if a terminology with a single root descriptor
      if (rootTreePositions.getCount() == 1) {

        // construct root tree from single root
        rootTree = new TreeJpa(rootTreePositions.getObjects().get(0));
        rootTree.setTotalCount(rootTreePositions.getTotalCount());

        // get the children tree positions
        TreePositionList childTreePositions =
            contentService.findDescriptorTreePositionChildren(
                rootTree.getNodeTerminologyId(), terminology, version,
                Branch.ROOT, pfs);

        // construct and add children
        for (TreePosition<? extends ComponentHasAttributesAndName> childTreePosition : childTreePositions
            .getObjects()) {
          Tree childTree = new TreeJpa(childTreePosition);
          rootTree.mergeTree(childTree, null);
        }
      }

      // otherwise, no single root descriptor
      else {
        // create a dummy tree position to serve as root
        rootTree = new TreeJpa();
        rootTree.setTerminology(terminology);
        rootTree.setVersion(version);
        rootTree.setNodeName("Root");
        rootTree.setTotalCount(1);

        // construct and add children
        for (TreePosition<? extends ComponentHasAttributesAndName> rootTreePosition : rootTreePositions
            .getObjects()) {
          Tree childTree = new TreeJpa(rootTreePosition);
          rootTree.addChild(childTree);
        }
      }

      return rootTree;
    } catch (Exception e) {
      handleException(e, "trying to find root trees");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

  /**
   * Find code tree roots.
   *
   * @param terminology the terminology
   * @param version the version
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the tree
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  @POST
  @Path("/code/{terminology}/{version}/trees/roots")
  @ApiOperation(value = "Find root trees for a code-based terminology", notes = "Returns paged root trees for a code-based terminology.", response = TreeJpa.class)
  public Tree findCodeTreeRoots(
    @ApiParam(value = "Code terminology name, e.g. SNOMEDCT_US", required = true) @PathParam("terminology") String terminology,
    @ApiParam(value = "Code terminology version, e.g. 2014_09_01", required = true) @PathParam("version") String version,
    @ApiParam(value = "PFS Parameter, e.g. '{ \"startIndex\":\"1\", \"maxResults\":\"5\" }'", required = false) PfsParameterJpa pfs,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Content): /code/" + terminology + "/" + version + "/"
            + "/trees/roots");
    ContentService contentService = new ContentServiceJpa();

    try {
      authorizeApp(securityService, authToken, "find trees for the code",
          UserRole.VIEWER);

      // instantiate root tree positions array, used to construct trees
      TreePositionList rootTreePositions = new TreePositionListJpa();

      // get tree positions where ancestor path is empty
      rootTreePositions =
          contentService.findCodeTreePositionsForQuery(terminology, version,
              Branch.ROOT, "-ancestorPath:[* TO *]", pfs);

      Tree rootTree = null;

      // if a terminology with a single root code
      if (rootTreePositions.getCount() == 1) {

        // construct root tree from single root
        rootTree = new TreeJpa(rootTreePositions.getObjects().get(0));
        rootTree.setTotalCount(rootTreePositions.getTotalCount());

        // get the children tree positions
        TreePositionList childTreePositions =
            contentService.findCodeTreePositionChildren(
                rootTree.getNodeTerminologyId(), terminology, version,
                Branch.ROOT, pfs);

        // construct and add children
        for (TreePosition<? extends ComponentHasAttributesAndName> childTreePosition : childTreePositions
            .getObjects()) {
          Tree childTree = new TreeJpa(childTreePosition);
          rootTree.mergeTree(childTree, null);
        }
      }

      // otherwise, no single root code
      else {
        // create a dummy tree position to serve as root
        rootTree = new TreeJpa();
        rootTree.setTerminology(terminology);
        rootTree.setVersion(version);
        rootTree.setNodeName("Root");
        rootTree.setTotalCount(1);

        // construct and add children
        for (TreePosition<? extends ComponentHasAttributesAndName> rootTreePosition : rootTreePositions
            .getObjects()) {
          Tree childTree = new TreeJpa(rootTreePosition);
          rootTree.addChild(childTree);
        }
      }

      return rootTree;
    } catch (Exception e) {
      handleException(e, "trying to find root trees");
      return null;
    } finally {
      contentService.close();
      securityService.close();
    }
  }

}
