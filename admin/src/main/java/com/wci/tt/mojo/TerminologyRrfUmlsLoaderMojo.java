/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

import com.wci.tt.jpa.services.ContentServiceJpa;
import com.wci.tt.services.ContentService;

/**
 * Goal which loads a set of RRF into a database.
 * 
 * See admin/loader/pom.xml for sample usage
 * 
 * @goal load-rrf-umls
 * 
 * @phase package
 */
public class TerminologyRrfUmlsLoaderMojo extends AbstractMojo {

  /**
   * Name of terminology to be loaded.
   * @parameter
   * @required
   */
  private String terminology;

  /**
   * The terminology version.
   * @parameter
   * @required
   */
  private String version;

  /**
   * Input directory.
   * @parameter
   * @required
   */
  private String inputDir;

  /**
   * Whether to run this mojo against an active server.
   *
   * @parameter
   */
  @SuppressWarnings("unused")
  private boolean server = false;

  /**
   * Mode - for recreating db
   * @parameter
   */
  @SuppressWarnings("unused")
  private String mode = null;

  /**
   * Instantiates a {@link TerminologyRrfUmlsLoaderMojo} from the specified
   * parameters.
   * 
   */
  public TerminologyRrfUmlsLoaderMojo() {
    // do nothing
  }

  /* see superclass */
  @Override
  public void execute() throws MojoFailureException {

    try {
      // getLog().info("RRF UMLS Terminology Loader called via mojo.");
      // getLog().info("WARN: Mojo changed for testing use of RXNORM/RRF loader.
      // Not applicable to other RRF terminologies");
      getLog().info("  Terminology        : " + terminology);
      getLog().info("  Terminology Version: " + version);
      getLog().info("  Input directory    : " + inputDir);
      getLog().info("  Config file        : "
          + System.getProperties().getProperty("tt.config"));

      ContentService contentService = new ContentServiceJpa();
      contentService.loadRrfTerminology(terminology, version, false, inputDir);
      contentService.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
