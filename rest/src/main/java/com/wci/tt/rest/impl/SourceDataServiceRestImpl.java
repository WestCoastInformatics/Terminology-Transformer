/*
 *    Copyright 2016 West Coast Informatics, LLC
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
import com.wci.tt.helpers.SourceDataFileList;
import com.wci.tt.helpers.SourceDataList;
import com.wci.tt.jpa.SourceDataFileJpa;
import com.wci.tt.jpa.SourceDataJpa;
import com.wci.tt.jpa.helpers.SourceDataFileListJpa;
import com.wci.tt.jpa.services.SourceDataServiceJpa;
import com.wci.tt.jpa.services.helper.SourceDataFileUtility;
import com.wci.tt.jpa.services.rest.SourceDataServiceRest;
import com.wci.tt.services.SourceDataService;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.StringList;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.SecurityService;
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

  /**
   * Instantiates an empty {@link SourceDataServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public SourceDataServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /* see superclass */
  @Override
  @Path("/upload")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public SourceDataFileList uploadSourceDataFile(
    @FormDataParam("file") InputStream fileInputStream,
    @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @QueryParam("unzip") boolean unzip,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Source Data): /upload "
            + (contentDispositionHeader != null
                ? contentDispositionHeader.getFileName() : "UNKNOWN FILE")
            + " unzip=" + unzip + " authToken=" + authToken);

    String destinationFolder =
        ConfigUtility.getConfigProperties().getProperty("upload.dir");

    final SourceDataService service = new SourceDataServiceJpa();
    try {
      final String userName = authorizeApp(securityService, authToken,
          "upload source data files", UserRole.ADMINISTRATOR);

      final List<File> files = new ArrayList<>();
      // if unzipping requested and file is valid, extract compressed file to
      // destination folder
      if (unzip == true) {
        files.addAll(SourceDataFileUtility.extractCompressedSourceDataFile(
            fileInputStream, destinationFolder,
            contentDispositionHeader.getFileName()));
      }
      // otherwise, simply write the input stream
      else {
        files.add(SourceDataFileUtility.writeSourceDataFile(fileInputStream,
            destinationFolder, contentDispositionHeader.getFileName()));

      }

      // Iterate through file list and add source data files.
      final SourceDataFileList fileList = new SourceDataFileListJpa();
      for (final File file : files) {
        final SourceDataFile sdf = new SourceDataFileJpa();
        sdf.setName(file.getName());
        sdf.setPath(file.getAbsolutePath());
        sdf.setDirectory(file.isDirectory());
        sdf.setSize(file.length());
        sdf.setTimestamp(new Date());
        sdf.setLastModifiedBy(userName);

        service.addSourceDataFile(sdf);
        fileList.addObject(sdf);
      }

      return fileList;

    } catch (Exception e) {
      handleException(e, "uploading a source data file");
      return null;
    } finally {
      service.close();
    }
  }

  /* see superclass */
  @Override
  @PUT
  @Path("/add")
  public SourceDataFile addSourceDataFile(
    @ApiParam(value = "SourceDataFile to add", required = true) SourceDataFileJpa sourceDataFile,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {

    Logger.getLogger(getClass()).info("RESTful call (Source Data): /add");
    final SourceDataService service = new SourceDataServiceJpa();
    try {
      final String userName = authorizeApp(securityService, authToken,
          "add source data file", UserRole.ADMINISTRATOR);

      sourceDataFile.setLastModifiedBy(userName);
      return service.addSourceDataFile(sourceDataFile);

    } catch (Exception e) {
      handleException(e, "update source data files");
    } finally {
      service.close();
    }
    return null;
  }

  /* see superclass */
  @Override
  @POST
  @Path("/update")
  public void updateSourceDataFile(
    @ApiParam(value = "SourceDataFile to update", required = true) SourceDataFileJpa sourceDataFile,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Source Data): /update");

    final SourceDataService service = new SourceDataServiceJpa();
    try {
      final String userName = authorizeApp(securityService, authToken,
          "add source data file", UserRole.ADMINISTRATOR);

      sourceDataFile.setLastModifiedBy(userName);
      service.updateSourceDataFile(sourceDataFile);

    } catch (Exception e) {
      handleException(e, "update source data files");
    } finally {
      service.close();
    }
  }

  /* see superclass */
  @Override
  @DELETE
  @Path("/remove/{id}")
  public void removeSourceDataFile(
    @ApiParam(value = "SourceDataFile id, e.g. 5", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    String uploadDir =
        ConfigUtility.getConfigProperties().getProperty("upload.dir");
    Logger.getLogger(getClass())
        .info("RESTful call (Source Data): /remove/" + id);

    final SourceDataService service = new SourceDataServiceJpa();
    try {
      authorizeApp(securityService, authToken, "delete source data file",
          UserRole.ADMINISTRATOR);

      final SourceDataFile sourceDataFile = service.getSourceDataFile(id);

      // physically remove the file
      final File dir = new File(uploadDir);
      final File[] files = dir.listFiles();
      for (final File f : files) {
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

  /* see superclass */
  @Override
  @GET
  @Path("/find")
  @ApiOperation(value = "Query source data files", notes = "Returns list of details for uploaded files returned by query", response = StringList.class)
  public SourceDataFileList findSourceDataFilesForQuery(
    @ApiParam(value = "String query, e.g. SNOMEDCT", required = true) @QueryParam("query") String query,
    @ApiParam(value = "Paging/filtering/sorting object", required = false) PfsParameter pfsParameter,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Source Data): /find - " + query);

    final SourceDataService service = new SourceDataServiceJpa();
    try {
      authorizeApp(securityService, authToken, "search for source data files",
          UserRole.ADMINISTRATOR);

      return service.findSourceDataFilesForQuery(query, pfsParameter);

    } catch (Exception e) {
      handleException(e, "search for source data files");
      return null;
    } finally {
      service.close();
    }

  }

  /* see superclass */
  @Override
  @Path("/data/add")
  @PUT
  public SourceData addSourceData(
    @ApiParam(value = "Source data to add", required = true) SourceDataJpa sourceData,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (Source Data): /data/add");

    final SourceDataService service = new SourceDataServiceJpa();
    try {
      final String userName = authorizeApp(securityService, authToken,
          "add new source data", UserRole.ADMINISTRATOR);

      sourceData.setLastModifiedBy(userName);
      return service.addSourceData(sourceData);

    } catch (Exception e) {
      handleException(e, "adding new source data");
      return null;
    } finally {
      service.close();
    }

  }

  /* see superclass */
  @Override
  @Path("/data/update")
  @POST
  public void updateSourceData(
    @ApiParam(value = "Source data to update", required = true) SourceDataJpa sourceData,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Source Data): /data/update");

    final SourceDataService service = new SourceDataServiceJpa();
    try {
      final String userName = authorizeApp(securityService, authToken,
          "add new source data", UserRole.ADMINISTRATOR);

      sourceData.setLastModifiedBy(userName);
      service.updateSourceData(sourceData);

    } catch (Exception e) {
      handleException(e, "adding new source data");
    } finally {
      service.close();
    }

  }

  /* see superclass */
  @Override
  @DELETE
  @Path("data/remove/{id}")
  public void removeSourceData(
    @ApiParam(value = "SourceData id, e.g. 5", required = true) @PathParam("id") Long id,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Source Data): /data/remove/" + id);

    final SourceDataService service = new SourceDataServiceJpa();
    try {
      authorizeApp(securityService, authToken, "delete source data",
          UserRole.ADMINISTRATOR);

      service.removeSourceData(id);

    } catch (Exception e) {
      handleException(e, "delete source data");
    } finally {
      service.close();
    }
  }

  /* see superclass */
  @Override
  @GET
  @Path("/data/find")
  @ApiOperation(value = "Query source data files", notes = "Returns list of details for uploaded files returned by query", response = StringList.class)
  public SourceDataList findSourceDataForQuery(
    @ApiParam(value = "String query, e.g. SNOMEDCT", required = true) @QueryParam("query") String query,
    @ApiParam(value = "Paging/filtering/sorting object", required = false) PfsParameter pfsParameter,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Source Data): /data/find" + query);

    final SourceDataService service = new SourceDataServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get source datas",
          UserRole.ADMINISTRATOR);

      return service.findSourceDatasForQuery(query, pfsParameter);

    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
      return null;
    } finally {
      service.close();
    }

  }

  /* see superclass */
  @Override
  @GET
  @Path("/data/loaders")
  @ApiOperation(value = "Get loader names", notes = "Gets all loader names.", response = StringList.class)
  public StringList getLoaderNames(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
      throws Exception {
    Logger.getLogger(getClass())
        .info("RESTful call (Source Data): /data/loaders");

    final SourceDataService service = new SourceDataServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get source datas",
          UserRole.ADMINISTRATOR);

      return service.getLoaderNames();

    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
      return null;
    } finally {
      service.close();
    }
  }

}
