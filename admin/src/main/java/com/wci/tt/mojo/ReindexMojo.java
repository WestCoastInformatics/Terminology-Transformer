/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.mojo;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.rest.client.ContentClientRest;
import com.wci.umls.server.rest.impl.ContentServiceRestImpl;
import com.wci.umls.server.services.SecurityService;

/**
 * Goal which reindexes model JPA objects
 * 
 * See admin/pom.xml for sample usage
 * 
 * @goal reindex
 * 
 * @phase package
 */
public class ReindexMojo extends AbstractMojo {

  /**
   * The specified objects to index.
   * @parameter
   *
   */

  private String indexedObjects = null;

  /**
   * Whether to run this mojo against an active server.
   * @parameter
   * 
   */

  private boolean server = false;

  /**
   * Instantiates a {@link ReindexMojo} from the specified parameters.
   * 
   */
  public ReindexMojo() {
    // do nothing
  }

  @Override
  public void execute() throws MojoFailureException {
    try {
      getLog().info("Lucene reindexing called via mojo.");
      getLog().info("  Indexed objects : " + indexedObjects);
      getLog().info("  Expect server up: " + server);
      Properties properties = ConfigUtility.getConfigProperties();

      boolean serverRunning = ConfigUtility.isServerActive();

      getLog()
          .info("Server status detected:  " + (!serverRunning ? "DOWN" : "UP"));

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
      service.close();

      if (!serverRunning) {
        getLog().info("Running directly");

        ContentServiceRestImpl contentService = new ContentServiceRestImpl();
        contentService.luceneReindex(indexedObjects, authToken);

      } else {
        getLog().info("Running against server");

        // invoke the client
        ContentClientRest client = new ContentClientRest(properties);
        client.luceneReindex(indexedObjects, authToken);
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new MojoFailureException("Unexpected exception:", e);
    }

  }

}
