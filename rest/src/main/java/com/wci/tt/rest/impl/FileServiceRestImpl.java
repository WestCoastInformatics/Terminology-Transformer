/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.wci.tt.UserRole;
import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.jpa.UserJpa;
import com.wci.tt.jpa.services.SecurityServiceJpa;
import com.wci.tt.jpa.services.rest.FileServiceRest;
import com.wci.tt.services.SecurityService;
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
public class FileServiceRestImpl extends RootServiceRestImpl implements
    FileServiceRest {


  /* see superclass */
  @Override
  @PUT
  @Path("/file/upload")
  @Consumes({
    MediaType.MULTIPART_FORM_DATA
  })
  @ApiOperation(value = "Authenticate a user", notes = "Performs authentication on specified userName/password and returns the corresponding user with authToken included. Throws 401 error if not", response = UserJpa.class)
  public void saveFile(
    @ApiParam(value = "File to save as multi-part form data", required = true) File file,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
  throws Exception {

    Logger.getLogger(getClass())
        .info(
            "RESTful call (Authentication): /file/upload");
    final SecurityService securityService = new SecurityServiceJpa();
    try {
      authorizeApp(securityService, authToken, "upload a file",
          UserRole.VIEWER);
   
      // TODO Handle file here
    } catch (Exception e) {
      handleException(e, "trying to retrieve a user");
    } finally {
      securityService.close();
    }

  }

  @Override
  public void deleteFile(String fileName, String authToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void getFileNames(PfsParameter pfsParameter, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }


  

}