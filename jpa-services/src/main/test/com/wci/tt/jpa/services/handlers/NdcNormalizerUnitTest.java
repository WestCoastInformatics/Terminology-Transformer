/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.umls.server.helpers.CancelException;

/**
 * Unit testing for {@link CancelException}.
 */
public class NdcNormalizerUnitTest {

  /** The ndc normalizer. */
  private NdcNormalizer ndcNormalizer;

  /**
   * Setup class.
   */
  @BeforeClass
  public static void setupClass() {
    // do nothing
  }

  /**
   * Setup.
   */
  @Before
  public void setup() {
    ndcNormalizer = new NdcNormalizer();
  }

  /**
   * Test normalizer.
   * @throws Exception the exception
   */
  @Test
  public void testHelperNormalize() throws Exception {
    Logger.getLogger(getClass()).info("TEST NdcNormalizer normalize");

    List<ScoredResult> normStr;

    // 11 digit NDC HIPPA - pass through
    normStr = ndcNormalizer.normalize("16571043111", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("16571043111"));

    // 12 digit NDC VANDF - remove leading 0
    normStr = ndcNormalizer.normalize("016571043111", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("16571043111"));

    // 6-4-2
    normStr = ndcNormalizer.normalize("123456-1234-12", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("23456123412"));

    // 6-4-1
    normStr = ndcNormalizer.normalize("123456-1234-1", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("23456123401"));

    // 6-3-2
    normStr = ndcNormalizer.normalize("123456-123-12", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("23456012312"));

    // 6-3-1
    normStr = ndcNormalizer.normalize("123456-123-1", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("23456012301"));

    // 5-4-2
    normStr = ndcNormalizer.normalize("12345-1234-12", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("12345123412"));

    // 5-4-1
    normStr = ndcNormalizer.normalize("12345-1234-1", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("12345123401"));

    // 5-3-2
    normStr = ndcNormalizer.normalize("12345-123-12", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("12345012312"));

    // 4-4-2
    normStr = ndcNormalizer.normalize("1234-1234-12", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("01234123412"));

    // contains '*' MTHFDA - replace with 0
    normStr = ndcNormalizer.normalize("*1234-1234-12", new DataContextJpa());
    assertTrue(normStr.get(0).getValue().equals("01234123412"));

    // 10 digit no hyphens - invalid // TODO throw exception? warn?
    try {
      normStr = ndcNormalizer.normalize("1657104311", new DataContextJpa());
      fail("10 digits no hyphens is invalid and should throw exception");
    } catch (Exception e) {
      // do nothing
    }

    // more than 2 hyphens - invalid
    try {
      normStr = ndcNormalizer.normalize("165-710-431-1", new DataContextJpa());
      fail("more than 2 hyphens is invalid and should throw exception");
    } catch (Exception e) {
      // do nothing
    }
  }

  /**
   * Test degenerate use of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperDegenerateUse001() throws Exception {
    // n/a
  }

  /**
   * Test edge cases of the helper object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testHelperEdgeCases001() throws Exception {
    // n/a
  }

  /**
   * Teardown.
   */
  @After
  public void teardown() {
    // do nothing
  }

  /**
   * Teardown class.
   */
  @AfterClass
  public static void teardownClass() {
    // do nothing
  }

}
