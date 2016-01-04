/*
 * Copyright 2015 West Coast Informatics, LLC
 */
/*
 * 
 */
package com.wci.tt.test.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import com.wci.tt.DefinitionClause;
import com.wci.tt.Project;
import com.wci.tt.Refset;
import com.wci.tt.Refset.FeedbackEvent;
import com.wci.tt.UserRole;
import com.wci.tt.ValidationResult;
import com.wci.tt.helpers.ConfigUtility;
import com.wci.tt.helpers.RefsetList;
import com.wci.tt.jpa.DefinitionClauseJpa;
import com.wci.tt.jpa.ProjectJpa;
import com.wci.tt.jpa.RefsetJpa;
import com.wci.tt.jpa.UserJpa;
import com.wci.tt.jpa.services.ProjectServiceJpa;
import com.wci.tt.rest.client.ProjectClientRest;
import com.wci.tt.rest.client.RefsetClientRest;
import com.wci.tt.rest.client.SecurityClientRest;
import com.wci.tt.rest.client.TranslationClientRest;
import com.wci.tt.rest.client.ValidationClientRest;
import com.wci.tt.rest.client.WorkflowClientRest;
import com.wci.tt.services.ProjectService;
import com.wci.tt.workflow.TrackingRecord;
import com.wci.tt.workflow.TrackingRecordList;
import com.wci.tt.workflow.WorkflowStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for refset.
 */
public class WorkflowTest {

  /** The viewer auth token. */
  private static String viewerAuthToken;

  /** The adminAuthToken auth token. */
  protected static String adminAuthToken;

  /** The refset service. */
  protected static RefsetClientRest refsetService;

  /** The translation service. */
  protected static TranslationClientRest translationService;

  /** The security service. */
  protected static SecurityClientRest securityService;

  /** The validation service. */
  protected static ValidationClientRest validationService;

  /** The project service. */
  protected static ProjectClientRest projectService;

  /** The workflow service. */
  protected static WorkflowClientRest workflowService;

  /** The properties. */
  protected static Properties properties;

  /** The test adminAuthToken username. */
  protected static String adminUser;

  /** The test adminAuthToken password. */
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
    translationService = new TranslationClientRest(properties);
    securityService = new SecurityClientRest(properties);
    validationService = new ValidationClientRest(properties);
    projectService = new ProjectClientRest(properties);
    refsetService = new RefsetClientRest(properties);
    workflowService = new WorkflowClientRest(properties);

    // test run.config.ts has adminAuthToken user
    adminUser = properties.getProperty("admin.user");
    adminPassword = properties.getProperty("admin.password");

    if (adminUser == null || adminUser.isEmpty()) {
      throw new Exception(
          "Test prerequisite: adminAuthToken.user must be specified");
    }
    if (adminPassword == null || adminPassword.isEmpty()) {
      throw new Exception(
          "Test prerequisite: adminAuthToken.password must be specified");
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
   * Make basic refset.
   *
   * @param name the name
   * @param definition the definition
   * @param type the type
   * @param project the project
   * @param refsetId the refset id
   * @param importMembers if to import members during refset creation
   * @return the refset jpa
   * @throws Exception the exception
   */
  protected RefsetJpa makeRefset(String name, String definition,
    Refset.Type type, Project project, String refsetId, boolean importMembers)
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
    refset.setPublished(false);
    refset.setTerminology("SNOMEDCT");
    refset.setTerminologyId(refsetId);
    refset.setVersion("2015-01-31");
    refset.setWorkflowPath("DFEAULT");
    refset.setWorkflowStatus(WorkflowStatus.NEW);

    if (type == Refset.Type.INTENSIONAL && definition == null) {
      refset.setDefinitionClauses(new ArrayList<DefinitionClause>());
    } else if (type == Refset.Type.EXTERNAL) {
      refset.setExternalUrl("http://www.example.com/some/other/refset.txt");
    }

    // Validate refset
    ValidationResult result =
        validationService.validateRefset(refset, project.getId(),
            adminAuthToken);
    if (!result.isValid()) {
      Logger.getLogger(getClass()).error(result.toString());
      throw new Exception("Refset does not pass validation.");
    }
    // Add refset
    refsetService = new RefsetClientRest(properties);

    refset = (RefsetJpa) refsetService.addRefset(refset, adminAuthToken);
    refsetService = new RefsetClientRest(properties);

    if (importMembers) {
      refset =
          (RefsetJpa) refsetService.getRefset(refset.getId(), adminAuthToken);

      if (type == Refset.Type.EXTENSIONAL) {
        // EXTENSIONAL Import members (from file)
        ValidationResult vr =
            refsetService.beginImportMembers(refset.getId(), "DEFAULT",
                adminAuthToken);
        if (!vr.isValid()) {
          throw new Exception("import staging is invalid - " + vr);
        }

        refsetService = new RefsetClientRest(properties);
        refset =
            (RefsetJpa) refsetService.getRefset(refset.getId(), adminAuthToken);

        InputStream in =
            new FileInputStream(
                new File(
                    "../config/src/main/resources/data/refset/der2_Refset_SimpleSnapshot_INT_20140731.txt"));
        refsetService.finishImportMembers(null, in, refset.getId(), "DEFAULT",
            adminAuthToken);
        in.close();
      } else if (type == Refset.Type.INTENSIONAL) {
        // Import definition (from file)
        InputStream in =
            new FileInputStream(
                new File(
                    "../config/src/main/resources/data/refset/der2_Refset_DefinitionSnapshot_INT_20140731.txt"));
        refsetService.importDefinition(null, in, refset.getId(), "DEFAULT",
            adminAuthToken);
        in.close();
      }
    }

    return (RefsetJpa) refsetService.getRefset(refset.getId(), adminAuthToken);
  }

  /**
   * Make project.
   *
   * @param name the name
   * @param namespace the namespace
   * @param authToken the auth
   * @return the project jpa
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  private ProjectJpa makeProject(String name, String namespace, String authToken)
    throws Exception {
    ProjectJpa project = new ProjectJpa();
    project.setName(name);
    project.setDescription("Description of project " + name);
    project.setLastModified(new Date());
    project.setTerminology("SNOMEDCT");
    project.setTerminologyId("JIRA-12345");
    project.setVersion("latest");
    // This is the only namespace configured in the sample id generation service
    // when there are others, we can play with this
    project.setNamespace(namespace);
    project.setLastModifiedBy("Author1");
    project.setOrganization("IHTSDO");
    project.addValidationCheck("DEFAULT");

    ProjectService service = new ProjectServiceJpa();

    project = (ProjectJpa) service.addProject(project);
    service.close();

    return project;
  }

  /**
   * Removes the project.
   *
   * @param project the project
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  private void removeProject(Project project) throws Exception {
    ProjectService service = new ProjectServiceJpa();

    service.removeProject(project.getId());
    service.close();
  }

  /**
   * Test adding a member to a refset via an expression
   *
   * @throws Exception the exception
   */
  @Test
  public void testFindAllAvailableAssignedRefsets() throws Exception {
    Logger.getLogger(getClass()).debug("TEST findAllAvailableAssignedRefsets");

    // Test Setup
    UserJpa reviewer1 =
        (UserJpa) securityService.getUser("reviewer1", adminAuthToken);
    UserJpa author1 =
        (UserJpa) securityService.getUser("author1", adminAuthToken);

    ProjectJpa project = makeProject("Project", "1000001", adminAuthToken);
    projectService = new ProjectClientRest(properties);
    projectService.assignUserToProject(project.getId(),
        reviewer1.getUserName(), UserRole.REVIEWER.toString(), adminAuthToken);
    projectService.assignUserToProject(project.getId(), author1.getUserName(),
        UserRole.AUTHOR.toString(), adminAuthToken);

    // Nothing returned to start with
    RefsetList availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    // Nothing returned to start with
    TrackingRecordList assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(0, availableRefsets.getCount());
    assertEquals(0, assignedRefsets.getCount());

    // Create WF Refset1
    RefsetJpa refset1 =
        makeRefset("refset WF Test 1", null, Refset.Type.EXTERNAL, project,
            null, false);
    refset1.setWorkflowStatus(WorkflowStatus.NEW);
    refset1.setWorkflowPath("DEFAULT");
    refsetService = new RefsetClientRest(properties);
    refsetService.updateRefset(refset1, adminAuthToken);

    // Before any WF Action, have availableRefset due to refset1
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(0, assignedRefsets.getCount());

    // AUTHOR-ASSIGN-R1
    workflowService.performWorkflowAction(project.getId(), refset1.getId(),
        author1.getUserName(), "AUTHOR", "ASSIGN", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(0, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, assignedRefsets.getCount());

    // AUTHOR-SAVE-R1
    workflowService.performWorkflowAction(project.getId(), refset1.getId(),
        author1.getUserName(), "AUTHOR", "SAVE", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(0, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, assignedRefsets.getCount());

    // Create WF Refset2, now have 1 availableRefset again
    RefsetJpa refset2 =
        makeRefset("refset WF Test 2", null, Refset.Type.EXTERNAL, project,
            null, false);
    refset2.setWorkflowStatus(WorkflowStatus.NEW);
    refset2.setWorkflowPath("DEFAULT");
    refsetService = new RefsetClientRest(properties);
    refsetService.updateRefset(refset2, adminAuthToken);

    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, assignedRefsets.getCount());

    // AUTHOR-FINISH-R1, as refset2 is available and refset1 is now
    // EDITING_DONE, 2 refsets available
    workflowService.performWorkflowAction(project.getId(), refset1.getId(),
        author1.getUserName(), "AUTHOR", "FINISH", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(2, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, assignedRefsets.getCount());

    // REVIEWER-ASSIGN-R1
    workflowService.performWorkflowAction(project.getId(), refset1.getId(),
        reviewer1.getUserName(), "REVIEWER", "ASSIGN", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, assignedRefsets.getCount());

    // REVIEWER-UNASSIGN-R1
    workflowService.performWorkflowAction(project.getId(), refset1.getId(),
        reviewer1.getUserName(), "REVIEWER", "UNASSIGN", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(2, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, assignedRefsets.getCount());

    // REVIEWER-SAVE-R2
    workflowService.performWorkflowAction(project.getId(), refset2.getId(),
        author1.getUserName(), "AUTHOR", "ASSIGN", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(2, assignedRefsets.getCount());

    // AUTHOR-REASSIGN-R1
    workflowService.performWorkflowAction(project.getId(), refset1.getId(),
        reviewer1.getUserName(), "AUTHOR", "REASSIGN", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(0, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(2, assignedRefsets.getCount());

    // AUTHOR-SAVE-R1
    workflowService.performWorkflowAction(project.getId(), refset1.getId(),
        author1.getUserName(), "AUTHOR", "SAVE", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(0, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(2, assignedRefsets.getCount());

    // Reset WF on both
    workflowService.performWorkflowAction(project.getId(), refset1.getId(),
        author1.getUserName(), "AUTHOR", "UNASSIGN", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(1, assignedRefsets.getCount());

    workflowService.performWorkflowAction(project.getId(), refset2.getId(),
        author1.getUserName(), "AUTHOR", "UNASSIGN", adminAuthToken);
    availableRefsets =
        workflowService.findAllAvailableRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(2, availableRefsets.getCount());
    assignedRefsets =
        workflowService.findAllAssignedRefsets(project.getId(), null,
            adminAuthToken);
    assertEquals(0, assignedRefsets.getCount());

    // Clean Up
    verifyRefsetLookupCompleted(refset1.getId());
    verifyRefsetLookupCompleted(refset2.getId());
    refsetService.removeRefset(refset1.getId(), true, adminAuthToken);
    refsetService.removeRefset(refset2.getId(), true, adminAuthToken);

    projectService.unassignUserFromProject(project.getId(),
        author1.getUserName(), adminAuthToken);
    projectService.unassignUserFromProject(project.getId(),
        reviewer1.getUserName(), adminAuthToken);
    removeProject(project);
  }

  /**
   * Test adding a member to a refset via an expression
   *
   * @throws Exception the exception
   */
  @Test
  public void testGetTrackingRecordForRefset() throws Exception {
    Logger.getLogger(getClass()).debug("TEST getTrackingRecordForRefset");

    // Test Setup
    UserJpa author1 =
        (UserJpa) securityService.getUser("author1", adminAuthToken);

    ProjectJpa project = makeProject("Project", "1000001", adminAuthToken);
    projectService = new ProjectClientRest(properties);
    projectService.assignUserToProject(project.getId(), author1.getUserName(),
        UserRole.AUTHOR.toString(), adminAuthToken);

    // Create WF Refset1
    RefsetJpa refset1 =
        makeRefset("refset WF Test 1", null, Refset.Type.EXTERNAL, project,
            null, false);
    refset1.setWorkflowStatus(WorkflowStatus.NEW);
    refset1.setWorkflowPath("DEFAULT");
    refsetService = new RefsetClientRest(properties);
    refsetService.updateRefset(refset1, adminAuthToken);

    // Create WF Refset2
    RefsetJpa refset2 =
        makeRefset("refset WF Test 2", null, Refset.Type.EXTERNAL, project,
            null, false);
    refset2.setWorkflowStatus(WorkflowStatus.NEW);
    refset2.setWorkflowPath("DEFAULT");
    refsetService = new RefsetClientRest(properties);
    refsetService.updateRefset(refset2, adminAuthToken);

    // AUTHOR-ASSIGN-R1
    TrackingRecord outdatedRecord =
        workflowService.performWorkflowAction(project.getId(), refset1.getId(),
            author1.getUserName(), "AUTHOR", "ASSIGN", adminAuthToken);

    // Store this workflow status for testing as will be overwritten in next
    // call to performWorkflowAction
    refset1 =
        (RefsetJpa) refsetService.getRefset(refset1.getId(), adminAuthToken);
    WorkflowStatus outdatedRecordWorkflowStatus = refset1.getWorkflowStatus();

    // AUTHOR-SAVE-R1
    TrackingRecord returnedRecord1 =
        workflowService.performWorkflowAction(project.getId(), refset1.getId(),
            author1.getUserName(), "AUTHOR", "SAVE", adminAuthToken);

    // AUTHOR-ASSIGN-R2
    TrackingRecord returnedRecord2 =
        workflowService.performWorkflowAction(project.getId(), refset2.getId(),
            author1.getUserName(), "AUTHOR", "ASSIGN", adminAuthToken);

    // Get refsets' tracking records
    TrackingRecord testingRecord1 =
        workflowService.getTrackingRecordForRefset(refset1.getId(),
            adminAuthToken);
    TrackingRecord testingRecord2 =
        workflowService.getTrackingRecordForRefset(refset2.getId(),
            adminAuthToken);

    // Test
    assertEquals(outdatedRecord.getId(), testingRecord1.getId());
    refset1 =
        (RefsetJpa) refsetService.getRefset(refset1.getId(), adminAuthToken);
    refset2 =
        (RefsetJpa) refsetService.getRefset(refset2.getId(), adminAuthToken);
    assertNotEquals(outdatedRecordWorkflowStatus, refset1.getWorkflowStatus());
    assertNotEquals(outdatedRecord.getLastModified(),
        testingRecord1.getLastModified());

    assertNotEquals(refset1.getWorkflowStatus(), refset2.getWorkflowStatus());
    assertNotEquals(testingRecord1.getId(), testingRecord2.getId());
    assertEquals(returnedRecord1.getId(), testingRecord1.getId());
    assertEquals(returnedRecord2.getId(), testingRecord2.getId());

    // Clean Up
    workflowService.performWorkflowAction(project.getId(), refset1.getId(),
        author1.getUserName(), "AUTHOR", "UNASSIGN", adminAuthToken);

    workflowService.performWorkflowAction(project.getId(), refset2.getId(),
        author1.getUserName(), "AUTHOR", "UNASSIGN", adminAuthToken);

    verifyRefsetLookupCompleted(refset1.getId());
    refsetService.removeRefset(refset1.getId(), true, adminAuthToken);
    refsetService.removeRefset(refset2.getId(), true, adminAuthToken);

    projectService.unassignUserFromProject(project.getId(),
        author1.getUserName(), adminAuthToken);
    removeProject(project);
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
