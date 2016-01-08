/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.mojo;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

import com.wci.tt.helpers.ConfigUtility;
import com.wci.tt.jpa.services.SecurityServiceJpa;
import com.wci.tt.jpa.services.rest.ContentServiceRest;
import com.wci.tt.rest.client.ContentClientRest;
import com.wci.tt.rest.impl.ContentServiceRestImpl;
import com.wci.tt.services.SecurityService;

/**
 * Goal which removes a terminology from a database.
 * 
 * See admin/remover/pom.xml for sample usage
 * 
 * @goal remove-terminology
 * 
 * @phase package
 */
public class TerminologyRemoverMojo extends AbstractMojo {

  /**
   * Name of terminology to be removed.
   * @parameter
   * @required
   */
  private String terminology;

  /**
   * Terminology version to remove.
   * @parameter
   * @required
   */
  private String version;

  /**
   * Whether to run this mojo against an active server
   * @parameter
   */
  private boolean server = false;

  /**
   * Instantiates a {@link TerminologyRemoverMojo} from the specified
   * parameters.
   * 
   */
  public TerminologyRemoverMojo() {
    // do nothing
  }

  /* see superclass */
  @Override
  public void execute() throws MojoFailureException {
    getLog().info("Starting removing terminology");
    getLog().info("  terminology = " + terminology);
    getLog().info("  version = " + version);
    try {

      Properties properties = ConfigUtility.getConfigProperties();

      boolean serverRunning = ConfigUtility.isServerActive();

      getLog().info(
          "Server status detected:  " + (!serverRunning ? "DOWN" : "UP"));

      if (serverRunning && !server) {
        throw new MojoFailureException(
            "Mojo expects server to be down, but server is running");
      }

      if (!serverRunning && server) {
        throw new MojoFailureException(
            "Mojo expects server to be running, but server is down");
      }

      // authenticate
      SecurityService service = new SecurityServiceJpa();
      String authToken =
          service.authenticate(properties.getProperty("admin.user"),
              properties.getProperty("admin.password")).getAuthToken();

      if (!serverRunning) {
        getLog().info("Running directly");

        getLog().info("  Remove concepts");
        ContentServiceRest contentService = new ContentServiceRestImpl();
        contentService.removeTerminology(terminology, version, authToken);

       
      } else {
        getLog().info("Running against server");

        getLog().info("  Remove concepts");
        ContentClientRest contentService = new ContentClientRest(properties);
        contentService.removeTerminology(terminology, version, authToken);

        
      }
      service.close();

      getLog().info("done ...");
      System.exit(0);
    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }
  }

}
