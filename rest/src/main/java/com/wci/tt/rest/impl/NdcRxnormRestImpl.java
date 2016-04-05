/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.io.File;
import java.util.List;

import javax.ws.rs.Consumes;
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

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.jpa.services.algo.RrfLoaderAlgorithm;
import com.wci.tt.jpa.services.rest.NdcRxnormRest;
import com.wci.tt.services.CoordinatorService;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.algo.LabelSetMarkedParentAlgorithm;
import com.wci.umls.server.jpa.algo.RrfFileSorter;
import com.wci.umls.server.jpa.algo.RrfReaders;
import com.wci.umls.server.jpa.algo.TransitiveClosureAlgorithm;
import com.wci.umls.server.jpa.algo.TreePositionAlgorithm;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.model.content.ConceptSubset;
import com.wci.umls.server.model.content.Subset;
import com.wci.umls.server.model.meta.IdType;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.ContentService;
import com.wci.umls.server.services.SecurityService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Class implementation the REST Service for NDC routines for
 * {@link NdcRxNormServiceRest}.
 * 
 * Includes hibernate tags for MEME database.
 */
@Path("/ndc")
@Api(value = "/ndc", description = "NDC Operations")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class NdcRxnormRestImpl extends RootServiceRestImpl
    implements NdcRxnormRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link NdcRxnormRestImpl}.
   *
   * @throws Exception the exception
   */
  public NdcRxnormRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /* see superclass */
  @Override
  @PUT
  @Path("/terminology/load/ndc")
  @Consumes(MediaType.TEXT_PLAIN)
  @ApiOperation(value = "Load NDC from an RXNORM directory", notes = "Loads terminologies from an RRF directory for specified terminology and version")
  public void loadTerminologyNdc(
    @ApiParam(value = "Terminology, e.g. UMLS", required = true) @QueryParam("terminology") String terminology,
    @ApiParam(value = "version, e.g. latest", required = true) @QueryParam("version") String version,
    @ApiParam(value = "RRF input directory", required = true) String inputDir,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful POST call (Content): /terminology/load/rrf/umls/"
            + terminology + "/" + version + " from input directory "
            + inputDir);

    // Track system level information
    long startTimeOrig = System.nanoTime();
    final ContentService contentService = new ContentServiceJpa();
    try {
      authorizeApp(securityService, authToken, "load RRF",
          UserRole.ADMINISTRATOR);

      // Check the input directory
      File inputDirFile = new File(inputDir);
      if (!inputDirFile.exists()) {
        throw new Exception("Specified input directory does not exist");
      }

      // Sort files - not really needed because files are already sorted
      Logger.getLogger(getClass()).info("  Sort RRF Files");
      final RrfFileSorter sorter = new RrfFileSorter();
      // Be flexible about missing files for RXNORM
      sorter
          .setRequireAllFiles(false);
      // File outputDir = new File(inputDirFile, "/RRF-sorted-temp/");
      // sorter.sortFiles(inputDirFile, outputDir);
      String releaseVersion = sorter.getFileVersion(inputDirFile);
      if (releaseVersion == null) {
        releaseVersion = version;
      }
      Logger.getLogger(getClass()).info("  releaseVersion = " + releaseVersion);

      // Open readers - just open original RRF
      final RrfReaders readers = new RrfReaders(inputDirFile);
      // Use default prefix if not specified
      readers.openOriginalReaders("RXN");

      // Load RRF
      final RrfLoaderAlgorithm algorithm = new RrfLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setVersion(version);

      algorithm.setReleaseVersion(releaseVersion);
      algorithm.setReaders(readers);
      algorithm.compute();
      algorithm.close();

      // Compute transitive closure
      // Obtain each terminology and run transitive closure on it with the
      // correct id type
      // Refresh caches after metadata has changed in loader
      contentService.refreshCaches();
      for (final Terminology t : contentService.getTerminologyLatestVersions()
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
      for (final Terminology t : contentService.getTerminologyLatestVersions()
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
      for (final Terminology t : contentService.getTerminologyLatestVersions()
          .getObjects()) {
        for (final Subset subset : contentService
            .getConceptSubsets(t.getTerminology(), t.getVersion(), Branch.ROOT)
            .getObjects()) {
          final ConceptSubset conceptSubset = (ConceptSubset) subset;
          if (conceptSubset.isLabelSubset()) {
            Logger.getLogger(getClass())
                .info("  Create label set for subset = " + subset);
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
