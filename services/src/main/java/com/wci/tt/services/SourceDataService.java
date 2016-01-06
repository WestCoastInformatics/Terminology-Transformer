/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services;

import java.io.File;

import com.wci.tt.helpers.PfsParameter;

/**
 * Generically represents a service for accessing {@link Project} information.
 */
public interface SourceDataService extends RootService {

  public void uploadFile(File file, String authToken);
  
  public void getFileNames(PfsParameter pfsParameter, String authToken);
  
  public void deleteFile(String fileName, String authToken);

}