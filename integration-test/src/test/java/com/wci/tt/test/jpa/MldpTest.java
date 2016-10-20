/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.jpa;

import static org.junit.Assert.assertEquals;

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
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.services.CoordinatorServiceJpa;
import com.wci.tt.services.CoordinatorService;

/**
 * Some initial testing for {@link CoordinatorServiceJpa}. Assumes stock dev
 * load.
 */
public class MldpTest extends JpaSupport {

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
   * Test conditions.
   *
   * @throws Exception the exception
   */
  @Test
  public void testSingleCondition() throws Exception {
    Logger.getLogger(getClass()).info("TEST " + name.getMethodName());

    // Service
    final CoordinatorService service = new CoordinatorServiceJpa();

    // Input context
    final DataContext inputContext = new DataContextJpa();
    inputContext.setType(DataContextType.NAME);
    inputContext.setSemanticType("Condition");

    // Input string
    final String inputString = "Right knee pain";

    final List<ScoredDataContext> contexts =
        service.identify(inputString, inputContext);
    assertEquals(1, contexts.size());
    assertEquals("Condition", contexts.get(0).getSemanticType());
    assertEquals(DataContextType.NAME, contexts.get(0).getType());

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
