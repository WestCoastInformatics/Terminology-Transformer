/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.model;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import com.wci.tt.ReleaseArtifact;
import com.wci.tt.ReleaseInfo;
import com.wci.tt.helpers.CopyConstructorTester;
import com.wci.tt.helpers.EqualsHashcodeTester;
import com.wci.tt.helpers.GetterSetterTester;
import com.wci.tt.helpers.ProxyTester;
import com.wci.tt.jpa.ReleaseArtifactJpa;
import com.wci.tt.jpa.ReleaseInfoJpa;
import com.wci.tt.jpa.helpers.NullableFieldTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link ReleaseArtifactJpa}.
 */
public class ReleaseArtifactJpaUnitTest extends ModelUnitSupport {

  /** The model object to test. */
  private ReleaseArtifactJpa object;

  /** The test fixture r1. */
  private ReleaseInfo r1;

  /** The test fixture r2. */
  private ReleaseInfo r2;

  /**
   * Setup class.
   */
  @BeforeClass
  public static void setupClass() {
    // do nothing
  }

  /**
   * Setup.
   * @throws Exception
   */
  @Before
  public void setup() throws Exception {
    object = new ReleaseArtifactJpa();

    ProxyTester tester = new ProxyTester(new ReleaseInfoJpa());
    r1 = (ReleaseInfoJpa) tester.createObject(1);
    r2 = (ReleaseInfoJpa) tester.createObject(2);
  }

  /**
   * Test getter and setter methods of model object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelGetSet032() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    GetterSetterTester tester = new GetterSetterTester(object);
    tester.test();
  }

  /**
   * Test equals and hascode methods.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelEqualsHashcode032() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("name");
    tester.include("releaseInfo");
    tester.include("data");

    // Set up objects
    tester.proxy(ReleaseInfo.class, 1, r1);
    tester.proxy(ReleaseInfo.class, 2, r2);

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
  public void testModelCopy032() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    CopyConstructorTester tester = new CopyConstructorTester(object);

    // Set up objects
    tester.proxy(ReleaseInfo.class, 1, r1);
    tester.proxy(ReleaseInfo.class, 2, r2);

    assertTrue(tester.testCopyConstructor(ReleaseArtifact.class));
  }

  /**
   * Test not null fields.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelNotNullField032() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    NullableFieldTester tester = new NullableFieldTester(object);
    tester.include("name");
    tester.include("timestamp");
    tester.include("lastModified");
    tester.include("lastModifiedBy");
    tester.include("data");
    assertTrue(tester.testNotNullFields());
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
