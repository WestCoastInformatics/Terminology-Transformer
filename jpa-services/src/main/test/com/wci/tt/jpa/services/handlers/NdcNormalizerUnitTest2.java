/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.JpaSupport;
import com.wci.tt.services.handlers.NormalizerHandler;

/**
 * Some initial testing for {@link DefaultNormalizer}. Assumes stock dev load.
 */
public class NdcNormalizerUnitTest2 extends JpaSupport {

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
   * Test various forms of NDC.
   *
   * @throws Exception the exception
   */
  @Test
  public void testNormal() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    NormalizerHandler handler = new NdcNormalizer();

    List<ScoredResult> results = handler.normalize("12345678910", null);
    assertEquals(1, results.size());
    assertEquals("12345678910", results.get(0).getValue());

    // with *
    results = handler.normalize("*2345678910", null);
    assertEquals(1, results.size());
    assertEquals("02345678910", results.get(0).getValue());

    // 4-4-2
    results = handler.normalize("4444-4444-22", null);
    assertEquals(1, results.size());
    assertEquals("04444444422", results.get(0).getValue());

    // 5-3-2
    results = handler.normalize("55555-333-22", null);
    assertEquals(1, results.size());
    assertEquals("55555033322", results.get(0).getValue());

    // 5-4-1
    results = handler.normalize("55555-4444-1", null);
    assertEquals(1, results.size());
    assertEquals("55555444401", results.get(0).getValue());

    // 6-4-2
    results = handler.normalize("066666-4444-22", null);
    assertEquals(1, results.size());
    assertEquals("66666444422", results.get(0).getValue());

    // 6-4-1
    results = handler.normalize("066666-4444-1", null);
    assertEquals(1, results.size());
    assertEquals("66666444401", results.get(0).getValue());

    // 6-3-2
    results = handler.normalize("066666-333-22", null);
    assertEquals(1, results.size());
    assertEquals("66666033322", results.get(0).getValue());

    // 6-3-1
    results = handler.normalize("066666-333-1", null);
    assertEquals(1, results.size());
    assertEquals("66666033301", results.get(0).getValue());

    // 5-4-2
    results = handler.normalize("55555-4444-22", null);
    assertEquals(1, results.size());
    assertEquals("55555444422", results.get(0).getValue());

    // 5-4-1
    results = handler.normalize("55555-4444-1", null);
    assertEquals(1, results.size());
    assertEquals("55555444401", results.get(0).getValue());

    // 5-3-2
    results = handler.normalize("55555-333-22", null);
    assertEquals(1, results.size());
    assertEquals("55555033322", results.get(0).getValue());

    // 4-4-2
    results = handler.normalize("4444-4444-22", null);
    assertEquals(1, results.size());
    assertEquals("04444444422", results.get(0).getValue());

  }

  /**
   * Test degenerate.
   *
   * @throws Exception the exception
   */
  @Test
  public void testDegenerate() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    NormalizerHandler handler = new NdcNormalizer();

    List<ScoredResult> results = handler.normalize("1234567891000", null);
    assertEquals(0, results.size());
    results = handler.normalize("123456789", null);
    assertEquals(0, results.size());

    results = handler.normalize("", null);
    assertEquals(0, results.size());

    results = handler.normalize(null, null);
    assertEquals(0, results.size());

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
