/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.StringList;

/**
 * Represents a security available via a REST service.
 */
public interface SourceDataServiceRest {

  public void deleteFile(String fileName, String authToken) throws Exception;
  
  public StringList getUploadedFilesDetails(PfsParameter pfsParameter, String authToken) throws Exception;

  public String saveFile(InputStream fileInputStream,
    FormDataContentDisposition contentDispositionHeader, boolean unzip)
      throws Exception;
}