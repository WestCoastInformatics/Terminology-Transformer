/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.infomodels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

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
import com.wci.umls.server.helpers.ProxyTester;
import com.wci.umls.server.helpers.XmlSerializationTester;

/**
 * Unit testing for {@link RxcuiModel}.
 */
public class RxcuiModelUnitTest extends JpaSupport {

  /** The model object to test. */
  private RxcuiModel object;

  /** the test fixture l1. */
  private List<RxcuiNdcHistoryModel> l1;

  /** the test fixture l2. */
  private List<RxcuiNdcHistoryModel> l2;

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
    object = new RxcuiModel();
    ProxyTester tester = new ProxyTester(new RxcuiNdcHistoryModel());
    l1 = new ArrayList<>();
    l1.add((RxcuiNdcHistoryModel) tester.createObject(1));
    l2 = new ArrayList<>();
    l2.add((RxcuiNdcHistoryModel) tester.createObject(2));
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
    tester.include("active");
    tester.include("history");

    tester.proxy(List.class, 1, l1);
    tester.proxy(List.class, 2, l2);
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
    tester.proxy(List.class, 1, l1);
    tester.proxy(List.class, 2, l2);
    assertTrue(tester.testCopyConstructor(RxcuiModel.class));
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
    tester.proxy(List.class, 1, l1);
    tester.testXmlSerialization();
  }

  /**
   * Test model in common.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelInCommon() throws Exception {

    RxcuiModel m1 = new RxcuiModel();
    RxcuiModel m2 = new RxcuiModel();

    // should work, but be empty
    RxcuiModel common = m1.getModelInCommon(m2, false);
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

    // History
    m1.getHistory().add(new RxcuiNdcHistoryModel("abc", "def", "ghi"));
    common = m1.getModelInCommon(m2, false);
    assertEquals(0, common.getHistory().size());
    m2.getHistory().add(new RxcuiNdcHistoryModel("def", "ghi", "jkl"));
    common = m1.getModelInCommon(m2, false);
    assertEquals(0, common.getHistory().size());
    m2.getHistory().add(new RxcuiNdcHistoryModel("abc", "def", "ghi"));
    common = m1.getModelInCommon(m2, false);
    assertEquals(1, common.getHistory().size());
    m1.getHistory().add(new RxcuiNdcHistoryModel("def", "ghi", "jkl"));
    common = m1.getModelInCommon(m2, false);
    assertEquals(2, common.getHistory().size());
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
