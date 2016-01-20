/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.mojo;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;
import com.wci.tt.jpa.SourceDataFileJpa;
import com.wci.tt.jpa.SourceDataJpa;
import com.wci.tt.jpa.loaders.RrfSourceDataLoader;
import com.wci.tt.jpa.services.SourceDataServiceJpa;
import com.wci.tt.services.SourceDataService;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.algo.LuceneReindexAlgorithm;
import com.wci.umls.server.jpa.services.MetadataServiceJpa;
import com.wci.umls.server.services.handlers.ExceptionHandler;

/**
 * Used to for sample data load to get RXNORM into a database and bound to a
 * source data object.
 * 
 * See admin/pom.xml for a sample execution.
 * 
 * @goal sample
 * @phase package
 */
public class RrfSourceDataLoaderMojo extends AbstractMojo {

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
   * create or update mode.
   * @parameter
   */
  private String mode;

  /**
   * The terminology version.
   * @parameter
   */
  private String prefix;

  /**
   * Input directory.
   * @parameter
   * @required
   */
  private String inputDir;

  /**
   * Executes the plugin.
   *
   * @throws MojoExecutionException the mojo execution exception
   * @throws MojoFailureException the mojo failure exception
   */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Starting sample data load");
    getLog().info("  terminology = " + terminology);
    getLog().info("  version = " + version);
    getLog().info("  prefix = " + prefix);
    getLog().info("  inputDir = " + inputDir);

    SourceDataService service = null;
    try {

      final Properties properties = ConfigUtility.getConfigProperties();

      // Create DB
      if (mode != null && mode.equals("create")) {
        getLog().info("Recreate database");
        // This will trigger a rebuild of the db
        properties.setProperty("hibernate.hbm2ddl.auto", mode);
        // Trigger a JPA event
        new MetadataServiceJpa().close();
        properties.remove("hibernate.hbm2ddl.auto");

        // Rebuild Indexes
        final LuceneReindexAlgorithm reindex = new LuceneReindexAlgorithm();
        reindex.compute();
      }

      // SEtup sample data

      service = new SourceDataServiceJpa();
      // As this is a sample loader and not an integration test,
      // we will use the JPA service layer directly.

      if (inputDir == null) {
        throw new Exception("Input directory not specified");
      }

      final File dir = new File(inputDir);
      if (!dir.exists()) {
        throw new Exception("Input directory does not exist");
      }

      final SourceDataFile sdFile = new SourceDataFileJpa();
      sdFile.setDirectory(true);
      sdFile.setLastModifiedBy("loader");
      sdFile.setName(dir.getName());
      sdFile.setPath(inputDir);
      // TODO
      sdFile.setSize(1000000L);
      sdFile.setTimestamp(new Date());
      service.addSourceDataFile(sdFile);
      getLog().info("    file = " + sdFile);

      // Create loader
      final RrfSourceDataLoader loader = new RrfSourceDataLoader();

      // Create and add the source data
      final SourceData sourceData = new SourceDataJpa();
      sourceData.setName("RXNORM source data");
      sourceData.setDescription("Set of RRF files loaded from " + dir);
      sourceData.setLastModifiedBy("loader");
      sourceData.setLoader(loader.getName());
      sourceData.getSourceDataFiles().add(sdFile);
      service.addSourceData(sourceData);
      getLog().info("    source data = " + sourceData);

      sdFile.setSourceDataName(sourceData.getName());
      service.updateSourceDataFile(sdFile);
      getLog().info("    file (with reference) = " + sdFile);

      // Now, invoke the loader
      final Properties p = new Properties();
      p.setProperty("prefix", "RXN");
      loader.setSourceData(sourceData);
      loader.setTerminology(terminology);
      loader.setVersion(version);
      loader.setProperties(p);

      loader.compute();

      getLog().info("Done ...");

    } catch (Exception e) {
      // Send email if something went wrong
      try {
        ExceptionHandler.handleException(e, "Error loading sample source data");
      } catch (Exception e1) {
        e1.printStackTrace();
        throw new MojoFailureException(e.getMessage());
      }

    } finally {
      // Close service(s)
      if (service != null) {
        try {
          service.close();
        } catch (Exception e) {
          // n/a
        }
      }
    }

  }
}