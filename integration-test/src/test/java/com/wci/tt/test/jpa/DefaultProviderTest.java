/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.DataContextJpa;
import com.wci.tt.jpa.services.handlers.DefaultProvider;
import com.wci.tt.services.handlers.ProviderHandler;

/**
 * Some initial testing for {@link DefaultProvider}. Assumes stock dev
 * load.
 */
public class DefaultProviderTest extends JpaSupport {

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
   * Test null inputs on accepts routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAcceptNullInputs() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();
    List<DataContext> results = handler.accepts(null);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(1, results.size());
  }

  /**
   * Test empty input on accepts routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAcceptEmptyContent() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();
    List<DataContext> results = handler.accepts(new DataContextJpa());

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(1, results.size());
  }

  /**
   * Test filled out input on accepts routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testAcceptFullInput() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();

    DataContextJpa inputContext = new DataContextJpa();
    inputContext.setCustomer("Test Input Customer");
    inputContext.setInfoModelName("Test Input Information Model Name");
    inputContext.setSemanticType("Test Input Semantic Type");
    inputContext.setSpecialty("Test Input Specialty");
    inputContext.setTerminology("Test Input Terminology");
    inputContext.setVersion("Test Input Version");
    inputContext.setType(DataContextType.CODE);

    List<DataContext> results = handler.accepts(inputContext);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertNotNull(results);
    assertEquals(1, results.size());

    DataContext result = results.get(0);
    assertEquals(inputContext, result);
  }

  /**
   * Test null inputs on identify routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testIdentifyNullInputs() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();
    List<ScoredDataContext> results = handler.identify(null, null);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.size());
  }

  /**
   * Test null input data with empty context on identify routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testIdentifyNullData() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();
    List<ScoredDataContext> results =
        handler.identify(null, new DataContextJpa());

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.size());
  }

  /**
   * Test empty input data with null context on identify routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testIdentifyEmptyDataNullContext() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();
    List<ScoredDataContext> results = handler.identify("", null);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.size());
  }

  /**
   * Test empty input data with empty context on identify routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testIdentifyEmptyInput() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();
    List<ScoredDataContext> results =
        handler.identify("", new DataContextJpa());

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.size());
  }

  /**
   * Test filled input data with null context on identify routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testIdentifyFilledDataNullContext() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    String inputString = "test string";

    ProviderHandler handler = new DefaultProvider();
    List<ScoredDataContext> results = handler.identify(inputString, null);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(1, results.size());
  }

  /**
   * Test filled input data with empty context on identify routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testIdentifyFilledDataEmptyContext() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    String inputString = "test string";

    ProviderHandler handler = new DefaultProvider();
    List<ScoredDataContext> results =
        handler.identify(inputString, new DataContextJpa());

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(1, results.size());
  }

  /**
   * Test filled out input on identify routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testIdentifyFullInput() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();

    String inputString = "test string";

    DataContextJpa inputContext = new DataContextJpa();
    inputContext.setCustomer("Test Input Customer");
    inputContext.setInfoModelName("Test Input Information Model Name");
    inputContext.setSemanticType("Test Input Semantic Type");
    inputContext.setSpecialty("Test Input Specialty");
    inputContext.setTerminology("Test Input Terminology");
    inputContext.setVersion("Test Input Version");
    inputContext.setType(DataContextType.CODE);

    List<ScoredDataContext> results =
        handler.identify(inputString, inputContext);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(1, results.size());

    ScoredDataContext result = results.get(0);
    assertEquals(inputContext.getCustomer(), result.getCustomer());
    assertEquals(inputContext.getInfoModelName(), result.getInfoModelName());
    assertEquals(inputContext.getSemanticType(), result.getSemanticType());
    assertEquals(inputContext.getSpecialty(), result.getSpecialty());
    assertEquals(inputContext.getTerminology(), result.getTerminology());
    assertEquals(inputContext.getVersion(), result.getVersion());
    assertEquals(inputContext.getType(), result.getType());
    assertTrue(result.getScore() == 1f);
  }

  /**
   * Test null inputs on process routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testProcessNullInputs() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();
    List<ScoredResult> results = handler.process(null, null, null);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.size());
  }

  /**
   * Test empty input on process routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testProcessEmptyContent() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();

    List<ScoredResult> results =
        handler.process("", new DataContextJpa(), new DataContextJpa());

    Logger.getLogger(getClass()).info("  results = " + results);

    assertEquals(0, results.size());
  }

  /**
   * Test filled out input on process routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testProcessFullInput() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ProviderHandler handler = new DefaultProvider();

    String inputString = "test string";

    DataContextJpa inputContext = new DataContextJpa();
    inputContext.setCustomer("Test Input Customer");
    inputContext.setInfoModelName("Test Input Information Model Name");
    inputContext.setSemanticType("Test Input Semantic Type");
    inputContext.setSpecialty("Test Input Specialty");
    inputContext.setTerminology("Test Input Terminology");
    inputContext.setVersion("Test Input Version");
    inputContext.setType(DataContextType.CODE);

    DataContextJpa outputContext = new DataContextJpa();
    outputContext.setCustomer("Test Output Customer");
    outputContext.setInfoModelName("Test Output Information Model Name");
    outputContext.setSemanticType("Test Output Semantic Type");
    outputContext.setSpecialty("Test Output Specialty");
    outputContext.setTerminology("Test Output Terminology");
    outputContext.setVersion("Test Output Version");
    outputContext.setType(DataContextType.CODE);

    List<ScoredResult> results =
        handler.process(inputString, inputContext, outputContext);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertNotNull(results);
    assertEquals(1, results.size());

    ScoredResult result = results.get(0);

    assertEquals(inputString, result.getValue());
    assertTrue(result.getScore() == 1f);
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
