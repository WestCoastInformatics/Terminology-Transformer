/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.model;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import com.wci.tt.ValidationResult;
import com.wci.tt.helpers.CopyConstructorTester;
import com.wci.tt.helpers.EqualsHashcodeTester;
import com.wci.tt.helpers.GetterSetterTester;
import com.wci.tt.helpers.XmlSerializationTester;
import com.wci.tt.jpa.ValidationResultJpa;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link ValidationResultJpa}.
 */
public class ValidationResultJpaUnitTest extends ModelUnitSupport {

  /** The model object to test. */
  private ValidationResultJpa object;

  /** The test fixture s1. */
  private Set<String> s1;

  /** The test fixture s2. */
  private Set<String> s2;

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
    object = new ValidationResultJpa();
    s1 = new HashSet<>();
    s1.add("1");
    s2 = new HashSet<>();
    s2.add("2");
  }

  /**
   * Test getter and setter methods of model object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelGetSet004() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    GetterSetterTester tester = new GetterSetterTester(object);
    tester.exclude("valid");
    tester.test();
  }

  /**
   * Test equals and hascode methods.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelEqualsHashcode004() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("errors");
    tester.include("warnings");
    tester.include("comments");

    // Set up objects
    tester.proxy(Set.class, 1, s1);
    tester.proxy(Set.class, 2, s2);

    assertTrue(tester.testIdentitiyFieldEquals());
    assertTrue(tester.testNonIdentitiyFieldEquals());
    assertTrue(tester.testIdentityFieldNotEquals());
    assertTrue(tester.testIdentitiyFieldHashcode());
    assertTrue(tester.testNonIdentitiyFieldHashcode());
    assertTrue(tester.testIdentityFieldDifferentHashcode());
  }

  /**
   * Test copy constructor.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelCopy004() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    CopyConstructorTester tester = new CopyConstructorTester(object);

    // Set up objects
    tester.proxy(Set.class, 1, s1);
    tester.proxy(Set.class, 2, s2);

    assertTrue(tester.testCopyConstructor(ValidationResult.class));
  }

  /**
   * Test XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelXmlSerialization004() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    XmlSerializationTester tester = new XmlSerializationTester(object);
    // Set up objects
    tester.proxy(Set.class, 1, s1);
    assertTrue(tester.testXmlSerialization());
  }

  /**
   * Test not null fields.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelNotNullField004() throws Exception {
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
