/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.mojo;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.Query;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.wci.tt.jpa.services.handlers.NdcSourceDataHandler;
import com.wci.umls.server.SourceData;
import com.wci.umls.server.SourceDataFile;
import com.wci.umls.server.jpa.SourceDataFileJpa;
import com.wci.umls.server.jpa.SourceDataJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.SourceDataServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Attribute;
import com.wci.umls.server.services.RootService;
import com.wci.umls.server.services.SourceDataService;
import com.wci.umls.server.services.handlers.ExceptionHandler;

/**
 * Used for data load to get NDC into a database and bound to a source data
 * object.
 * 
 * See admin/pom.xml for a sample execution.
 * 
 * @goal ndc-rxnorm-update
 * @phase package
 */
public class NdcUpdateSourceDataHandlerMojo extends SourceDataMojo {

  /**
   * Name of terminology to be loaded.
   * @parameter
   * @required
   */
  private String terminology;

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
  @SuppressWarnings("unchecked")
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Starting NDC update data load");
    getLog().info("  terminology = " + terminology);
    getLog().info("  inputDir = " + inputDir);

    SourceDataService sdService = null;
    ContentServiceJpa contentService = null;
    try {

      sdService = new SourceDataServiceJpa();
      contentService = new ContentServiceJpa();

      // Check preconditions
      if (inputDir == null) {
        throw new Exception("Input directory not specified");
      }

      final File dir = new File(inputDir);
      if (!dir.exists()) {
        throw new Exception("Input directory does not exist");
      }

      if (!dir.isDirectory()) {
        throw new Exception("Input directory must be a directory");
      }

      //
      // Identify the max version currently in the database and remove the
      // attributes
      //
      String version =
          contentService.getTerminologyLatestVersion(terminology).getVersion();

      // Remove attributes from current version
      getLog().info("Remove attributes from current version");
      contentService.setTransactionPerOperation(false);
      contentService.beginTransaction();
      final Query query = contentService.getEntityManager().createQuery(
          "SELECT a.id FROM AtomJpa a WHERE terminology = :terminology "
              + " AND version = :version");
      query.setParameter("terminology", terminology);
      query.setParameter("version", version);
      int ct = 0;
      for (final Long id : (List<Long>) query.getResultList()) {
        final Atom atom = contentService.getAtom(id);
        final Set<Attribute> attributes = new HashSet<>();
        // Clear attributes from atoms and save attribute list
        for (final Attribute attribute : new HashSet<>(atom.getAttributes())) {
          attributes.add(attribute);
          atom.getAttributes().remove(attribute);
        }
        // Update atom
        contentService.updateAtom(atom);
        // Remove attributes
        for (final Attribute attribute : attributes) {
          contentService.removeAttribute(attribute.getId());
        }
        contentService.logAndCommit(++ct, RootService.logCt,
            RootService.commitCt);
      }
      contentService.commitClearBegin();

      getLog().info("Insert latest version (with attributes)");

      // Find the highest version to process
      // Only load attributes for that version
      String maxVersion = "00000000";
      for (File versionDir : dir.listFiles()) {
        // Skip if not an 8 digit yyyyMMdd directory
        if (!versionDir.getName().matches("\\d{8}")) {
          continue;
        } else {
          version = versionDir.getName();
          if (version.compareTo(maxVersion) > 0) {
            maxVersion = version;
          }
        }
      }
      getLog().info("  maxVersion = " + maxVersion);
      // Get the version directory to process (the max version)
      File versionDir = new File(dir, maxVersion);

      // Verify presence of an "rrf" directory
      File[] versionDirContents = versionDir.listFiles();
      File rrfDir = null;
      for (File f : versionDirContents) {
        if (f.getName().equals("rrf")) {
          rrfDir = f;
        }
      }
      if (rrfDir == null) {
        throw new Exception("No rrf directory in the release: "
            + versionDir.getCanonicalPath());
      }

      // Create source data file
      final SourceDataFile sdFile = new SourceDataFileJpa();
      sdFile.setDirectory(true);
      sdFile.setLastModifiedBy("loader");
      sdFile.setName(rrfDir.getName());
      sdFile.setPath(rrfDir.getAbsolutePath());
      sdFile.setSize(1000000L);
      sdFile.setTimestamp(new Date());
      sdService.addSourceDataFile(sdFile);
      getLog().info("    file = " + sdFile);

      // Create loader
      final NdcSourceDataHandler loader = new NdcSourceDataHandler();

      // Create and add the source data
      final SourceData sourceData = new SourceDataJpa();
      sourceData.setName(getName(terminology, versionDir.getName()));
      sourceData.setDescription(
          "Set of RXNORM-NDC files loaded from " + versionDir.getName());
      sourceData.setLastModifiedBy("loader");
      sourceData.setHandler(loader.getName());
      sourceData.getSourceDataFiles().add(sdFile);
      sourceData.setVersion(versionDir.getName());
      sourceData.setTerminology(terminology);
      sdService.addSourceData(sourceData);
      getLog().info("    source data = " + sourceData);

      sdFile.setSourceData(sourceData);
      sdService.updateSourceDataFile(sdFile);
      getLog().info("    file (with reference) = " + sdFile);

      // Now, invoke the loader
      final Properties p = new Properties();
      loader.setSourceData(sourceData);
      loader.setProperties(p);
      loader.setAttributesFlag(true);
      loader.compute();
      loader.close();

      getLog().info("Done loading " + versionDir.getCanonicalPath());

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
      if (sdService != null) {
        try {
          sdService.close();
        } catch (Exception e) {
          // n/a
        }
      }
      if (contentService != null) {
        try {
          contentService.close();
        } catch (Exception e) {
          // n/a
        }
      }
    }

  }
}