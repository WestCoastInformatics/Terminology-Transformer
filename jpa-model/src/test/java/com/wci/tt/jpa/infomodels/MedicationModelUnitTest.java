/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.infomodels;

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
import com.wci.tt.jpa.helpers.ValueRawModel;
import com.wci.umls.server.helpers.CopyConstructorTester;
import com.wci.umls.server.helpers.EqualsHashcodeTester;
import com.wci.umls.server.helpers.GetterSetterTester;
import com.wci.umls.server.helpers.ProxyTester;
import com.wci.umls.server.helpers.XmlSerializationTester;

/**
 * Unit testing for {@link MedicationModel}.
 */
public class MedicationModelUnitTest extends JpaSupport {

  /** The model object to test. */
  private MedicationModel object;

  /** the test fixture s1. */
  private List<IngredientModel> s1;

  /** the test fixture s2. */
  private List<IngredientModel> s2;

  private ValueRawModel v1;

  private ValueRawModel v2;

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
    object = new MedicationModel();
    ProxyTester tester = new ProxyTester(new IngredientModel());

    s1 = new ArrayList<>();
    s1.add((IngredientModel) tester.createObject(1));
    s1.add((IngredientModel) tester.createObject(3));
    s2 = new ArrayList<>();
    s2.add((IngredientModel) tester.createObject(2));
    
    
    ProxyTester tester2 = new ProxyTester(new ValueRawModel());

    v1 = (ValueRawModel) tester2.createObject(3);
    v2 = (ValueRawModel) tester2.createObject(4);

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
    tester.include("ingredients");
    tester.include("brandName");
    tester.include("doseForm");
    tester.include("doseFormQualifier");
    tester.include("route");
    tester.include("releasePeriod");

    tester.proxy(List.class, 1, s1);
    tester.proxy(List.class, 2, s2);
    tester.proxy(ValueRawModel.class, 1, v1);
    tester.proxy(ValueRawModel.class, 2, v2);
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
    tester.proxy(List.class, 1, s1);
    tester.proxy(List.class, 2, s2);
    tester.proxy(ValueRawModel.class, 1, v1);
    tester.proxy(ValueRawModel.class, 2, v2);
    assertTrue(tester.testCopyConstructor(MedicationModel.class));
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
    tester.proxy(List.class, 1, s1);
    tester.proxy(List.class, 2, s2);
    tester.proxy(ValueRawModel.class, 1, v1);
    tester.proxy(ValueRawModel.class, 2, v1);
    tester.testXmlSerialization();
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
