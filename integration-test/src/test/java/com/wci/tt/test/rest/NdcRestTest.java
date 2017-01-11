/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
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
import com.wci.tt.rest.client.TransformClientRest;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.StringList;

/**
 * Some initial testing for {@link TransformClientRest}. Assumes stock dev load.
 */
public class NdcRestTest extends RestIntegrationSupport {

  /** The transform transformService. */
  protected static NdcClientRest ndcService;

  /** The properties. */
  protected static Properties properties;

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

  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {
    // n/a
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @After
  public void teardown() throws Exception {
    // n/a
  }

  /**
   * Test cases where call to process expected to return proper results.
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetNdcInfo() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    String ndc = "00247100552";
    NdcModel results = ndcService.getNdcInfo(ndc, true, "guest");
    Logger.getLogger(getClass()).info(" results = " + results);
    assertEquals(ndc, results.getNdc());
    assertTrue(results.isActive());
    assertEquals("91349", results.getRxcui());
    assertEquals("Hydrogen Peroxide 30 MG/ML Topical Solution",
        results.getRxcuiName());
    assertNull(null, results.getSplSetId());
    assertEquals(3, results.getHistory().size());

    // NDC that exists only in the first terminology version (with history)
    ndc = "49452360601";
    results = ndcService.getNdcInfo(ndc, true, "guest");
    Logger.getLogger(getClass()).info(" results = " + results);
    assertEquals(ndc, results.getNdc());
    assertFalse(results.isActive());
    assertEquals("91348", results.getRxcui());
    assertEquals("Hydrogen Peroxide 300 MG/ML Topical Solution",
        results.getRxcuiName());
    assertNull(results.getSplSetId());
    assertEquals(1, results.getHistory().size());

    // (without history)
    ndc = "49452360601";
    results = ndcService.getNdcInfo(ndc, false, "guest");
    Logger.getLogger(getClass()).info(" results = " + results);
    assertEquals(ndc, results.getNdc());
    assertFalse(results.isActive());
    assertEquals("91348", results.getRxcui());
    assertEquals("Hydrogen Peroxide 300 MG/ML Topical Solution",
        results.getRxcuiName());
    assertNull(results.getSplSetId());
    assertEquals(0, results.getHistory().size());

    // NDC that exists only in the third terminology version
    ndc = "69315020906";
    results = ndcService.getNdcInfo(ndc, true, "guest");
    Logger.getLogger(getClass()).info(" results = " + results);
    assertEquals(ndc, results.getNdc());
    assertTrue(results.isActive());
    assertEquals("1744400", results.getRxcui());
    assertEquals("40c18435-ae27-4740-b69e-6bcd1aec3212", results.getSplSetId());
    assertEquals(1, results.getHistory().size());

    // An NDC that exists in multiple versions but changes RXCUI along the way
    ndc = "00143314501";
    results = ndcService.getNdcInfo(ndc, true, "guest");
    Logger.getLogger(getClass()).info(" results = " + results);
    assertEquals(ndc, results.getNdc());
    assertTrue(results.isActive());
    assertEquals("1116191", results.getRxcui());
    assertEquals("b63d6ef1-ccce-4cd7-80f7-6d674dbf432f", results.getSplSetId());
    assertEquals(2, results.getHistory().size());

    // NDC that doesn't exist
    ndc = "5555555";
    results = ndcService.getNdcInfo(ndc, true, "guest");
    Logger.getLogger(getClass()).info(" results = " + results);
    assertNull(ndc, results.getNdc());
    assertNull(results.getRxcui());
    assertNull(results.getRxcuiName());
    assertNull(results.getSplSetId());
    assertEquals(0, results.getHistory().size());

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
    String rxcui = "283420";
    RxcuiModel results = ndcService.getRxcuiInfo(rxcui, true, "guest");
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(rxcui, results.getRxcui());
    // This is the most recent name the concept had when it was active
    assertEquals("Hydrogen Peroxide 30 MG/ML Topical Spray",
        results.getRxcuiName());
    // These are all the NDCs ever associated with this RXCUI (one entry per
    // NDC)
    assertEquals(25, results.getHistory().size());
    // These are the SPL_SET_IDs associated with the CURRENT version.
    assertEquals(14, results.getSplSetIds().size());

    // Invalid rxcui
    rxcui = "5555ddd";
    results = ndcService.getRxcuiInfo(rxcui, true, "guest");
    Logger.getLogger(getClass()).info("  results = " + results);
    assertNull(results.getRxcui());
    assertNull(results.getRxcuiName());
    assertEquals(0, results.getSplSetIds().size());
    assertEquals(0, results.getHistory().size());

    // Rxcui that exists only in the first terminology version (with history)
    rxcui = "351772";
    results = ndcService.getRxcuiInfo(rxcui, true, "guest");
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(rxcui, results.getRxcui());
    assertEquals("Azithromycin 100 MG/ML Injectable Solution [Zithromax]",
        results.getRxcuiName());
    assertEquals(0, results.getSplSetIds().size());
    assertEquals(8, results.getHistory().size());

    // Without history
    rxcui = "351772";
    results = ndcService.getRxcuiInfo(rxcui, false, "guest");
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(rxcui, results.getRxcui());
    assertEquals("Azithromycin 100 MG/ML Injectable Solution [Zithromax]",
        results.getRxcuiName());
    assertEquals(0, results.getSplSetIds().size());
    assertEquals(0, results.getHistory().size());

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
    String ndc = "61010-5400-2";
    NdcPropertiesModel results = ndcService.getNdcProperties(ndc, "guest");
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals("283420", results.getRxcui());
    assertEquals("Hydrogen Peroxide 30 MG/ML Topical Spray",
        results.getRxcuiName());
    assertEquals("61010540002", results.getNdc11());
    assertEquals("61010-5400-2", results.getNdc10());
    assertEquals("61010-5400", results.getNdc9());
    assertEquals("a665e2ce-c7e1-4d79-aa58-da7595944e7f", results.getSplSetId());
    assertEquals(7, results.getPropertyList().size());

    ndc = "0069-3150-83";
    results = ndcService.getNdcProperties(ndc, "guest");
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals("1668240", results.getRxcui());
    assertEquals("Azithromycin 500 MG Injection [Zithromax]",
        results.getRxcuiName());
    assertEquals("0069-3150-83", results.getNdc10());
    assertEquals("0069-3150", results.getNdc9());
    assertEquals("00069315083", results.getNdc11());
    assertEquals(7, results.getPropertyList().size());
    assertEquals("3b631aa1-2d46-40bc-a614-d698301ea4f9", results.getSplSetId());

    // Test NDC that is not in current version
    ndc = "49452360601";
    results = ndcService.getNdcProperties(ndc, "guest");
    Logger.getLogger(getClass()).info("  results = " + results);
    assertNull(results.getRxcui());
    assertNull(results.getRxcuiName());
    assertEquals(null, results.getNdc10());
    assertEquals(null, results.getNdc9());
    assertEquals(null, results.getNdc11());
    assertEquals(0, results.getPropertyList().size());
    assertEquals(null, results.getSplSetId());

  }

  /**
   * Test get ndc properties for SPL_SET_ID.
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetNdcPropertiesForSplSetId() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Get SPL_SET_ID from current version
    String splSetId = "8d24bacb-feff-4c6a-b8df-625e1435387a";

    NdcPropertiesListModel results =
        ndcService.getNdcPropertiesForSplSetId(splSetId, "guest");
    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(2, results.getList().size());
    assertEquals("1668240", results.getList().get(0).getRxcui());
    assertEquals("8d24bacb-feff-4c6a-b8df-625e1435387a",
        results.getList().get(0).getSplSetId());
    assertEquals(7, results.getList().get(0).getPropertyList().size());

    // Get SPL_SET_ID only in previous version
    splSetId = "led3c03f4-2829-423f-a595-dc09c9cc0e40";
    results = ndcService.getNdcPropertiesForSplSetId(splSetId, "guest");
    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.getList().size());

  }

  /**
   * Test autocomlete.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAutocomplete() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    StringList results = ndcService.autocomplete("247", "guest");
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(20, results.getObjects().size());
    assertEquals(33, results.getTotalCount());

    results = ndcService.autocomplete("asp", "guest");
    Logger.getLogger(getClass()).info("  results = " + results);
    assertEquals(19, results.getObjects().size());
    assertEquals(20, results.getTotalCount());

  }

  /**
   * Test get ndc info batch.
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetNdcInfoBatch() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    List<String> ndcs = new ArrayList<>();
    ndcs.add("00247100552");
    ndcs.add("49452360601");
    ndcs.add("69315020906");
    ndcs.add("00143314501");
    ndcs.add("5555555");

    List<NdcModel> resultsList = ndcService.getNdcInfoBatch(ndcs, true, "guest");
    Logger.getLogger(getClass()).info(" results = " + resultsList.getClass() + " " + resultsList);

    NdcModel results = resultsList.get(0);
    String ndc = "00247100552";
    assertEquals(ndc, results.getNdc());
    assertTrue(results.isActive());
    assertEquals("91349", results.getRxcui());
    assertEquals("Hydrogen Peroxide 30 MG/ML Topical Solution",
        results.getRxcuiName());
    assertNull(null, results.getSplSetId());
    assertEquals(3, results.getHistory().size());

    // NDC that exists only in the first terminology version (with history)
    results = resultsList.get(1);
    ndc = "49452360601";
    assertEquals(ndc, results.getNdc());
    assertFalse(results.isActive());
    assertEquals("91348", results.getRxcui());
    assertEquals("Hydrogen Peroxide 300 MG/ML Topical Solution",
        results.getRxcuiName());
    assertNull(results.getSplSetId());
    assertEquals(1, results.getHistory().size());

    // NDC that exists only in the third terminology version
    results = resultsList.get(2);
    ndc = "69315020906";
    assertEquals(ndc, results.getNdc());
    assertTrue(results.isActive());
    assertEquals("1744400", results.getRxcui());
    assertEquals("40c18435-ae27-4740-b69e-6bcd1aec3212", results.getSplSetId());
    assertEquals(1, results.getHistory().size());

    // An NDC that exists in multiple versions but changes RXCUI along the way
    results = resultsList.get(3);
    ndc = "00143314501";
    assertEquals(ndc, results.getNdc());
    assertTrue(results.isActive());
    assertEquals("1116191", results.getRxcui());
    assertEquals("b63d6ef1-ccce-4cd7-80f7-6d674dbf432f", results.getSplSetId());
    assertEquals(2, results.getHistory().size());

    // NDC that doesn't exist
    results = resultsList.get(4);
    ndc = "5555555";
    assertNull(ndc, results.getNdc());
    assertNull(results.getRxcui());
    assertNull(results.getRxcuiName());
    assertNull(results.getSplSetId());
    assertEquals(0, results.getHistory().size());

  }
  
  /**
   * Test get rxcui info batch.
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetRxcuiInfoBatch() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Input Data
    List<String> rxcuis = new ArrayList<>();
    rxcuis.add("283420");
    rxcuis.add("5555ddd");
    rxcuis.add("351772");
    
    List<RxcuiModel> resultsList = ndcService.getRxcuiInfoBatch(rxcuis, true, "guest");
    Logger.getLogger(getClass()).info(" results = " + resultsList.getClass() + " " + resultsList);

    RxcuiModel results = resultsList.get(0);
    String rxcui = "283420";
    assertEquals(rxcui, results.getRxcui());
    // This is the most recent name the concept had when it was active
    assertEquals("Hydrogen Peroxide 30 MG/ML Topical Spray",
        results.getRxcuiName());
    // These are all the NDCs ever associated with this RXCUI (one entry per
    // NDC)
    assertEquals(25, results.getHistory().size());
    // These are the SPL_SET_IDs associated with the CURRENT version.
    assertEquals(14, results.getSplSetIds().size());

    // Invalid rxcui
    results = resultsList.get(1);
    rxcui = "5555ddd";
    assertNull(results.getRxcui());
    assertNull(results.getRxcuiName());
    assertEquals(0, results.getSplSetIds().size());
    assertEquals(0, results.getHistory().size());

    // Rxcui that exists only in the first terminology version (with history)
    results = resultsList.get(2);
    rxcui = "351772";
    assertEquals(rxcui, results.getRxcui());
    assertEquals("Azithromycin 100 MG/ML Injectable Solution [Zithromax]",
        results.getRxcuiName());
    assertEquals(0, results.getSplSetIds().size());
    assertEquals(8, results.getHistory().size());

  }
}
