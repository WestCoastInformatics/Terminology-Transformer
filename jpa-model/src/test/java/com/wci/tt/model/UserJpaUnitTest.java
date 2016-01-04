/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.model;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import com.wci.tt.User;
import com.wci.tt.UserPreferences;
import com.wci.tt.helpers.CopyConstructorTester;
import com.wci.tt.helpers.EqualsHashcodeTester;
import com.wci.tt.helpers.GetterSetterTester;
import com.wci.tt.helpers.XmlSerializationTester;
import com.wci.tt.jpa.UserJpa;
import com.wci.tt.jpa.UserPreferencesJpa;
import com.wci.tt.jpa.helpers.IndexedFieldTester;
import com.wci.tt.jpa.helpers.NullableFieldTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link UserJpa}.
 */
public class UserJpaUnitTest extends ModelUnitSupport {

  /** The model object to test. */
  private UserJpa object;

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
    object = new UserJpa();

  }

  /**
   * Test getter and setter methods of model object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelGetSet003() throws Exception {
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
  public void testModelEqualsHashcode003() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("applicationRole");
    tester.include("email");
    tester.include("authToken");
    tester.include("name");
    tester.include("userName");

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
  public void testModelCopy003() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    CopyConstructorTester tester = new CopyConstructorTester(object);

    assertTrue(tester.testCopyConstructor(User.class));
  }

  /**
   * Test XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelXmlSerialization003() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    XmlSerializationTester tester = new XmlSerializationTester(object);

    UserPreferences up = new UserPreferencesJpa();
    User user = new UserJpa();
    user.setId(1L);
    user.setUserName("1");
    up.setId(1L);
    up.setUser(user);
    tester.proxy(UserPreferences.class, 1, up);

    assertTrue(tester.testXmlSerialization());
  }

  /**
   * Test not null fields.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelNotNullField003() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    NullableFieldTester tester = new NullableFieldTester(object);
    tester.include("name");
    tester.include("userName");
    tester.include("email");
    tester.include("applicationRole");
    assertTrue(tester.testNotNullFields());
  }

  /**
   * Test field indexing.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelIndexedFields003() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());

    // Test analyzed fields
    IndexedFieldTester tester = new IndexedFieldTester(object);
    tester.include("name");
    tester.include("projectAnyRole");
    tester.include("projectRoleMap");
    assertTrue(tester.testAnalyzedIndexedFields());

    // Test non analyzed fields
    assertTrue(tester.testAnalyzedIndexedFields());
    tester = new IndexedFieldTester(object);
    tester.include("id");
    tester.include("userName");
    tester.include("email");
    tester.include("applicationRole");
    tester.include("nameSort");
    assertTrue(tester.testNotAnalyzedIndexedFields());

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
