/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.rest;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesListModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.RxcuiModel;
import com.wci.tt.rest.client.NdcClientRest;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.StringList;
import com.wci.umls.server.rest.client.SecurityClientRest;

/**
 * Some initial testing for NDC related services. Independent testing by BAC.
 */
public class NdcRestTest2 extends RestIntegrationSupport {

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
  public void testgetNdcInfo() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    String ndc = "59630020510";
    NdcModel results = ndcService.getNdcInfo(ndc, adminAuthToken);
    Logger.getLogger(getClass()).info("  results = " + results.getModelValue());

    String ndc2 = "59630-0205-10";
    NdcModel results2 = ndcService.getNdcInfo(ndc2, adminAuthToken);
    Logger.getLogger(getClass())
        .info("  results2 = " + results2.getModelValue());

    assertEquals(results, results2);

  }

  /**
   * Test rxcui to ndc conversion.
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetRxcuiInfo() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    String rxcui = "102166";
    RxcuiModel results = ndcService.getRxcuiInfo(rxcui, adminAuthToken);
    Logger.getLogger(getClass()).info("  results = " + results.getModelValue());

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
    final String ndc = "59630020510";
    final NdcPropertiesModel results =
        ndcService.getNdcProperties(ndc, adminAuthToken);
    Logger.getLogger(getClass()).info("  results = " + results.getModelValue());

    final String ndc2 = "59630-0205-10";
    final NdcPropertiesModel results2 =
        ndcService.getNdcProperties(ndc2, adminAuthToken);
    Logger.getLogger(getClass())
        .info("  results2 = " + results2.getModelValue());

    final String ndc3 = "00069315083";
    final NdcPropertiesModel results3 =
        ndcService.getNdcProperties(ndc3, adminAuthToken);
    Logger.getLogger(getClass())
        .info("  results3 = " + results3.getModelValue());

  }

  /**
   * Test get ndc properties for SPL_SET_ID
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetNdcPropertiesForSplSetId() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    String splSetId = "bd65ee5e-2000-423c-b0a6-72eb213455c4";
    NdcPropertiesListModel results =
        ndcService.getNdcPropertiesForSplSetId(splSetId, adminAuthToken);
    Logger.getLogger(getClass()).info("  results = " + results.getModelValue());

  }

  /**
   * Test autocomlete.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAutocomplete() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Test something that works
    StringList results = ndcService.autocomplete("247", adminAuthToken);
    Logger.getLogger(getClass()).info("  247 results = " + results);
    assertEquals(20, results.getCount());

    // Test something that doesn't work
    results = ndcService.autocomplete("ABC", adminAuthToken);
    Logger.getLogger(getClass()).info("  ABC results = " + results);
    assertEquals(0, results.getCount());
    assertEquals(0, results.getTotalCount());
  }
}
