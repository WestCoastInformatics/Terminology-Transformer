/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.examples;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.wci.tt.rest.client.NdcClientRest;

/**
 * Examples of making calls to the NdcServerRestImpl API using the client
 * classes. Sample "curl" calls are also provided
 */
public class NdcServiceRestExamples {

  /** The name. */
  @Rule
  public TestName name = new TestName();

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    // do nothing
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    // n/a
  }

  /**
   * Test looking up NDC information and RXCUI history.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNdcInfo() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Setup
    final Properties props = new Properties();
    props.setProperty("base.url", "https://ndc.terminology.tools");
    final NdcClientRest client = new NdcClientRest(props);

    // Call with RXNORM-style NDC (without history)
    // curl -H Authorization:guest
    // https://ndc.terminology.tools/rxnorm/ndc/00143314501
    client.getNdcInfo("00143314501", false, "guest");

    // Call with RXNORM-style NDC (with history)
    // curl -H Authorization:guest
    // https://ndc.terminology.tools/rxnorm/ndc/00143314501?history=true
    client.getNdcInfo("00143314501", true, "guest");

    // Call with SPL-style NDC (without history)
    // curl -H Authorization:guest
    // https://ndc.terminology.tools/rxnorm/ndc/0143-3145-01
    client.getNdcInfo("0143-3145-01", false, "guest");

    // Call with SPL-style NDC (with history)
    // curl -H Authorization:guest
    // https://ndc.terminology.tools/rxnorm/ndc/0143-3145-01?history=true
    client.getNdcInfo("0143-3145-01", true, "guest");

  }

  /**
   * Test looking up NDC properties from the current RXNORM version.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNdcProperties() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Setup
    final Properties props = new Properties();
    props.setProperty("base.url", "https://ndc.terminology.tools");
    final NdcClientRest client = new NdcClientRest(props);

    // Call with RXNORM-style NDC
    // curl -H Authorization:guest
    // https://ndc.terminology.tools/rxnorm/ndc/00143314501/properties
    client.getNdcProperties("00143314501", "guest");

    // Call with SPL-style NDC
    // curl -H Authorization:guest
    // https://ndc.terminology.tools/rxnorm/ndc/0143-3145-01/properties
    client.getNdcProperties("0143-3145-01", "guest");

  }

  /**
   * Test looking up RXCUI information and NDC history.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRxcuiInfo() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Setup
    final Properties props = new Properties();
    props.setProperty("base.url", "https://ndc.terminology.tools");
    final NdcClientRest client = new NdcClientRest(props);

    // Call with RXCUI (without history)
    // curl -H Authorization:guest
    // https://ndc.terminology.tools/rxnorm/rxcui/351772
    client.getRxcuiInfo("351772", false, "guest");

    // Call with RXCUI (with history)
    // curl -H Authorization:guest
    // https://ndc.terminology.tools/rxnorm/rxcui/351772?history=true
    client.getRxcuiInfo("351772", true, "guest");

  }

  /**
   * Test looking up NDC properties list info for an SPL Set Id.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNdcPropertiesForSplSetId() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Setup
    final Properties props = new Properties();
    props.setProperty("base.url", "https://ndc.terminology.tools");
    final NdcClientRest client = new NdcClientRest(props);

    // Call with SPL_SET_ID
    // curl -H Authorization:guest
    // https://ndc.terminology.tools/rxnorm/spl/8d24bacb-feff-4c6a-b8df-625e1435387a/ndc/properties
    client.getNdcPropertiesForSplSetId("8d24bacb-feff-4c6a-b8df-625e1435387a",
        "guest");

  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @After
  public void teardown() throws Exception {
    // n/a
  }

  /**
   * Teardown class.
   *
   * @throws Exception the exception
   */
  @AfterClass
  public static void teardownClass() throws Exception {
    // do nothing
  }
}
