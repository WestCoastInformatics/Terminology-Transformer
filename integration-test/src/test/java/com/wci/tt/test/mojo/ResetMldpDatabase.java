
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
    InvocationRequest request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("Simple"));
    request.setGoals(Arrays.asList("clean", "install"));
    Properties p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("server", server);
    p.setProperty("mode", "create");
    p.setProperty("terminology", "HKFT-LABS");
    p.setProperty("version", "latest");
    p.setProperty("input.dir", "../config/mldp/src/main/resources/data/labs");
    if (System.getProperty("input.dir") != null) {
      p.setProperty("input.dir", System.getProperty("input.dir"));
    }
    request.setProperties(p);
    request.setDebug(false);
    DefaultInvoker invoker = new DefaultInvoker();
    InvocationResult result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("Simple"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("server", server);
    p.setProperty("mode", "update");
    p.setProperty("terminology", "HKFT-MEDS");
    p.setProperty("version", "latest");
    p.setProperty("input.dir", "../config/mldp/src/main/resources/data/meds");
    if (System.getProperty("input.dir") != null) {
      p.setProperty("input.dir", System.getProperty("input.dir"));
    }
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("Simple"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("server", server);
    p.setProperty("mode", "update");
    p.setProperty("terminology", "HKFT-CONDITIONS");
    p.setProperty("version", "latest");
    p.setProperty("input.dir",
        "../config/mldp/src/main/resources/data/conditions");
    if (System.getProperty("input.dir") != null) {
      p.setProperty("input.dir", System.getProperty("input.dir"));
    }
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("Simple"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("server", server);
    p.setProperty("mode", "update");
    p.setProperty("terminology", "HKFT-PROCEDURES");
    p.setProperty("version", "latest");
    p.setProperty("input.dir",
        "../config/mldp/src/main/resources/data/procedures");
    if (System.getProperty("input.dir") != null) {
      p.setProperty("input.dir", System.getProperty("input.dir"));
    }
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Load lab acronym config files
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("LoadConfig"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("type", "HKFT-LABS-ABBR");
    p.setProperty("input.file",
        "../config/mldp/src/main/resources/data/labs/labAbbr.txt");
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }
    // Load lab sy config files
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("LoadConfig"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("type", "labSy");
    p.setProperty("input.file",
        "../config/mldp/src/main/resources/data/labs/labSy.txt");
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }


    // Load meds acronym config files
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("LoadConfig"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("type", "HKFT-MEDS-ABBR");
    p.setProperty("input.file",
        "../config/mldp/src/main/resources/data/meds/medAbbr.txt");
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }
    

    // Load meds sy config files
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("LoadConfig"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("type", "medSy");
    p.setProperty("input.file",
        "../config/mldp/src/main/resources/data/meds/medSy.txt");
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Load condition acronym config files
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("LoadConfig"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("type", "HKFT-CONDITIONS-ABBR");
    p.setProperty("input.file",
        "../config/mldp/src/main/resources/data/conditions/conditionAbbr.txt");
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }
    

    // Load condition sy config files
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("LoadConfig"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("type", "conditionSy");
    p.setProperty("input.file",
        "../config/mldp/src/main/resources/data/conditions/conditionSy.txt");
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }

    // Load procedure acronym config files
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("LoadConfig"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("type", "HKFT-PROCEDURES-ABBR");
    p.setProperty("input.file",
        "../config/mldp/src/main/resources/data/procedures/procedureAbbr.txt");
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }


    // Load procedure sy config files
    request = new DefaultInvocationRequest();
    request.setPomFile(new File("../admin/pom.xml"));
    request.setProfiles(Arrays.asList("LoadConfig"));
    request.setGoals(Arrays.asList("clean", "install"));
    p = new Properties();
    p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    p.setProperty("type", "procedureSy");
    p.setProperty("input.file",
        "../config/mldp/src/main/resources/data/procedures/procedureSy.txt");
    request.setProperties(p);
    request.setDebug(false);
    invoker = new DefaultInvoker();
    result = invoker.execute(request);
    if (result.getExitCode() != 0) {
      throw result.getExecutionException();
    }
    // Generate Sample Data
    // request = new DefaultInvocationRequest();
    // request.setPomFile(new File("../admin/loader/pom.xml"));
    // request.setProfiles(Arrays.asList("GenerateSampleData"));
    // request.setGoals(Arrays.asList("clean", "install"));
    // p = new Properties();
    // p.setProperty("run.config.umls", System.getProperty("run.config.umls"));
    // p.setProperty("mode", "update");
    // request.setProperties(p);
    // invoker = new DefaultInvoker();
    // result = invoker.execute(request);
    // if (result.getExitCode() != 0) {
    // throw result.getExecutionException();
    // }

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
