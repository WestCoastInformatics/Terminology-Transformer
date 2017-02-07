
/*
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.tt.jpa.services.algo.TerminologySimpleCsvLoaderAlgorithm;
import com.wci.tt.jpa.services.handlers.DefaultAbbreviationHandler;
import com.wci.tt.services.handlers.AbbreviationHandler;
import com.wci.umls.server.Project;
import com.wci.umls.server.User;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.PrecedenceList;
import com.wci.umls.server.jpa.ProjectJpa;
import com.wci.umls.server.jpa.UserJpa;
import com.wci.umls.server.jpa.services.MetadataServiceJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.services.MetadataService;
import com.wci.umls.server.services.SecurityService;

/**
 * A mechanism to reset to the stock dev database for MLDP work..
 */
public class ResetMldpDatabase {

  /** The properties. */
  static Properties config;

  /** The server. */
  static String server = "false";

  /** The input directory */
  private String inputDirPath = null;

  /**
   * Create test fixtures for class.
   *
   * @throws Exception the exception
   */
  @BeforeClass
  public static void setupClass() throws Exception {
    config = ConfigUtility.getConfigProperties();
    if (ConfigUtility.isServerActive()) {
      server = "true";
    }
  }

  /**
   * Test the sequence:
   * 
   * <pre>
   * 1. Load terminologies (simple)
   * 2. Load config
   * 3. Generate users, projects, and data
   * </pre>
   * 
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  @Test
  public void test() throws Exception {

    // check for existence of non-empty input directory
    inputDirPath = config.getProperty("input.dir");

    if (inputDirPath == null) {
      inputDirPath = "../config/mldp/src/main/resources/data/";
    }
    if (!inputDirPath.endsWith("/")) {
      inputDirPath += "/";
    }
    File inputDir = new File(inputDirPath);
    if (!inputDir.isDirectory()) {
      throw new Exception("Specified input directory is not a directory");
    }
    if (inputDir.list().length == 0) {
      throw new Exception("Specified input directory is empty");
    }

    // List of MLDP terminologies
    final List<String> mldpTerminologies = new ArrayList<>();

    // {
    // "allergy", "anatomy", "condition", "immunization", "lab", "med",
    // "procedure", "vital"
    // };
    //
    // extract the folder names
    for (final File f : inputDir.listFiles()) {
      if (f.isDirectory()) {
        Logger.getLogger(getClass())
            .info("Discovered terminology: " + f.getName());
        mldpTerminologies.add(f.getName());
        for (final String fileName : f.list()) {
          Logger.getLogger(getClass()).info("  File: " + fileName);
        }
      }
    }
    
    Logger.getLogger(getClass()).info("Number of terminologies to load: " + mldpTerminologies.size());

    // output identifier handler
    Logger.getLogger(getClass()).info("DEFAULT id assignment handler: "
        + config.getProperty("identifier.assignment.handler.DEFAULT.class"));

    // re-create the database by triggering a JPA event
    Logger.getLogger(getClass()).info("Re-creating database...");
    config.setProperty("hibernate.hbm2ddl.auto", "create");
    new MetadataServiceJpa().close();
    config.setProperty("hibernate.hbm2ddl.auto", "update");
    Logger.getLogger(getClass()).info("  Done.");

    // clear index
    // TODO Need to specify all indexed field names -- for now assume clean
    // build
    // Logger.getLogger(getClass()).info("Purging lucene indexes...");
    // LuceneReindexAlgorithm luceneAlgo = new LuceneReindexAlgorithm();
    // luceneAlgo.setProperties(config);
    // luceneAlgo.reset();
    // Logger.getLogger(getClass()).info(" Done.");

    final SecurityService securityService = new SecurityServiceJpa();
    final MetadataService service = new MetadataServiceJpa();

    // shared variables
    InputStream inStream;
    AbbreviationHandler abbrHandler;
    Project project;

    Logger.getLogger(getClass()).info("Creating default users...");
    final Map<User, UserRole> userRoleMap = new HashMap<>();

    // add admin, user, and viewer users
    User admin = new UserJpa();
    admin.setApplicationRole(UserRole.ADMINISTRATOR);
    admin.setName("Administrator");
    admin.setUserName("admin");
    admin.setEmail("");
    admin = securityService.addUser(admin);
    User user = new UserJpa();
    user.setApplicationRole(UserRole.USER);
    user.setName("User");
    user.setUserName("user");
    user.setEmail("");
    user = securityService.addUser(user);
    User viewer = new UserJpa();
    viewer.setApplicationRole(UserRole.VIEWER);
    viewer.setName("Guest");
    viewer.setUserName("guest");
    viewer.setEmail("");
    viewer = securityService.addUser(viewer);

    userRoleMap.put(admin, UserRole.ADMINISTRATOR);
    userRoleMap.put(user, UserRole.USER);
    userRoleMap.put(viewer, UserRole.VIEWER);
    Logger.getLogger(getClass()).info("  Done.");

    final String[] validationChecks =
        config.getProperty("validation.service.handler").split(",");

    // for each terminology
    // - Create an editing project
    // - Load csv concepts file
    // - Load txt abbreviations file
    // - Load txt synonyms file
    for (String mldpTerminology : mldpTerminologies) {

      final String terminology = "HKFT-" + mldpTerminology.toUpperCase();
      final String version = "latest";
      final String loaderUser = "loader";

      // load terminology
      Logger.getLogger(getClass())
          .info("Loading terminology " + mldpTerminology);
      Logger.getLogger(getClass()).info("  File: " + inputDirPath
          + mldpTerminology + "/" + mldpTerminology + "Concepts.csv");

      final TerminologySimpleCsvLoaderAlgorithm termAlgo =
          new TerminologySimpleCsvLoaderAlgorithm();
      termAlgo.setTerminology(terminology);
      termAlgo.setVersion(version);
      termAlgo.setAssignIdentifiersFlag(true);
      termAlgo.setInputFile(inputDirPath + mldpTerminology + "/"
          + mldpTerminology + "Concepts.csv");
      termAlgo.setReleaseVersion(version);
      termAlgo.setLastModifiedBy(loaderUser);
      termAlgo.compute();

      // load abbreviations file
      Logger.getLogger(getClass())
          .info("Loading abbreviations for terminology " + mldpTerminology);
      Logger.getLogger(getClass()).info("  File: " + inputDirPath
          + mldpTerminology + "/" + mldpTerminology + "Abbr.txt");

      inStream = new FileInputStream(inputDirPath
          + mldpTerminology + "/" + mldpTerminology + "Abbr.txt");
      abbrHandler = new DefaultAbbreviationHandler();
      abbrHandler.setService(service);
      abbrHandler.setReviewFlag(false);
      abbrHandler.importAbbreviationFile(
          "HKFT-" + mldpTerminology.toUpperCase() + "-ABBR", inStream);

      // load synonyms file
      // TODO Commented out for now, out of scope and time-consuming
      // Logger.getLogger(getClass())
      // .info("Loading synonyms for terminology " + mldpTerminology);
      // Logger.getLogger(getClass())
      // .info(" File: " +inputDirPath
      // + mldpTerminology + "/" + mldpTerminology + "Sy.txt");
      //
      // inStream = new
      // FileInputStream(inputDirPath
      // + mldpTerminology + "/" + mldpTerminology + "Sy.txt");
      // abbrHandler = new DefaultAbbreviationHandler();
      // abbrHandler.setService(projectService);
      // abbrHandler.setReviewFlag(false);
      // abbrHandler.importAbbreviationFile(
      // "MLDP-" + mldpTerminology.toUpperCase() + "-SY", inStream);

      // create a project for the terminology
      Logger.getLogger(getClass())
          .info("Creating project for terminology " + mldpTerminology);

      project = new ProjectJpa();
      project.setAutomationsEnabled(true);
      project.setName("MLDP Project - " + terminology);
      project.setLanguage("en");
      project.setDescription(
          "Simple editing project for MLDP terminology " + terminology);
      project.setPublic(true);
      project.setTerminology(terminology);
      project.setVersion(version);
      project.setBranch(Branch.ROOT);
      project.setUserRoleMap(userRoleMap);

      // add all validation checks to project
      project.setValidationChecks(Arrays.asList(validationChecks));

      service.setLastModifiedBy(loaderUser);

      PrecedenceList precedenceList =
          service.getPrecedenceList(terminology, version);
      project.setPrecedenceList(precedenceList);
      service.addProject(project);
    }

  }

  /**
   * Create test fixtures per test.
   *
   * @throws Exception the exception
   */
  @Before
  public void setup() throws Exception {
    // n/a
  }

  /**
   * Teardown.
   *
   * @throws Exception the exception
   */
  @After
  public void teardown() throws Exception {
    // n/a
  }

  /**
   * Teardown class.
   *
   * @throws Exception the exception
   */
  @AfterClass
  public static void teardownClass() throws Exception {
    // n/a
  }

}
