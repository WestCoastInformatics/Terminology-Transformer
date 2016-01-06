/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import java.io.File;

import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.services.SourceDataService;
import com.wci.tt.services.SecurityService;

/**
 * Reference implementation of the {@link SecurityService}.
 */
public class SourceDataServiceJpa extends RootServiceJpa implements
    SourceDataService {

  public SourceDataServiceJpa() throws Exception {
    super();
    // TODO Auto-generated constructor stub
  }


  @Override
  public void uploadFile(File file, String authToken) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void getFileNames(PfsParameter pfsParameter, String authToken) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void deleteFile(String fileName, String authToken) {
    // TODO Auto-generated method stub
    
  }
}
