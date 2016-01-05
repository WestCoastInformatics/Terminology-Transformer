/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.wci.tt.helpers.ConfigUtility;
import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.StringList;
import com.wci.tt.jpa.services.rest.FileServiceRest;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link FileServiceRest}.
 */
@Path("/file")
@Api(value = "/file", description = "Operations supporting file")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class FileServiceRestImpl extends RootServiceRestImpl
    implements FileServiceRest {

  /* see superclass */
  @Override
  @Path("/upload")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_XML)
  public String saveFile(@FormDataParam("file") InputStream fileInputStream,
    @FormDataParam("file") FormDataContentDisposition contentDispositionHeader)
      throws Exception {

    Logger.getLogger(getClass())
        .info("RESTful call (Authentication): /file/upload "
            + (contentDispositionHeader != null
                ? contentDispositionHeader.getFileName() : "UNKNOWN FILE"));
    try {

      String uploadDir =
          ConfigUtility.getConfigProperties().getProperty("upload.dir");

      int read = 0;
      byte[] bytes = new byte[1024];

      OutputStream outputStream = new FileOutputStream(new File(uploadDir
          + (uploadDir.charAt(uploadDir.length() - 1) == '/' ? "" : "/")
          + contentDispositionHeader.getFileName()));
      while ((read = fileInputStream.read(bytes)) != -1) {
        outputStream.write(bytes, 0, read);
      }
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      handleException(e, "uploading a file");

    }
    return null;

  }

  @Override
  @DELETE
  @Path("delete/{fileName}")
  public void deleteFile(
    @ApiParam(value = "Name of file to delete, e.g. filename.txt", required = true) @PathParam("fileName") String fileName, 
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
  throws Exception {
    String uploadDir =
        ConfigUtility.getConfigProperties().getProperty("upload.dir");
    
    try {
      File dir = new File(uploadDir);

      File[] files = dir.listFiles();
      for (File f : files) {
        if (f.getName().equals(fileName)) {
          f.delete();
        }
      }
    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
    }
  }

  @Override
  @GET
  @Path("/list")
  @ApiOperation(value = "Get uploaded file details", notes = "Returns list of details for uploaded files", response = StringList.class)
  public StringList getUploadedFilesDetails(
    @ApiParam(value = "Paging/filtering/sorting object", required = false) PfsParameter pfsParameter,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {

    String uploadDir =
        ConfigUtility.getConfigProperties().getProperty("upload.dir");

    StringList fileNames = new StringList();

    try {
      File dir = new File(uploadDir);

      File[] files = dir.listFiles();
      for (File f : files) {
        fileNames.addObject(f.getName());
      }
      return fileNames;
    } catch (Exception e) {
      handleException(e, "retrieving uploaded file list");
      return fileNames;
    }
    

  }

}