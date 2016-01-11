/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
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

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;
import com.wci.tt.UserRole;
import com.wci.tt.helpers.ConfigUtility;
import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.SourceDataFileList;
import com.wci.tt.helpers.SourceDataList;
import com.wci.tt.helpers.StringList;
import com.wci.tt.jpa.SourceDataFileJpa;
import com.wci.tt.jpa.SourceDataJpa;
import com.wci.tt.jpa.algo.RemoveTerminologyAlgorithm;
import com.wci.tt.jpa.helpers.SourceDataFileListJpa;
import com.wci.tt.jpa.services.ContentServiceJpa;
import com.wci.tt.jpa.services.SecurityServiceJpa;
import com.wci.tt.jpa.services.SourceDataServiceJpa;
import com.wci.tt.jpa.services.helper.SourceDataFileUtil;
import com.wci.tt.jpa.services.rest.SourceDataServiceRest;
import com.wci.tt.model.meta.Terminology;
import com.wci.tt.services.ContentService;
import com.wci.tt.services.SecurityService;
import com.wci.tt.services.SourceDataService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link SourceDataServiceRest}.
 */
@Path("/file")
@Api(value = "/file", description = "Operations supporting file")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class SourceDataServiceRestImpl extends RootServiceRestImpl
    implements SourceDataServiceRest {

  /** The security service. */
  private SecurityService securityService;

  public SourceDataServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /* see superclass */
  @Override
  @Path("/sourceDataFile/add")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_XML)
  public SourceDataFileList uploadSourceDataFile(
    @FormDataParam("file") InputStream fileInputStream,
    @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @QueryParam("unzip") boolean unzip,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (SourceDataService): /sourceDataFile/add "
            + (contentDispositionHeader != null
                ? contentDispositionHeader.getFileName() : "UNKNOWN FILE")
            + " unzip=" + unzip + " authToken=" + authToken);

    String destinationFolder =
        ConfigUtility.getConfigProperties().getProperty("upload.dir");

    List<File> files = new ArrayList<>();

    try {

      authorizeApp(securityService, authToken, "upload source data files",
          UserRole.ADMIN);

      // if unzipping requested and file is valid, extract compressed file to
      // destination folder
      if (unzip == true) {
        files.addAll(
            SourceDataFileUtil.extractCompressedSourceDataFile(fileInputStream,
                destinationFolder, contentDispositionHeader.getFileName()));
      }

      // otherwise, simply write the input stream
      else {
        files.add(SourceDataFileUtil.writeSourceDataFile(fileInputStream,
            destinationFolder, contentDispositionHeader.getFileName()));

      }
    } catch (Exception e) {
      System.out.println("caught");
      handleException(e, "uploading a source data sfile");
    } finally {
      // do nothing
    }

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      SourceDataFileList fileList = new SourceDataFileListJpa();

      for (File file : files) {
        SourceDataFile sdf = new SourceDataFileJpa();
        sdf.setName(file.getName());
        sdf.setPath(file.getAbsolutePath());
        sdf.setDirectory(file.isDirectory());
        sdf.setSize(file.length());
        sdf.setDateUploaded(new Date());
        sdf.setLastModifiedBy(authToken);

        service.addSourceDataFile(sdf);
        fileList.addObject(sdf);
      }

      return fileList;

    } catch (Exception e) {
      handleException(e, " uploading a file");
      return null;
    } finally {
      service.close();
    }
  }

  @Override
  @PUT
  @Path("sourceDataFile/add")
  public SourceDataFile addSourceDataFile(
    @ApiParam(value = "SourceDataFile to add", required = true) SourceDataFileJpa sourceDataFile,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Source Data Service): /sourceData/update");
    final SourceDataService service = new SourceDataServiceJpa();
    try {

      authorizeApp(securityService, authToken, "add source data file",
          UserRole.ADMIN);

      service.addSourceDataFile(sourceDataFile);

      return sourceDataFile;

    } catch (Exception e) {
      handleException(e, "update source data files");
      return null;
    } finally {
      service.close();
    }
  }

  @Override
  @POST
  @Path("sourceDataFile/update")
  public void updateSourceDataFile(
    @ApiParam(value = "SourceDataFile to update", required = true) SourceDataFileJpa sourceDataFile,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Source Data Service): /sourceData/update");

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      authorizeApp(securityService, authToken, "add source data file",
          UserRole.ADMIN);

      service.updateSourceDataFile(sourceDataFile);

    } catch (Exception e) {
      handleException(e, "update source data files");
    } finally {
      service.close();
    }
  }

  @Override
  @DELETE
  @Path("sourceDataFile/delete/{sourceDataFileId}")
  public void deleteSourceDataFile(
    @ApiParam(value = "Id of sourceDataFile to delete, e.g. 5", required = true) @PathParam("sourceDataFileId") Long sourceDataFileId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    String uploadDir =
        ConfigUtility.getConfigProperties().getProperty("upload.dir");

    Logger.getLogger(getClass())
        .info("RESTful call (Source Data Service): /sourceData/delete/"
            + sourceDataFileId);

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      authorizeApp(securityService, authToken, "delete source data file",
          UserRole.ADMIN);

      SourceDataFile sourceDataFile =
          service.getSourceDataFile(sourceDataFileId);

      // physically remove the file
      File dir = new File(uploadDir);
      File[] files = dir.listFiles();
      for (File f : files) {
        if (f.getName().equals(sourceDataFile.getName())) {
          f.delete();
        }
      }

      // remove the database entry
      service.removeSourceDataFile(sourceDataFile.getId());

    } catch (Exception e) {
      handleException(e, "delete source data files");
    } finally {
      service.close();
    }
  }

  @Override
  @GET
  @Path("/sourceDataFile/sourceDataFiles")
  @ApiOperation(value = "Get uploaded file details", notes = "Returns list of details for uploaded files", response = StringList.class)
  public SourceDataFileList getSourceDataFiles(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Source Data Service): /sourceDataFile/sourceDataFiles");

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      authorizeApp(securityService, authToken, "retrieve source data files",
          UserRole.ADMIN);

      SourceDataFileList sourceDataFiles = service.getSourceDataFiles();

      return sourceDataFiles;

    } catch (Exception e) {
      handleException(e, "retrieving source data files");
      return null;
    } finally {
      service.close();
    }

  }

  @Override
  @GET
  @Path("/sourceDataFile/query/{query}")
  @ApiOperation(value = "Query source data files", notes = "Returns list of details for uploaded files returned by query", response = StringList.class)
  public SourceDataFileList findSourceDataFilesForQuery(
    @ApiParam(value = "String query, e.g. SNOMEDCT", required = true) @PathParam("query") String query,
    @ApiParam(value = "Paging/filtering/sorting object", required = false) PfsParameter pfsParameter,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass()).info(
        "RESTful call (Source Data Service): /sourceDataFile/query/" + query);

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      authorizeApp(securityService, authToken, "search for source data files",
          UserRole.ADMIN);

      SourceDataFileList sourceDataFiles =
          service.findSourceDataFilesForQuery(query, pfsParameter);

      return sourceDataFiles;

    } catch (Exception e) {
      handleException(e, "search for source data files");
      return null;
    } finally {
      service.close();
    }

  }

  /* see superclass */
  @Override
  @Path("/sourceData/add")
  @PUT
  public SourceData addSourceData(
    @ApiParam(value = "Source data to add", required = true) SourceDataJpa sourceData,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (SourceDataService): /sourceData/add");

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      authorizeApp(securityService, authToken, "add new source data",
          UserRole.ADMIN);

      service.addSourceData(sourceData);

      return sourceData;

    } catch (Exception e) {
      handleException(e, "adding new source data");
      return null;
    } finally {
      service.close();
    }

  }

  /* see superclass */
  @Override
  @Path("/sourceData/update")
  @POST
  public void updateSourceData(
    @ApiParam(value = "Source data to update", required = true) SourceDataJpa sourceData,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (SourceDataService): /sourceData/add");

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      authorizeApp(securityService, authToken, "add new source data",
          UserRole.ADMIN);

      service.updateSourceData(sourceData);

    } catch (Exception e) {
      handleException(e, "adding new source data");
    } finally {
      service.close();
    }

  }

  @Override
  @DELETE
  @Path("sourceData/delete/{sourceDataId}")
  public void deleteSourceData(
    @ApiParam(value = "Id of sourceData to delete, e.g. 5", required = true) @PathParam("sourceDataId") Long sourceDataId,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Source Data Service): /sourceData/delete/"
            + sourceDataId);

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      authorizeApp(securityService, authToken, "delete source data",
          UserRole.ADMIN);

      service.removeSourceData(sourceDataId);

    } catch (Exception e) {
      handleException(e, "delete source data");
    } finally {
      service.close();
    }
  }

  @Override
  @GET
  @Path("/sourceData/sourceDatas")
  @ApiOperation(value = "Get uploaded file details", notes = "Returns list of details for uploaded files", response = StringList.class)
  public SourceDataList getSourceDatas(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Source Data Service): /sourceData/get");

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      authorizeApp(securityService, authToken, "get source datas",
          UserRole.ADMIN);

      SourceDataList sourceDatas = service.getSourceDatas();

      return sourceDatas;

    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
      return null;
    } finally {
      service.close();
    }

  }

  @Override
  @GET
  @Path("/sourceData/query/{query}")
  @ApiOperation(value = "Query source data files", notes = "Returns list of details for uploaded files returned by query", response = StringList.class)
  public SourceDataList findSourceDatasForQuery(
    @ApiParam(value = "String query, e.g. SNOMEDCT", required = true) @PathParam("query") String query,
    @ApiParam(value = "Paging/filtering/sorting object", required = false) PfsParameter pfsParameter,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Source Data Service): /sourceData/query/" + query);

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      authorizeApp(securityService, authToken, "get source datas",
          UserRole.ADMIN);

      SourceDataList sourceDatas =
          service.findSourceDatasForQuery(query, pfsParameter);

      return sourceDatas;

    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
      return null;
    } finally {
      service.close();
    }

  }

  @Override
  @POST
  @Path("/loadRxnorm")
  @ApiOperation(value = "Test function for loading rxnorm", notes = "Loads Rxnorm", response = StringList.class)
  public void loadRxnormTestFn() throws Exception {
    Logger.getLogger(getClass())
        .info("SourceDataFile Service - load test RXNORM data (temp fn)");

    ContentService contentService = new ContentServiceJpa();

    final SourceDataService service = new SourceDataServiceJpa();

    try {

      contentService.loadRrfTerminology("RXNORMTEST", "20160101", false,
          "D:/Terminology-Transformer/data/RXNORM/rrf");

    } catch (Exception e) {
      e.printStackTrace();
      handleException(e, "loading test RXNORM terminology");
    } finally {
      service.close();
    }

  }

  @Override
  @POST
  @Path("/sourceDataFile/removeRxnorm")
  @ApiOperation(value = "Test function for removing rxnorm", notes = "Removes Rxnorm", response = StringList.class)
  public void removeRxnormTestFn() throws Exception {
    Logger.getLogger(getClass())
        .info("SourceDataFile Service - remove test RXNORM data (temp fn)");

    ContentService contentService = new ContentServiceJpa();
    RemoveTerminologyAlgorithm algo = new RemoveTerminologyAlgorithm();

    try {

      for (Terminology terminology : contentService.getTerminologies()
          .getObjects()) {

        Logger.getLogger(getClass()).info("Removing terminology "
            + terminology.getTerminology() + "/" + terminology.getVersion());

        // Remove terminology
        algo.setTerminology(terminology.getTerminology());
        algo.setVersion(terminology.getVersion());
        algo.compute();

      }

      // remove root terminology
      algo.setTerminology("RXNORMTEST");
      algo.setVersion("20160101");
      algo.compute();

    } catch (Exception e) {
      e.printStackTrace();
      handleException(e, "loading test RXNORM data");
    } finally {
      contentService.close();
      algo.close();

    }

  }

  @Override
  @GET
  @Path("/converter/converters")
  @ApiOperation(value = "Test function for removing rxnorm", notes = "Removes Rxnorm", response = StringList.class)
  public StringList getConverterNames(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Source Data Service): /converter/converters");

    final SourceDataService service = new SourceDataServiceJpa();

    try {
      authorizeApp(securityService, authToken, "get source datas",
          UserRole.ADMIN);

      StringList converterNameList = service.getConverterNames();

      return converterNameList;

    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
      return null;
    } finally {
      service.close();
    }
  }

}
