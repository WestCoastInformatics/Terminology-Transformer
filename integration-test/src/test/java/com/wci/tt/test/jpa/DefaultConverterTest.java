/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.jpa.helpers.DataContextJpa;
import com.wci.tt.jpa.services.handlers.DefaultConverter;
import com.wci.tt.services.handlers.ConverterHandler;

/**
 * Some initial testing for {@link DefaultConverter}. Assumes stock dev
 * load.
 */
public class DefaultConverterTest extends JpaSupport {

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

    ConverterHandler handler = new DefaultConverter();
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

    ConverterHandler handler = new DefaultConverter();
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

    ConverterHandler handler = new DefaultConverter();

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
   * Test null inputs on convert routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConvertNullInputs() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ConverterHandler handler = new DefaultConverter();
    DataContextTuple results = handler.convert(null, null, null);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertNotNull(results);
    assertNull(results.getData());
    assertNull(results.getDataContext());
  }

  /**
   * Test empty input on convert routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConvertEmptyContent() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ConverterHandler handler = new DefaultConverter();
    DataContextTuple results =
        handler.convert("", new DataContextJpa(), new DataContextJpa());

    Logger.getLogger(getClass()).info("  results = " + results);

    assertNotNull(results);
    assertNull(results.getData());
    assertNull(results.getDataContext());
  }

  /**
   * Test filled out input on convert routine.
   *
   * @throws Exception the exception
   */
  @Test
  public void testConvertFullInput() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    ConverterHandler handler = new DefaultConverter();

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

    DataContextTuple results =
        handler.convert(inputString, inputContext, outputContext);

    Logger.getLogger(getClass()).info("  results = " + results);

    assertNotNull(results);
    assertNotNull(results.getData());
    assertNotNull(results.getDataContext());

    assertEquals(inputString, results.getData());
    assertEquals(outputContext, results.getDataContext());
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
