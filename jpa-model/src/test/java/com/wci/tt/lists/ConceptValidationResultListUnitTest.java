/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.lists;

import com.wci.tt.ConceptValidationResult;
import com.wci.tt.helpers.ConceptValidationResultList;
import com.wci.tt.helpers.ProxyTester;
import com.wci.tt.jpa.ConceptValidationResultJpa;
import com.wci.tt.jpa.helpers.ConceptValidationResultListJpa;
import com.wci.tt.rf2.Concept;
import com.wci.tt.rf2.jpa.ConceptJpa;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link ConceptValidationResultList}.
 */
public class ConceptValidationResultListUnitTest extends AbstractListUnit<ConceptValidationResult> {

  /** The list1 test fixture . */
  private ConceptValidationResultList list1;

  /** The list2 test fixture . */
  private ConceptValidationResultList list2;

  /** The test fixture o1. */
  private ConceptValidationResult o1;

  /** The test fixture o2. */
  private ConceptValidationResult o2;

  /**
   * Setup class.
   */
  @BeforeClass
  public static void setupClass() {
    // do nothing
  }

  /**
   * Setup.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    list1 = new ConceptValidationResultListJpa();
    list2 = new ConceptValidationResultListJpa();

    ProxyTester tester = new ProxyTester(new ConceptValidationResultJpa());
    o1 = (ConceptValidationResult) tester.createObject(1);
    o2 = (ConceptValidationResult) tester.createObject(2);
    Concept c1 = new ConceptJpa();
    c1.setId(1L);
    c1.setTerminologyId("1");
    Concept c2 = new ConceptJpa();
    c2.setId(2L);
    c2.setTerminologyId("2");
    o1.setConcept(c1);
    o2.setConcept(c2);
  }

  /**
   * Test normal use of a list.
   * @throws Exception the exception
   */
  @Test
  public void testNormalUse018() throws Exception {
    testNormalUse(list1, list2, o1, o2);
  }

  /**
   * Test degenerate use of a list. Show that the underlying data structure
   * should NOT be manipulated.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testDegenerateUse018() throws Exception {
    testDegenerateUse(list1, list2, o1, o2);
  }

  /**
   * Test edge cases of a list.
   * 
   * @throws Exception the exception
   */
  @Test
  public void testEdgeCases018() throws Exception {
    testEdgeCases(list1, list2, o1, o2);
  }

  /**
   * Test XML serialization of a list.
   *
   * 
   * @throws Exception the exception
   */
  @Test
  public void testXmlSerialization018() throws Exception {
    testXmllSerialization(list1, list2, o1, o2);
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
