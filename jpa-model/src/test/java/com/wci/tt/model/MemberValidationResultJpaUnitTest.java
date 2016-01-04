/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.model;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import com.wci.tt.MemberValidationResult;
import com.wci.tt.Refset;
import com.wci.tt.helpers.CopyConstructorTester;
import com.wci.tt.helpers.EqualsHashcodeTester;
import com.wci.tt.helpers.GetterSetterTester;
import com.wci.tt.helpers.ProxyTester;
import com.wci.tt.helpers.XmlSerializationTester;
import com.wci.tt.jpa.MemberValidationResultJpa;
import com.wci.tt.jpa.RefsetJpa;
import com.wci.tt.rf2.ConceptRefsetMember;
import com.wci.tt.rf2.jpa.ConceptRefsetMemberJpa;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit testing for {@link MemberValidationResultJpa}.
 */
public class MemberValidationResultJpaUnitTest extends ModelUnitSupport {

  /** The model object to test. */
  private MemberValidationResultJpa object;

  /** the test fixture c1 */
  private ConceptRefsetMember c1;

  /** the test fixture c2 */
  private ConceptRefsetMember c2;

  /** the test fixture l1 */
  private Set<String> s1;

  /** the test fixture l2 */
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
   * @throws Exception
   */
  @Before
  public void setup() throws Exception {
    object = new MemberValidationResultJpa();

    ProxyTester tester = new ProxyTester(new ConceptRefsetMemberJpa());
    c1 = (ConceptRefsetMemberJpa) tester.createObject(1);
    c2 = (ConceptRefsetMemberJpa) tester.createObject(2);
    Refset r1 = new RefsetJpa();
    r1.setId(1L);
    Refset r2 = new RefsetJpa();
    r1.setId(2L);
    c1.setRefset(r1);
    c2.setRefset(r2);
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
  public void testModelGetSet043() throws Exception {
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
  public void testModelEqualsHashcode043() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    EqualsHashcodeTester tester = new EqualsHashcodeTester(object);
    tester.include("member");
    tester.include("errors");
    tester.include("warnings");
    tester.include("comments");

    tester.proxy(ConceptRefsetMember.class, 1, c1);
    tester.proxy(ConceptRefsetMember.class, 2, c2);
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
  public void testModelCopy043() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    CopyConstructorTester tester = new CopyConstructorTester(object);
    tester.proxy(ConceptRefsetMember.class, 1, c1);
    tester.proxy(ConceptRefsetMember.class, 2, c2);
    tester.proxy(Set.class, 1, s1);
    tester.proxy(Set.class, 2, s2);

    assertTrue(tester
        .testCopyConstructor(MemberValidationResult.class));
  }

  /**
   * Test XML serialization.
   *
   * @throws Exception the exception
   */
  @Test
  public void testModelXmlSerialization043() throws Exception {
    Logger.getLogger(getClass()).debug("TEST " + name.getMethodName());
    XmlSerializationTester tester = new XmlSerializationTester(object);

    ConceptRefsetMember member = new ConceptRefsetMemberJpa();    
    member.setId(1L);
    Refset refset = new RefsetJpa();
    refset.setId(1L);
    member.setRefset(refset);

    tester.proxy(ConceptRefsetMember.class, 1, member);
    assertTrue(tester.testXmlSerialization());
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
