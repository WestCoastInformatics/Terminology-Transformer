/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.rest;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.RxcuiModel;
import com.wci.tt.rest.client.NdcClientRest;
import com.wci.tt.rest.client.TransformClientRest;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.rest.client.SecurityClientRest;

/**
 * Some initial testing for {@link TransformClientRest}. Assumes stock dev load.
 */
public class NdcRestTest extends RestIntegrationSupport {

  /** The adminAuthToken auth token. */
  protected static String adminAuthToken;

  /** The transform transformService. */
  protected static NdcClientRest ndcService;

  /** The security transformService. */
  protected static SecurityClientRest securityService;

  /** The properties. */
  protected static Properties properties;

  /** The test adminAuthToken username. */
  protected static String adminUser;

  /** The test adminAuthToken password. */
  protected static String adminPassword;

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {

    // instantiate properties
    properties = ConfigUtility.getConfigProperties();

    // instantiate required services
    ndcService = new NdcClientRest(properties);
    securityService = new SecurityClientRest(properties);

    // test run.config.ts has adminAuthToken user
    adminUser = properties.getProperty("admin.user");
    adminPassword = properties.getProperty("admin.password");

    if (adminUser == null || adminUser.isEmpty()) {
      throw new Exception(
          "Test prerequisite: adminAuthToken.user must be specified");
    }
    if (adminPassword == null || adminPassword.isEmpty()) {
      throw new Exception(
          "Test prerequisite: adminAuthToken.password must be specified");
    }
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {

    // authentication
    adminAuthToken =
        securityService.authenticate(adminUser, adminPassword).getAuthToken();
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @After
  public void teardown() throws Exception {
    // logout
    securityService.logout(adminAuthToken);
  }



  /**
   * Test cases where call to process expected to return proper results.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNdcToRxcuiConversion() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    String inputString = "00247100552";

    NdcModel results =
        ndcService.getNdcInfo(inputString, 
            adminAuthToken);

    Logger.getLogger(getClass()).info("  results = " + results);
   
  }

  /**
   * Test rxcui to ndc conversion.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRxcuiToNdcConversion() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    String inputString = "283420";

    RxcuiModel results =
        ndcService.getRxcuiInfo(inputString, 
            adminAuthToken);

    Logger.getLogger(getClass()).info("  results = " + results);
   
  }
  
  /**
   * Test get ndc properties.
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetNdcProperties() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    String inputString = "61010-5400-2";

    NdcPropertiesModel results =
        ndcService.getNdcProperties(inputString, 
            adminAuthToken);

    Logger.getLogger(getClass()).info("  results = " + results);
   
  }
}
