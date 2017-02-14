/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.mojo;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

import com.wci.tt.jpa.services.algo.TerminologySimpleCsvLoaderAlgorithm;
import com.wci.umls.server.Project;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.ProjectList;
import com.wci.umls.server.jpa.ProjectJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.rest.client.ContentClientRest;
import com.wci.umls.server.rest.impl.ContentServiceRestImpl;
import com.wci.umls.server.services.ContentService;
import com.wci.umls.server.services.SecurityService;

/**
 * Goal which updates the db to sync it with the model via JPA.
 * 
 * See admin/pom.xml for sample usage
 * 
 * @goal simple-csv-loader
 * 
 * @phase package
 */
public class TerminologySimpleCsvLoaderMojo extends AbstractMojo {

  /**
   * Input file
   * @parameter
   * @required
   */
  public String inputFile;

  /**
   * Terminology
   * @parameter
   * @required
   */
  public String terminology;

  /**
   * Version
   * @parameter
   * @required
   */
  public String version;

  /**
   * Retain ids from file flag
   * @parameter
   */
  public boolean keepIds = false;

  /**
   * Delete existing terminology before loading
   * @parameter
   */
  public boolean replace = false;

  /**
   * Whether server running
   * @parameter
   */
  public boolean server = false;

  /**
   * Database mode
   * @parameter
   */

  /**
   * Instantiates a {@link TerminologySimpleCsvLoaderMojo} from the specified
   * parameters.
   * 
   */
  public TerminologySimpleCsvLoaderMojo() {
    // do nothing
  }

  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Start updating database schema...");
    getLog().info("  input.file   = " + inputFile);
    getLog().info("  terminology  = " + terminology);
    getLog().info("  version      = " + version);
    getLog().info("  use file ids = " + keepIds);
    getLog().info("  replace flag = " + replace);
    try {

      Properties config = ConfigUtility.getConfigProperties();

      //
      // Prerequsites
      //
      if (inputFile == null || inputFile.isEmpty()) {
        throw new Exception("Input file must be specified");
      }
      if (terminology == null || terminology.isEmpty()) {
        throw new Exception("Terminology must be specified");
      }
      if (version == null || version.isEmpty()) {
        throw new Exception("Version must be specified");
      }

      Project project = null;
      final ContentService service = new ContentServiceJpa();
      service.setLastModifiedBy("loader");

      // authenticate
      final SecurityService securityService = new SecurityServiceJpa();
      final String authToken =
          securityService.authenticate(config.getProperty("admin.user"),
              config.getProperty("admin.password")).getAuthToken();

      // check if terminology already exists
      for (final Terminology term : service.getTerminologies().getObjects()) {
        if (term.getTerminology().equals(terminology)) {
          if (!replace) {
            throw new Exception(
                "Terminology already exists and replace mode not specified");
          } else {
            getLog().info("Terminology exists and replace requested");
            if (!server) {
              final ContentServiceRestImpl rest = new ContentServiceRestImpl();
              rest.removeTerminology(terminology, version, authToken);
            } else {
              final ContentClientRest rest = new ContentClientRest(config);
              rest.removeTerminology(terminology, version, authToken);
            }
          }
        }
      }
      
      // TODO Not sensitive to server status

      // check if a project already exists for this terminology
      ProjectList projects =
          service.findProjects("terminology:" + terminology, null);
      if (projects.size() > 0) {
        project = projects.getObjects().get(0);
        getLog().info("Using existing project: " + project.getName());
      } else {

        project = new ProjectJpa();
        project.setAutomationsEnabled(true);
        project.setBranch(Branch.ROOT);
        project.setEditingEnabled(true);
        project.setLanguage("en");
        project.setDescription("Simple editing project for " + terminology);
        project.setPublic(true);
        project.setName("Project: " + terminology);
        project.setTerminology(terminology);
        project.setVersion(version);
        project = service.addProject(project);
        getLog().info("Creating new project: " + project.getName());

        // note: user management and validation checks not enabled by default
      }
      TerminologySimpleCsvLoaderAlgorithm algo =
          new TerminologySimpleCsvLoaderAlgorithm();

      algo.setProperties(config);
      algo.setAssignIdentifiersFlag(true);
      algo.setInputFile(inputFile);
      algo.setKeepFileIdsFlag(keepIds);
      algo.setTerminology(terminology);
      algo.setVersion(version);
      algo.setReleaseVersion(version);
      algo.setMolecularActionFlag(false);
      algo.setProject(project);
      algo.setLastModifiedFlag(true);
      algo.setLastModifiedBy("loader");

      algo.checkPreconditions();
      algo.compute();

      getLog().info("done ...");
    } catch (

    Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }

  }

}
