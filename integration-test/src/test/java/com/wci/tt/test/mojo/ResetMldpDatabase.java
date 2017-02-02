
/*
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.mojo;

import java.io.FileInputStream;
import java.io.InputStream;
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
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.ProjectJpa;
import com.wci.umls.server.jpa.services.MetadataServiceJpa;
import com.wci.umls.server.jpa.services.ProjectServiceJpa;
import com.wci.umls.server.services.ProjectService;

/**
 * A mechanism to reset to the stock dev database for MLDP work..
 */
public class ResetMldpDatabase {

  /** The properties. */
  static Properties config;

  /** The server. */
  static String server = "false";

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
    

    // output identifier handler
    Logger.getLogger(getClass()).info("DEFAULT id assignment handler: "
        + config.getProperty("identifier.assignment.handler.DEFAULT.class"));

    // re-create the database
    config.setProperty("hibernate.hbm2ddl.auto", "create");

    // Trigger a JPA event
    new MetadataServiceJpa().close();
    config.setProperty("hibernate.hbm2ddl.auto", "update");

    // List of MLDP terminologies
    String[] mldpTerminologies = {
        "allergy", "anatomy", "condition", "immunization", "lab", "med",
        "procedure", "vital"
    };

    ProjectService projectService = new ProjectServiceJpa();

    // for each terminology
    // - Create an editing project
    // - Load csv concepts file
    // - Load txt abbreviations file
    // - Load txt synonyms file
    for (String mldpTerminology : mldpTerminologies) {

      Logger.getLogger(getClass())
          .info("Creating project for terminology " + mldpTerminology);

      // create a project for the terminology
      ProjectJpa project = new ProjectJpa();
      project.setAutomationsEnabled(true);
      project.setName(
          "MLDP Project - " + mldpTerminology.substring(0, 1).toUpperCase()
              + mldpTerminology.substring(1));
      project.setLanguage("en");
      project.setDescription("Simple editing project for MLDP terminology "
          + mldpTerminology.substring(0, 1).toUpperCase()
          + mldpTerminology.substring(1));
      project.setPublic(true);
      project.setTerminology("MLDP-" + mldpTerminology.toUpperCase());
      project.setVersion("latest");
      project.setBranch(Branch.ROOT);

      projectService.setLastModifiedBy("loader");
      projectService.addProject(project);

      // load terminology
      Logger.getLogger(getClass())
          .info("Loading terminology " + mldpTerminology);
      Logger.getLogger(getClass())
          .info("  File: " + "../config/mldp/src/main/resources/data/"
              + mldpTerminology + "/" + mldpTerminology + "Concepts.csv");

      TerminologySimpleCsvLoaderAlgorithm termAlgo =
          new TerminologySimpleCsvLoaderAlgorithm();
      termAlgo.setTerminology("MLDP-" + mldpTerminology.toUpperCase());
      termAlgo.setVersion("latest");
      termAlgo.setAssignIdentifiersFlag(true);
      termAlgo.setInputFile("../config/mldp/src/main/resources/data/"
          + mldpTerminology + "/" + mldpTerminology + "Concepts.csv");
      termAlgo.setReleaseVersion("latest");
      termAlgo.setLastModifiedBy("loader");
      termAlgo.setProject(project);
      termAlgo.compute();

      // common variables for abbr/syns
      InputStream inStream;
      AbbreviationHandler abbrHandler;

      // load abbreviations file
      Logger.getLogger(getClass())
          .info("Loading abbreviations for terminology " + mldpTerminology);
      Logger.getLogger(getClass())
          .info("  File: " + "../config/mldp/src/main/resources/data/"
              + mldpTerminology + "/" + mldpTerminology + "Abbr.txt");

      inStream = new FileInputStream("../config/mldp/src/main/resources/data/"
          + mldpTerminology + "/" + mldpTerminology + "Abbr.txt");
      abbrHandler = new DefaultAbbreviationHandler();
      abbrHandler.setService(projectService);
      abbrHandler.setReviewFlag(false);
      abbrHandler.importAbbreviationFile(
          "MLDP-" + mldpTerminology.toUpperCase() + "-ABBR", inStream);

      // load synonyms file
      // TODO Commented out for now, out of scope
      // Logger.getLogger(getClass())
      // .info("Loading synonyms for terminology " + mldpTerminology);
      // Logger.getLogger(getClass())
      // .info(" File: " + "../config/mldp/src/main/resources/data/"
      // + mldpTerminology + "/" + mldpTerminology + "Sy.txt");
      //
      // inStream = new
      // FileInputStream("../config/mldp/src/main/resources/data/"
      // + mldpTerminology + "/" + mldpTerminology + "Sy.txt");
      // abbrHandler = new DefaultAbbreviationHandler();
      // abbrHandler.setService(projectService);
      // abbrHandler.setReviewFlag(false);
      // abbrHandler.importAbbreviationFile(
      // "MLDP-" + mldpTerminology.toUpperCase() + "-SY", inStream);

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
