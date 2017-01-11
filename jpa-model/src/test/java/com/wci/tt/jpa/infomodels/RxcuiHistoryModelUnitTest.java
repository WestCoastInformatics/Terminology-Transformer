/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.infomodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.jpa.JpaSupport;
import com.wci.umls.server.helpers.CopyConstructorTester;
import com.wci.umls.server.helpers.EqualsHashcodeTester;
import com.wci.umls.server.helpers.GetterSetterTester;
import com.wci.umls.server.helpers.XmlSerializationTester;

/**
 * Unit testing for {@link RxcuiNdcHistoryModel}.
 */
public class RxcuiHistoryModelUnitTest extends JpaSupport {

  /** The model object to test. */
  private RxcuiNdcHistoryModel object;

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
    object = new RxcuiNdcHistoryModel();

  }

  /**
   * Test getter and setter methods of model object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelGetSet() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    GetterSetterTester tester = new GetterSetterTester(object);
    tester.exclude("properties");
    tester.test();

  }

  /**
   * Test equals and hashcode methods.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelEqualsHashcode() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("ndc");
    tester.include("start");
    tester.include("end");
    assertTrue(tester.testIdentityFieldEquals());
    assertTrue(tester.testNonIdentityFieldEquals());
    assertTrue(tester.testIdentityFieldNotEquals());
    assertTrue(tester.testIdentityFieldHashcode());
    assertTrue(tester.testNonIdentityFieldHashcode());
    assertTrue(tester.testIdentityFieldDifferentHashcode());
  }

  /**
   * Test copy constructor.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelCopy() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    CopyConstructorTester tester = new CopyConstructorTester(object);

    assertTrue(tester.testCopyConstructor(RxcuiNdcHistoryModel.class));
  }

  /**
   * Test XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelXmlSerialization() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    XmlSerializationTester tester = new XmlSerializationTester(object);
    tester.testXmlSerialization();
  }

  /**
   * Test model in common.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void testModelInCommon() throws Exception {

    RxcuiNdcHistoryModel m1 = new RxcuiNdcHistoryModel();
    RxcuiNdcHistoryModel m2 = new RxcuiNdcHistoryModel();

    // should work, but be empty
    RxcuiNdcHistoryModel common = m1.getModelInCommon(m2, false);
    assertNull(common);

    // ndc
    m1.setNdc("abc");
    common = m1.getModelInCommon(m2, false);
    assertNull(common);
    m2.setNdc("def");
    common = m1.getModelInCommon(m2, false);
    assertNull(common);
    m2.setNdc("abc");
    common = m1.getModelInCommon(m2, false);
    assertEquals("abc", common.getNdc());

    // start
    m1 = new RxcuiNdcHistoryModel();
    m2 = new RxcuiNdcHistoryModel();
    m1.setStart("abc");
    common = m1.getModelInCommon(m2, false);
    assertNull(common);
    m2.setStart("def");
    common = m1.getModelInCommon(m2, false);
    assertNull(common);
    m2.setStart("abc");
    common = m1.getModelInCommon(m2, false);
    assertEquals("abc", common.getStart());

    // end 
    m1 = new RxcuiNdcHistoryModel();
    m2 = new RxcuiNdcHistoryModel();
    m1.setEnd("abc");
    common = m1.getModelInCommon(m2, false);
    assertNull(common);
    m2.setEnd("def");
    common = m1.getModelInCommon(m2, false);
    assertNull(common);
    m2.setEnd("abc");
    common = m1.getModelInCommon(m2, false);
    assertEquals("abc", common.getEnd());
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
