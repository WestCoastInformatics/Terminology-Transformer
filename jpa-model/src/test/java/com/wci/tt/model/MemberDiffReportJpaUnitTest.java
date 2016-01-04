/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.model;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import com.wci.tt.MemberDiffReport;
import com.wci.tt.Refset;
import com.wci.tt.helpers.ConfigUtility;
import com.wci.tt.helpers.CopyConstructorTester;
import com.wci.tt.helpers.EqualsHashcodeTester;
import com.wci.tt.helpers.GetterSetterTester;
import com.wci.tt.helpers.ProxyTester;
import com.wci.tt.helpers.XmlSerializationTester;
import com.wci.tt.jpa.MemberDiffReportJpa;
import com.wci.tt.jpa.ProjectJpa;
import com.wci.tt.jpa.RefsetJpa;
import com.wci.tt.rf2.ConceptRefsetMember;
import com.wci.tt.rf2.jpa.ConceptRefsetMemberJpa;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link MemberDiffReportJpa}.
 */
public class MemberDiffReportJpaUnitTest extends ModelUnitSupport {

  /** The model object to test. */
  private MemberDiffReportJpa object;

  /** the test fixture r1 */
  private Refset r1;

  /** the test fixture r2 */
  private Refset r2;

  /** the test fixture l1 */
  private List<ConceptRefsetMember> l1;

  /** the test fixture l2 */
  private List<ConceptRefsetMember> l2;

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
    object = new MemberDiffReportJpa();

    ProxyTester tester = new ProxyTester(new RefsetJpa());
    r1 = (RefsetJpa) tester.createObject(1);
    r1.setProject(new ProjectJpa());
    r2 = (RefsetJpa) tester.createObject(2);
    r2.setProject(new ProjectJpa());
    tester = new ProxyTester(new ConceptRefsetMemberJpa());
    
    ConceptRefsetMember cr1 = (ConceptRefsetMember) tester.createObject(1);
    cr1.setRefset(r1);
    ConceptRefsetMember cr2 = (ConceptRefsetMember) tester.createObject(2);
    cr2.setRefset(r2);
    l1 = new ArrayList<>();
    l2 = new ArrayList<>();
    l1.add(cr1);
    l2.add(cr2);
  }

  /**
   * Test getter and setter methods of model object.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelGetSet044() throws Exception {
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
  public void testModelEqualsHashcode044() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("oldRefset");
    tester.include("newRefset");
    tester.include("oldNotNew");
    tester.include("newNotOld");

    tester.proxy(Refset.class, 1, r1);
    tester.proxy(Refset.class, 2, r2);
    tester.proxy(List.class, 1, l1);
    tester.proxy(List.class, 2, l2);

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
  public void testModelCopy044() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    CopyConstructorTester tester = new CopyConstructorTester(object);
    tester.proxy(Refset.class, 1, r1);
    tester.proxy(Refset.class, 2, r2);
    tester.proxy(List.class, 1, l1);
    tester.proxy(List.class, 2, l2);

    assertTrue(tester.testCopyConstructor(MemberDiffReport.class));
  }

  /**
   * Test XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelXmlSerialization044() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    XmlSerializationTester tester = new XmlSerializationTester(object);

    MemberDiffReport report = new MemberDiffReportJpa();
    Refset r1 = new RefsetJpa();
    r1.setId(1L);
    report.setOldRefset(r1);
    report.setNewRefset(r1);
    report.setOldNotNew(l1);
    report.setNewNotOld(l1);
    tester.proxy(MemberDiffReport.class, 1, report);
    tester.proxy(Refset.class, 1, r1);
    assertTrue(tester.testXmlSerialization());
  }

  /**
   * Test concept reference in XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelXmlTransient044() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    MemberDiffReport report = new MemberDiffReportJpa();

    Refset r1 = new RefsetJpa();
    r1.setId(1L);
    report.setOldRefset(r1);
    report.setNewRefset(r1);
    report.setOldNotNew(l1);
    report.setNewNotOld(l1);

    String xml = ConfigUtility.getStringForGraph(report);
    assertTrue(xml.contains("<oldRefsetId>"));
    assertTrue(xml.contains("<newRefsetId>"));
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
