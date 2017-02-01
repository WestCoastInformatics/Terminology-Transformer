
/*
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.test.mojo;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.ProjectJpa;
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

    // Load Simple terminology
    InvocationRequest request;
    Properties p;
    DefaultInvoker invoker;
    InvocationResult result;

    // recreate the database
    request = new DefaultInvocationRequest();
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("Updatedb"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("server", server);
    p.setProperty("mode", "create");
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // List of MLDP terminologies
    String[] mldpTerminologies = {
        "allergy", "anatomy", "condition", "immunization", "lab", "med", "procedure", "vital"
    };
    
    ProjectService projectService = new ProjectServiceJpa();
    
    // for each terminology
    // - Create an editing project
    // - Load csv concepts file
    // - Load txt abbreviations file
    // - Load txt synonyms file
    for (String mldpTerminology : mldpTerminologies) {
      
      // create a project for the terminology
      ProjectJpa project = new ProjectJpa();
      project.setAutomationsEnabled(true);
      project.setName("MLDP Project - " + mldpTerminology.substring(0, 1).toUpperCase() + mldpTerminology.substring(1));
      project.setLanguage("en");
      project.setDescription("Simple editing project for MLDP terminology " + mldpTerminology.substring(0, 1).toUpperCase() + mldpTerminology.substring(1));
      project.setPublic(true);
      project.setTerminology("MLDP-" + mldpTerminology.toUpperCase());
      project.setVersion("latest");
      
      projectService.setLastModifiedBy("loader");
      projectService.addProject(project);
      
      // load terminology
      request = new DefaultInvocationRequest();
      request.setPomFile(new File("../admin/pom.xml"));
      request.setProfiles(Arrays.asList("Simple"));
      request.setGoals(Arrays.asList("clean", "install"));
      p = new Properties();
      p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
      p.setProperty("terminology", "HKFT-" + mldpTerminology.toUpperCase());
      p.setProperty("version", "latest");
      p.setProperty("input.dir", "../config/mldp/src/main/resources/data/" + mldpTerminology + "/" + mldpTerminology + "Concepts.csv");
      request.setProperties(p);
      request.setDebug(false);
      if (result.getExitCode() != 0) {
        throw result.getExecutionException();
      }
      
      // Load .txt Abbreviations File
      request = new DefaultInvocationRequest();
      request.setPomFile(new File("../admin/pom.xml"));
      request.setProfiles(Arrays.asList("LoadConfig"));
      request.setGoals(Arrays.asList("clean", "install"));
      p = new Properties();
      p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
      p.setProperty("type", "HKFT-" + mldpTerminology.toUpperCase() + "-ABBR");
      p.setProperty("input.file",
          "../config/mldp/src/main/resources/data/" + mldpTerminology + "/" + mldpTerminology + "Abbr.txt");
      request.setProperties(p);
      request.setDebug(false);
      invoker = new DefaultInvoker();
      result = invoker.execute(request);
      if (result.getExitCode() != 0) {
        throw result.getExecutionException();
      }
      
      // Load  .txt Synonyms File
      request = new DefaultInvocationRequest();
      request.setPomFile(new File("../admin/pom.xml"));
      request.setProfiles(Arrays.asList("LoadConfig"));
      request.setGoals(Arrays.asList("clean", "install"));
      p = new Properties();
      p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
      p.setProperty("type", "HKFT-" + mldpTerminology.toUpperCase() + "-SY");
      p.setProperty("input.file",
          "../config/mldp/src/main/resources/data/" + mldpTerminology + "/" + mldpTerminology + "Sy.txt");
      request.setProperties(p);
      request.setDebug(false);
      invoker = new DefaultInvoker();
      result = invoker.execute(request);
      if (result.getExitCode() != 0) {
        throw result.getExecutionException();
      }
      
    
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
