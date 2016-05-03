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
 * Unit testing for {@link NdcHistoryModel}.
 */
public class NdcHistoryModelUnitTest extends JpaSupport {

  /** The model object to test. */
  private NdcHistoryModel object;

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
    object = new NdcHistoryModel();

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
    tester.include("rxcui");
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

    assertTrue(tester.testCopyConstructor(NdcHistoryModel.class));
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
  @Test
  public void testModelInCommon() throws Exception {

    NdcHistoryModel m1 = new NdcHistoryModel();
    NdcHistoryModel m2 = new NdcHistoryModel();

    // should work, but be empty
    NdcHistoryModel common = m1.getModelInCommon(m2, false);
    assertNull(common);

    // rxcui
    m1.setRxcui("abc");
    common = m1.getModelInCommon(m2, false);
    assertNull(common);
    m2.setRxcui("def");
    common = m1.getModelInCommon(m2, false);
    assertNull(common);
    m2.setRxcui("abc");
    common = m1.getModelInCommon(m2, false);
    assertEquals("abc", common.getRxcui());

    // start
    m1 = new NdcHistoryModel();
    m2 = new NdcHistoryModel();
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
    m1 = new NdcHistoryModel();
    m2 = new NdcHistoryModel();
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
