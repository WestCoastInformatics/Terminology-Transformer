/*
 * Copyright 2015 West Coast Informatics, LLC
 */
/*
 * 
 */
package com.wci.tt.test.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.DefinitionClause;
import com.wci.tt.Project;
import com.wci.tt.Refset;
import com.wci.tt.Refset.FeedbackEvent;
import com.wci.tt.ReleaseArtifact;
import com.wci.tt.ReleaseInfo;
import com.wci.tt.User;
import com.wci.tt.ValidationResult;
import com.wci.tt.helpers.ConfigUtility;
import com.wci.tt.helpers.ReleaseInfoList;
import com.wci.tt.jpa.DefinitionClauseJpa;
import com.wci.tt.jpa.RefsetJpa;
import com.wci.tt.jpa.ReleaseArtifactJpa;
import com.wci.tt.jpa.ReleaseInfoJpa;
import com.wci.tt.jpa.services.ReleaseServiceJpa;
import com.wci.tt.rest.client.ProjectClientRest;
import com.wci.tt.rest.client.RefsetClientRest;
import com.wci.tt.rest.client.ReleaseClientRest;
import com.wci.tt.rest.client.SecurityClientRest;
import com.wci.tt.rest.client.ValidationClientRest;
import com.wci.tt.rf2.jpa.ConceptRefsetMemberJpa;
import com.wci.tt.services.ReleaseService;
import com.wci.tt.workflow.WorkflowStatus;

/**
 * Test case for redefinition.
 */
public class RefsetReleaseTest {

  /** The viewer auth token. */
  private static String viewerAuthToken;

  /** The admin auth token. */
  private static String adminAuthToken;

  /** The service. */
  protected static RefsetClientRest refsetService;

  /** The release service. */
  protected static ReleaseClientRest releaseService;

  /** The security service. */
  protected static SecurityClientRest securityService;

  /** The validation service. */
  protected static ValidationClientRest validationService;

  /** The project service. */
  protected static ProjectClientRest projectService;

  /** The properties. */
  protected static Properties properties;

  /** The test username. */
  protected static String testUser;

  /** The test password. */
  protected static String testPassword;

  /** The test admin username. */
  protected static String adminUser;

  /** The test admin password. */
  protected static String adminPassword;

  /** The assign names. */
  private static Boolean assignNames;

  /** The assign names. */
  private static Boolean backgroundLookup;

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {

    // instantiate properties
    properties = ConfigUtility.getConfigProperties();

    // instantiate required services
    refsetService = new RefsetClientRest(properties);
    releaseService = new ReleaseClientRest(properties);
    securityService = new SecurityClientRest(properties);
    validationService = new ValidationClientRest(properties);
    projectService = new ProjectClientRest(properties);

    // test run.config.ts has viewer user
    testUser = properties.getProperty("viewer.user");
    testPassword = properties.getProperty("viewer.password");

    // test run.config.ts has admin user
    adminUser = properties.getProperty("admin.user");
    adminPassword = properties.getProperty("admin.password");

    if (testUser == null || testUser.isEmpty()) {
      throw new Exception("Test prerequisite: viewer.user must be specified");
    }
    if (testPassword == null || testPassword.isEmpty()) {
      throw new Exception(
          "Test prerequisite: viewer.password must be specified");
    }
    if (adminUser == null || adminUser.isEmpty()) {
      throw new Exception("Test prerequisite: admin.user must be specified");
    }
    if (adminPassword == null || adminPassword.isEmpty()) {
      throw new Exception("Test prerequisite: admin.password must be specified");
    }

    // The assign names property
    assignNames =
        Boolean.valueOf(properties
            .getProperty("terminology.handler.DEFAULT.assignNames"));

    backgroundLookup = ConfigUtility.isBackgroundLookup();
  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Before
  public void setup() throws Exception {

    // authentication
    adminAuthToken =
        securityService.authenticate(adminUser, adminPassword).getAuthToken();

  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @After
  public void teardown() throws Exception {

    // logout
    securityService.logout(viewerAuthToken);
    securityService.logout(adminAuthToken);
  }

  /**
   * Make refset.
   *
   * @param name the name
   * @param definition the definition
   * @param type the type
   * @param project the project
   * @param refsetId the refset id
   * @param auth the auth
   * @return the refset jpa
   * @throws Exception the exception
   */
  private RefsetJpa makeRefset(String name, String definition,
    Refset.Type type, Project project, String refsetId, User auth)
    throws Exception {
    RefsetJpa refset = new RefsetJpa();
    refset.setActive(true);
    refset.setType(type);
    refset.setName(name);
    refset.setDescription("Description of refset " + name);
    if (type == Refset.Type.INTENSIONAL) {
      List<DefinitionClause> definitionClauses =
          new ArrayList<DefinitionClause>();
      DefinitionClause clause = new DefinitionClauseJpa();
      clause.setValue(definition);
      clause.setNegated(false);
      definitionClauses.add(clause);
      refset.setDefinitionClauses(definitionClauses);
    } else {
      refset.setDefinitionClauses(null);
    }
    refset.setExternalUrl(null);
    refset.setFeedbackEmail("bcarlsen@westcoastinformatics.com");
    refset.getEnabledFeedbackEvents().add(FeedbackEvent.MEMBER_ADD);
    refset.getEnabledFeedbackEvents().add(FeedbackEvent.MEMBER_REMOVE);
    refset.setForTranslation(false);
    refset.setLastModified(new Date());
    refset.setLookupInProgress(false);
    refset.setModuleId("900000000000445007");
    refset.setProject(project);
    refset.setPublishable(true);
    refset.setPublished(true);
    refset.setTerminology("SNOMEDCT");
    refset.setTerminologyId(refsetId);
    // This is an opportunity to use "branch"
    refset.setVersion("2015-01-31");
    refset.setWorkflowPath("DFEAULT");
    refset.setWorkflowStatus(WorkflowStatus.READY_FOR_PUBLICATION);

    if (type == Refset.Type.EXTERNAL) {
      refset.setExternalUrl("http://www.example.com/some/other/refset.txt");
    }

    // Validate refset
    ValidationResult result =
        validationService.validateRefset(refset, project.getId(),
            auth.getAuthToken());
    if (!result.isValid()) {
      Logger.getLogger(getClass()).error(result.toString());
      throw new Exception("Refset does not pass validation.");
    }
    // Add refset

    refset = (RefsetJpa) refsetService.addRefset(refset, auth.getAuthToken());

    if (type == Refset.Type.EXTENSIONAL) {
      // Import members (from file)
      ValidationResult vr =
          refsetService.beginImportMembers(refset.getId(), "DEFAULT",
              auth.getAuthToken());
      if (!vr.isValid()) {
        throw new Exception("import staging is invalid - " + vr);
      }
      InputStream in =
          new FileInputStream(
              new File(
                  "../config/src/main/resources/data/refset/der2_Refset_SimpleSnapshot_INT_20140731.txt"));
      refsetService.finishImportMembers(null, in, refset.getId(), "DEFAULT",
          auth.getAuthToken());
      in.close();
    } else if (type == Refset.Type.INTENSIONAL) {
      // Import definition (from file)
      InputStream in =
          new FileInputStream(
              new File(
                  "../config/src/main/resources/data/refset/der2_Refset_DefinitionSnapshot_INT_20140731.txt"));
      refsetService.importDefinition(null, in, refset.getId(), "DEFAULT",
          auth.getAuthToken());
      in.close();
    }

    return refset;
  }

  /**
   * Test refset release including begin and cancel.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRelease001() throws Exception {
    Logger.getLogger(getClass()).debug("RUN testMigration001");

    Project project2 = projectService.getProject(2L, adminAuthToken);
    User admin = securityService.authenticate(adminUser, adminPassword);
    // Create refset (intensional) and import definition
    RefsetJpa refset1 =
        makeRefset("refset1", null, Refset.Type.EXTENSIONAL, project2, UUID
            .randomUUID().toString(), admin);
    // Begin release
    releaseService.beginRefsetRelease(refset1.getId(),
        ConfigUtility.DATE_FORMAT.format(Calendar.getInstance()),
        adminAuthToken);
    // Cancel release
    releaseService.cancelRefsetRelease(refset1.getId(), adminAuthToken);
    // clean up
    verifyRefsetLookupCompleted(refset1.getId());
    refsetService.removeRefset(refset1.getId(), true, adminAuthToken);
  }

  /**
   * Test refset release including begin, validate and cancel.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRelease002() throws Exception {
    Logger.getLogger(getClass()).debug("RUN testMigration001");

    Project project2 = projectService.getProject(2L, adminAuthToken);
    User admin = securityService.authenticate(adminUser, adminPassword);
    // Create refset (intensional) and import definition
    RefsetJpa refset1 =
        makeRefset("refset1", null, Refset.Type.EXTENSIONAL, project2, UUID
            .randomUUID().toString(), admin);
    // Begin release
    releaseService.beginRefsetRelease(refset1.getId(),
        ConfigUtility.DATE_FORMAT.format(Calendar.getInstance()),
        adminAuthToken);
    // Validate release
    releaseService.validateRefsetRelease(refset1.getId(), adminAuthToken);
    // Cancel release
    releaseService.cancelRefsetRelease(refset1.getId(), adminAuthToken);
    // clean up
    verifyRefsetLookupCompleted(refset1.getId());
    refsetService.removeRefset(refset1.getId(), true, adminAuthToken);
  }

  /**
   * Test refset release including begin, validate, beta and cancel.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRelease003() throws Exception {
    Logger.getLogger(getClass()).debug("RUN testMigration001");

    Project project2 = projectService.getProject(2L, adminAuthToken);
    User admin = securityService.authenticate(adminUser, adminPassword);
    // Create refset (intensional) and import definition
    RefsetJpa refset1 =
        makeRefset("refset1", null, Refset.Type.EXTENSIONAL, project2, UUID
            .randomUUID().toString(), admin);
    // Begin release
    releaseService.beginRefsetRelease(refset1.getId(),
        ConfigUtility.DATE_FORMAT.format(Calendar.getInstance()),
        adminAuthToken);
    // Validate release
    releaseService.validateRefsetRelease(refset1.getId(), adminAuthToken);
    // Beta release
    releaseService.betaRefsetRelease(refset1.getId(), "DEFAULT",
        adminAuthToken);
    // Cancel release
    releaseService.cancelRefsetRelease(refset1.getId(), adminAuthToken);
    // clean up
    verifyRefsetLookupCompleted(refset1.getId());
    refsetService.removeRefset(refset1.getId(), true, adminAuthToken);
  }

  /**
   * Test refset release including begin, validate, beta and finish.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRelease004() throws Exception {
    Logger.getLogger(getClass()).debug("RUN testMigration001");

    Project project2 = projectService.getProject(2L, adminAuthToken);
    User admin = securityService.authenticate(adminUser, adminPassword);
    // Create refset (intensional) and import definition
    RefsetJpa refset1 =
        makeRefset("refset1", null, Refset.Type.EXTENSIONAL, project2, UUID
            .randomUUID().toString(), admin);
    // Begin release
    releaseService.beginRefsetRelease(refset1.getId(),
        ConfigUtility.DATE_FORMAT.format(Calendar.getInstance()),
        adminAuthToken);
    // Validate release
    releaseService.validateRefsetRelease(refset1.getId(), adminAuthToken);
    // Beta release
    releaseService.betaRefsetRelease(refset1.getId(), "DEFAULT",
        adminAuthToken);
    // Finish release
    releaseService.finishRefsetRelease(refset1.getId(), adminAuthToken);
    // clean up
    verifyRefsetLookupCompleted(refset1.getId());
    refsetService.removeRefset(refset1.getId(), true, adminAuthToken);
  }

  /**
   * Test refset release including begin, validate, beta, finish and delta.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRelease005() throws Exception {
    Logger.getLogger(getClass()).debug("RUN testMigration001");

    Project project2 = projectService.getProject(2L, adminAuthToken);
    User admin = securityService.authenticate(adminUser, adminPassword);
    // Create refset (intensional) and import definition
    RefsetJpa refset1 =
        makeRefset("refset1", null, Refset.Type.EXTENSIONAL, project2, UUID
            .randomUUID().toString(), admin);
    // Begin release
    releaseService.beginRefsetRelease(refset1.getId(),
        ConfigUtility.DATE_FORMAT.format(Calendar.getInstance()),
        adminAuthToken);
    // Validate release
    releaseService.validateRefsetRelease(refset1.getId(), adminAuthToken);
    // Beta release
    releaseService.betaRefsetRelease(refset1.getId(), "DEFAULT",
        adminAuthToken);
    // Finish release
    releaseService.finishRefsetRelease(refset1.getId(), adminAuthToken);
    // Add 5 members to refset
    ConceptRefsetMemberJpa member1 =
        makeConceptRefsetMember("member1", "123", refset1);
    refsetService.addRefsetMember(member1, adminAuthToken);
    ConceptRefsetMemberJpa member2 =
        makeConceptRefsetMember("member2", "12344", refset1);
    refsetService.addRefsetMember(member2, adminAuthToken);
    ConceptRefsetMemberJpa member3 =
        makeConceptRefsetMember("member3", "123333", refset1);
    refsetService.addRefsetMember(member3, adminAuthToken);
    ConceptRefsetMemberJpa member4 =
        makeConceptRefsetMember("member4", "123223", refset1);
    refsetService.addRefsetMember(member4, adminAuthToken);
    ConceptRefsetMemberJpa member5 =
        makeConceptRefsetMember("member5", "1234545", refset1);
    refsetService.addRefsetMember(member5, adminAuthToken);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONDAY, 1);
    // Begin release
    releaseService.beginRefsetRelease(refset1.getId(),
        ConfigUtility.DATE_FORMAT.format(calendar), adminAuthToken);
    // Validate release
    releaseService.validateRefsetRelease(refset1.getId(), adminAuthToken);
    // Beta release
    releaseService.betaRefsetRelease(refset1.getId(), "DEFAULT",
        adminAuthToken);
    // Finish release
    releaseService.finishRefsetRelease(refset1.getId(), adminAuthToken);
    // clean up
    verifyRefsetLookupCompleted(refset1.getId());
    refsetService.removeRefset(refset1.getId(), true, adminAuthToken);
  }

  /**
   * Test removing a release artifact for refset.
   *
   * @throws Exception the exception
   */
  @Test
  public void testRemoveReleaseArtifact() throws Exception {
    Project project = projectService.getProject(2L, adminAuthToken);
    User admin = securityService.authenticate(adminUser, adminPassword);

    // Create refset (intensional) and import definition
    RefsetJpa refset =
        makeRefset("refset3", null, Refset.Type.EXTENSIONAL, project, UUID
            .randomUUID().toString(), admin);

    // take a refset entirely through the release cycle, including release
    // artifacts
    ReleaseInfo refsetReleaseInfo =
        makeReleaseInfo("Refset release info", refset);

    ReleaseArtifact simpleRelArtRefset =
        makeReleaseArtifact(
            "releaseArtifact1.txt",
            refsetReleaseInfo,
            "../config/src/main/resources/data/refset/der2_Refset_SimpleSnapshot_INT_20140731.txt");

    ReleaseArtifact definitionArtRefset =
        makeReleaseArtifact(
            "releaseArtifact2.txt",
            refsetReleaseInfo,
            "../config/src/main/resources/data/refset/der2_Refset_DefinitionSnapshot_INT_20140731.txt");

    // Ensure that both ReleaseArtifacts created
    assertEquals(2, refsetReleaseInfo.getArtifacts().size());

    // Remove a ReleaseArtifact
    releaseService.removeReleaseArtifact(simpleRelArtRefset.getId(),
        adminAuthToken);
    refsetReleaseInfo =
        releaseService.getCurrentRefsetReleaseInfo(refset.getId(),
            adminAuthToken);
    assertEquals(1, refsetReleaseInfo.getArtifacts().size());

    // Ensure proper ReleaseArtifact removed
    assertEquals(definitionArtRefset.getName(), refsetReleaseInfo
        .getArtifacts().get(0).getName());
    assertEquals(definitionArtRefset.getId(), refsetReleaseInfo.getArtifacts()
        .get(0).getId());

    // Remove second ReleaseArtifact
    releaseService.removeReleaseArtifact(definitionArtRefset.getId(),
        adminAuthToken);
    refsetReleaseInfo =
        releaseService.getCurrentRefsetReleaseInfo(refset.getId(),
            adminAuthToken);
    assertEquals(0, refsetReleaseInfo.getArtifacts().size());

    // clean up
    releaseService.removeReleaseInfo(refsetReleaseInfo.getId(), adminAuthToken);
    verifyRefsetLookupCompleted(refset.getId());
    refsetService.removeRefset(refset.getId(), true, adminAuthToken);
  }

  /**
   * Test importing and exporting release artifacts from/to an InputStream.
   *
   * @throws Exception the exception
   */
  @Test
  public void testExportImportReleaseArtifact() throws Exception {
    Project project = projectService.getProject(2L, adminAuthToken);
    User admin = securityService.authenticate(adminUser, adminPassword);

    // Create refset (intensional) and import definition
    RefsetJpa refset =
        makeRefset("refset4", null, Refset.Type.EXTENSIONAL, project, UUID
            .randomUUID().toString(), admin);

    // Create ReleaseInfo & ReleaseArtifact
    ReleaseInfo refsetReleaseInfo =
        makeReleaseInfo("Refset release info", refset);

    ReleaseArtifact originalRelease =
        makeReleaseArtifact(
            "releaseArtifact1.txt",
            refsetReleaseInfo,
            "../config/src/main/resources/data/refset/der2_Refset_SimpleSnapshot_INT_20140731.txt");

    // Ensure that ReleaseArtifact created
    assertEquals(1, refsetReleaseInfo.getArtifacts().size());
    ReleaseInfo processedRefsetReleaseInfo =
        releaseService.getCurrentRefsetReleaseInfo(refset.getId(),
            adminAuthToken);

    // Export ReleaseArtifact
    InputStream artifactStream =
        releaseService.exportReleaseArtifact(originalRelease.getId(),
            adminAuthToken);

    // Import ReleaseArtifact
    //    ReleaseArtifact processedRelease =
        releaseService.importReleaseArtifact(null, artifactStream,
            refsetReleaseInfo.getId(), adminAuthToken);
    releaseService = new ReleaseClientRest(properties);

    // Verify Refset now has two ReleaseArtifact objects
    processedRefsetReleaseInfo =
        releaseService.getCurrentRefsetReleaseInfo(refset.getId(),
            adminAuthToken);
    assertEquals(2, processedRefsetReleaseInfo.getArtifacts().size());

    // TODO: getData md5 method to do equals() on getData() for originalRelease
    // versus processedRelease
    // Until then, below section commented out
    /*
     * ReleaseArtifact newRelease = null; ReleaseArtifact oldRelease = null; for
     * (ReleaseArtifact art : processedRefsetReleaseInfo.getArtifacts()) { if
     * (art.getId().equals(processedRelease.getId())) { newRelease = art; } else
     * { oldRelease = art; } }
     * 
     * assertEquals(originalRelease.getData(), processedRelease.getData());
     * assertEquals(oldRelease.getData(), newRelease.getData());
     */

    // clean up
    releaseService.removeReleaseInfo(refsetReleaseInfo.getId(), adminAuthToken);
    verifyRefsetLookupCompleted(refset.getId());
    refsetService.removeRefset(refset.getId(), true, adminAuthToken);
  }

  /**
   * Make release info.
   *
   * @param name the name
   * @param object the object
   * @return the release info
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  private ReleaseInfo makeReleaseInfo(String name, Object object)
    throws Exception {
    ReleaseInfoJpa info = new ReleaseInfoJpa();
    info.setName(name);
    info.setDescription("Description of release info " + name);
    info.setRefset((Refset) object);
    info.setLastModified(new Date());
    info.setLastModifiedBy("loader");
    info.setEffectiveTime(new Date());
    info.setPublished(true);
    info.setReleaseBeginDate(new Date());
    info.setReleaseFinishDate(new Date());
    info.setTerminology("SNOMEDCT");
    info.setVersion("latest");
    info.setPlanned(false);
    // Need to use Jpa because rest service doesn't have "add release info"
    ReleaseService service = new ReleaseServiceJpa();

    info = (ReleaseInfoJpa) service.addReleaseInfo(info);
    service.close();

    return info;
  }

  /**
   * Make release artifact.
   *
   * @param name the name
   * @param releaseInfo the release info
   * @param pathToFile the path to file
   * @return the release artifact
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  private ReleaseArtifact makeReleaseArtifact(String name,
    ReleaseInfo releaseInfo, String pathToFile) throws Exception {
    ReleaseArtifact artifact = new ReleaseArtifactJpa();
    artifact.setName(name);
    artifact.setLastModified(new Date());
    artifact.setLastModifiedBy("loader");
    artifact.setReleaseInfo(releaseInfo);
    artifact.setTimestamp(new Date());

    Path path = Paths.get(pathToFile);
    byte[] data = Files.readAllBytes(path);
    artifact.setData(data);

    releaseInfo.getArtifacts().add(artifact);
    // Need to use Jpa because rest service doesn't have "add release info"
    ReleaseService service = new ReleaseServiceJpa();
    artifact = service.addReleaseArtifact(artifact);
    service.close();
    return artifact;
  }

  /**
   * Make concept refset member.
   *
   * @param name the name
   * @param id the id
   * @param refset the refset
   * @return the concept refset member jpa
   */
  @SuppressWarnings("static-method")
  protected ConceptRefsetMemberJpa makeConceptRefsetMember(String name,
    String id, Refset refset) {
    ConceptRefsetMemberJpa member = new ConceptRefsetMemberJpa();
    member.setActive(true);
    member.setConceptActive(true);
    member.setConceptId(id);
    member.setConceptName(name);
    member.setEffectiveTime(new Date());
    member.setMemberType(Refset.MemberType.MEMBER);
    member.setTerminology("N/A");
    member.setVersion("N/A");
    member.setModuleId(refset.getModuleId());
    member.setRefset(refset);
    return member;
  }

  /**
   * Test finding refset releases via a query.
   * 
   * @throws Exception the exception
   */
   @Test
  public void testFindRefsetReleasesForQuery() throws Exception {
    Project project = projectService.getProject(2L, adminAuthToken);
    User admin = securityService.authenticate(adminUser, adminPassword);

    // Create refset (intensional) and import definition
    RefsetJpa refset =
        makeRefset("refset", null, Refset.Type.EXTENSIONAL, project, UUID
            .randomUUID().toString(), admin);

    // Begin release
    releaseService.beginRefsetRelease(refset.getId(),
        ConfigUtility.DATE_FORMAT.format(Calendar.getInstance()),
        adminAuthToken);
    releaseService.validateRefsetRelease(refset.getId(), adminAuthToken);
    // Beta release
    releaseService.betaRefsetRelease(refset.getId(), "DEFAULT",
        adminAuthToken);

    /*
     * While release still in process
     */

    // find releases per refset
    ReleaseInfoList releases =
        releaseService.findRefsetReleasesForQuery(refset.getId(), null, null,
            adminAuthToken);
    assertEquals(1, releases.getCount());

    // find releases per terminologyId (there are two b/c of
    // beginRefsetRelease() with 2nd planned=false
    releases =
        releaseService.findRefsetReleasesForQuery(null, "refsetTerminologyId:"
            + refset.getTerminologyId(), null, adminAuthToken);
    assertEquals(2, releases.getCount());

    // find releases per projectId. Will have 2 or more depending on how many
    // have been created in DB
    releases =
        releaseService.findRefsetReleasesForQuery(null,
            "projectId:" + project.getId(), null, adminAuthToken);
    assertTrue(releases.getCount() >= 2);

    // find releases per refsetId & projectId
    releases =
        releaseService.findRefsetReleasesForQuery(refset.getId(), "projectId:"
            + project.getId(), null, adminAuthToken);
    assertEquals(1, releases.getCount());

    // find releases per refsetId & terminologyId
    releases =
        releaseService.findRefsetReleasesForQuery(refset.getId(),
            "refsetTerminologyId:" + refset.getTerminologyId(), null,
            adminAuthToken);
    assertEquals(1, releases.getCount());

    // find releases per projectId & terminologyId. Reason is same as reason
    // during terminologyId only test
    releases =
        releaseService.findRefsetReleasesForQuery(null, "refsetTerminologyId:"
            + refset.getTerminologyId() + " AND projectId:" + project.getId(),
            null, adminAuthToken);
    assertEquals(2, releases.getCount());

    // find releases per refsetId & projectId & terminologyId
    releases =
        releaseService.findRefsetReleasesForQuery(refset.getId(),
            "refsetTerminologyId:" + refset.getTerminologyId()
                + " AND projectId:" + project.getId(), null, adminAuthToken);
    assertEquals(1, releases.getCount());

    // Now finish the release
    releaseService.finishRefsetRelease(refset.getId(), adminAuthToken);

    /*
     * Following completed of release
     */

    // find releases per refset
    releases =
        releaseService.findRefsetReleasesForQuery(refset.getId(), null, null,
            adminAuthToken);
    assertEquals(0, releases.getCount());

    // find releases per terminologyId (there are two b/c of
    // beginRefsetRelease() with 2nd planned=false
    releases =
        releaseService.findRefsetReleasesForQuery(null, "refsetTerminologyId:"
            + refset.getTerminologyId(), null, adminAuthToken);
    assertEquals(1, releases.getCount());

    // find releases per projectId. Will have 2 or more depending on how many
    // have been created in DB
    releases =
        releaseService.findRefsetReleasesForQuery(null,
            "projectId:" + project.getId(), null, adminAuthToken);
    assertTrue(releases.getCount() >= 1);

    // find releases per refsetId & projectId
    releases =
        releaseService.findRefsetReleasesForQuery(refset.getId(), "projectId:"
            + project.getId(), null, adminAuthToken);
    assertEquals(0, releases.getCount());

    // find releases per refsetId & terminologyId
    releases =
        releaseService.findRefsetReleasesForQuery(refset.getId(),
            "refsetTerminologyId:" + refset.getTerminologyId(), null,
            adminAuthToken);
    assertEquals(0, releases.getCount());

    // find releases per projectId & terminologyId. Reason is same as reason
    // during terminologyId only test
    releases =
        releaseService.findRefsetReleasesForQuery(null, "refsetTerminologyId:"
            + refset.getTerminologyId() + " AND projectId:" + project.getId(),
            null, adminAuthToken);
    assertEquals(1, releases.getCount());

    // find releases per refsetId & projectId & terminologyId
    releases =
        releaseService.findRefsetReleasesForQuery(refset.getId(),
            "refsetTerminologyId:" + refset.getTerminologyId()
                + " AND projectId:" + project.getId(), null, adminAuthToken);
    assertEquals(0, releases.getCount());

    // clean up
    verifyRefsetLookupCompleted(refset.getId());
    refsetService.removeRefset(refset.getId(), true, adminAuthToken);
  }

  /**
   * Ensure refset completed prior to shutting down test to avoid lookupName
   * issues.
   *
   * @param refsetId the refset id
   * @throws Exception the exception
   */
  protected void verifyRefsetLookupCompleted(Long refsetId) throws Exception {
    if (assignNames && backgroundLookup) {
      // Ensure that all lookupNames routines completed
      boolean completed = false;
      refsetService = new RefsetClientRest(properties);

      while (!completed) {
        // Assume process has completed
        completed = true;

        Refset r = refsetService.getRefset(refsetId, adminAuthToken);
        if (r.isLookupInProgress()) {
          // lookupNames still running on refset
          Logger.getLogger(getClass()).info("Inside wait-loop");
          completed = false;
          Thread.sleep(250);
        }
      }
    }
  }
}
