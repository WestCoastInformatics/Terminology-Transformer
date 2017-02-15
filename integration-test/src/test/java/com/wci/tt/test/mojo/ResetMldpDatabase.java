
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
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.PrecedenceList;
import com.wci.umls.server.jpa.ProjectJpa;
import com.wci.umls.server.jpa.UserJpa;
import com.wci.umls.server.jpa.services.MetadataServiceJpa;
import com.wci.umls.server.jpa.services.ProjectServiceJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.services.MetadataService;
import com.wci.umls.server.services.ProjectService;
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
  private String inputDir = null;

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
    inputDir = System.getProperty("input.dir");

    if (inputDir == null) {
      Logger.getLogger(getClass()).info("No input directory specified, using stock config files");
      inputDir = "../config/mldp/src/main/resources/data/";
    }
    if (!inputDir.endsWith("/")) {
      inputDir += "/";
    }
    Logger.getLogger(getClass()).info("Input directory: " + inputDir);
    File inputDirFile = new File(inputDir);
    if (!inputDirFile.isDirectory()) {
      throw new Exception("Specified input directory is not a directory");
    }
    if (inputDirFile.list().length == 0) {
      throw new Exception("Specified input directory is empty");
    }

    // List of MLDP terminologies
    final List<String> mldpTerminologies = new ArrayList<>();

    // {
    // "allergy", "anatomy", "condition", "immunization", "lab", "med",
    // "procedure", "vital"
    // };
    //
    // extract the terminologies from folder names
    for (final File f : inputDirFile.listFiles()) {
      if (f.isDirectory()) {
        Logger.getLogger(getClass())
            .info("Discovered terminology: " + f.getName());
        mldpTerminologies.add(f.getName());
        
        // log discovered files
        for (final String fileName : f.list()) {
          if (fileName.endsWith("Abbr.txt")) {
            Logger.getLogger(getClass()).info("  Found abbreviations file: " + fileName);
          }
          else if (fileName.endsWith("Concepts.csv")) {
            Logger.getLogger(getClass()).info("  Found concepts file     : " + fileName);
          }
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
    final ProjectService projectService = new ProjectServiceJpa();
    

    // set last modified by
    final String loaderUser = "loader";
    service.setLastModifiedBy(loaderUser);
    projectService.setLastModifiedBy(loaderUser);


    // shared variables
    InputStream inStream;
    AbbreviationHandler abbrHandler;
    Project project;

    Logger.getLogger(getClass()).info("Creating default users...");
    final Map<User, UserRole> userRoleMap = new HashMap<>();
    
    final Map<String, UserRole> userNames = new HashMap<>();
    userNames.put("admin", UserRole.ADMINISTRATOR);
    userNames.put("user", UserRole.USER);
    userNames.put("viewer", UserRole.VIEWER);

    for (String userName : userNames.keySet()) {
      User user = new UserJpa();
      user.setApplicationRole(userNames.get(userName));
      user.setEmail(userName + "@example.com");
      user.setUserName(userName);
      user.setName(userName.substring(0, 1).toUpperCase() + userName.substring(1));
      user = securityService.addUser(user);
      userRoleMap.put(user, userNames.get(userName));
    }
    

    
    Logger.getLogger(getClass()).info("  Done.");

    final String[] validationChecks =
        config.getProperty("validation.service.handler").split(",");
    
    ValidationResult result;

    // for each terminology
    // - Create an editing project
    // - Load csv concepts file
    // - Load txt abbreviations file
    // - Load txt synonyms file
    for (String mldpTerminology : mldpTerminologies) {

      final String terminology = "HKFT-" + mldpTerminology.toUpperCase();
      final String version = "latest";
    
      // load terminology
      Logger.getLogger(getClass())
          .info("Loading terminology " + mldpTerminology);
      Logger.getLogger(getClass()).info("  File: " + inputDir
          + mldpTerminology + "/" + mldpTerminology + "Concepts.csv");

      final TerminologySimpleCsvLoaderAlgorithm termAlgo =
          new TerminologySimpleCsvLoaderAlgorithm();
      termAlgo.setTerminology(terminology);
      termAlgo.setVersion(version);
      termAlgo.setAssignIdentifiersFlag(true);
      termAlgo.setInputFile(inputDir + mldpTerminology + "/"
          + mldpTerminology + "Concepts.csv");
      termAlgo.setReleaseVersion(version);
      termAlgo.setLastModifiedBy(loaderUser);
      termAlgo.compute();
      result = termAlgo.getValidationResult();
      
      // output results
      for (String error : result.getErrors()) {
        Logger.getLogger(getClass()).info("Error: " + error);
      }
      for (String comment : result.getComments()) {
        Logger.getLogger(getClass()).info(comment);
      }
      for (String warning : result.getWarnings()) {
        Logger.getLogger(getClass()).info("Warning: " + warning);
      }
     

      // load abbreviations file
      Logger.getLogger(getClass())
          .info("Loading abbreviations for terminology " + mldpTerminology);
      Logger.getLogger(getClass()).info("  File: " + inputDir
          + mldpTerminology + "/" + mldpTerminology + "Abbr.txt");

      inStream = new FileInputStream(inputDir
          + mldpTerminology + "/" + mldpTerminology + "Abbr.txt");
      abbrHandler = new DefaultAbbreviationHandler();
      abbrHandler.setService(projectService);
      abbrHandler.setReviewFlag(false);
      result = abbrHandler.importAbbreviationFile(
          "HKFT-" + mldpTerminology.toUpperCase(), inStream);
      
      // output results
      for (String error : result.getErrors()) {
        Logger.getLogger(getClass()).info("Error: " + error);
      }
      for (String comment : result.getComments()) {
        Logger.getLogger(getClass()).info(comment);
      }
      for (String warning : result.getWarnings()) {
        Logger.getLogger(getClass()).info("Warning: " + warning);
      }

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

      // use metadata service here (no commit clashing)
   
      PrecedenceList precedenceList =
          service.getPrecedenceList(terminology, version);
      project.setPrecedenceList(precedenceList);
      
       project = service.addProject(project);
      
      Logger.getLogger(getClass()).info("  Project successfully created");
     
    }
    
    // close project service
  
    service.close();
    service.closeFactory();
    
    Logger.getLogger(getClass()).info("Reset MLDP database complete");

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
