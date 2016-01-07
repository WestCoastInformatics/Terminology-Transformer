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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.wci.tt.SourceDataFile;
import com.wci.tt.helpers.ConfigUtility;
import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.SourceDataFileList;
import com.wci.tt.helpers.StringList;
import com.wci.tt.jpa.SourceDataFileJpa;
import com.wci.tt.jpa.services.SourceDataServiceJpa;
import com.wci.tt.jpa.services.helpers.SourceDataFileUtil;
import com.wci.tt.jpa.services.rest.SourceDataServiceRest;
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

  /* see superclass */
  @Override
  @Path("/sourceDataFile/add")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_XML)
  public String addSourceDataFile(
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
      handleException(e, "uploading a file");
    }

    SourceDataService sourceDataService = new SourceDataServiceJpa();

    for (File file : files) {
      SourceDataFile sdf = new SourceDataFileJpa();
      sdf.setName(file.getName());
      sdf.setPath(file.getAbsolutePath());
      sdf.setSize(file.length());
      sdf.setDateUploaded(new Date());
      sdf.setLastModifiedBy(authToken);

      try {
        sourceDataService.addSourceDataFile(sdf);
      } catch (Exception e) {
        handleException(e, " uploading a file");
        return null;
      }
    }

    sourceDataService.close();

    return null;

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

    try {

      SourceDataService service = new SourceDataServiceJpa();
      
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

      service.close();

    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
    }
  }

  @Override
  @GET
  @Path("/sourceDataFile/sourceDataFiles")
  @ApiOperation(value = "Get uploaded file details", notes = "Returns list of details for uploaded files", response = StringList.class)
  public SourceDataFileList getSourceDataFiles(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Source Data Service): /sourceData/get");

    try {

      SourceDataService service = new SourceDataServiceJpa();
      SourceDataFileList sourceDataFiles = service.getSourceDataFiles();
      service.close();

      return sourceDataFiles;

    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
      return null;
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

    try {

      SourceDataService service = new SourceDataServiceJpa();
      SourceDataFileList sourceDataFiles =
          service.findSourceDataFilesForQuery(query, pfsParameter);
      service.close();

      return sourceDataFiles;

    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
      return null;
    }

  }

}
