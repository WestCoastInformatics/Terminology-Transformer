/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.loaders;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.wci.tt.SourceData;
import com.wci.tt.jpa.services.SourceDataServiceJpa;
import com.wci.tt.jpa.services.algo.NdcLoaderAlgorithm;
import com.wci.tt.services.SourceDataService;
import com.wci.tt.services.handlers.SourceDataLoader;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.LocalException;
import com.wci.umls.server.jpa.algo.LabelSetMarkedParentAlgorithm;
import com.wci.umls.server.jpa.algo.TransitiveClosureAlgorithm;
import com.wci.umls.server.jpa.algo.TreePositionAlgorithm;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.rest.SecurityServiceRest;
import com.wci.umls.server.model.content.ConceptSubset;
import com.wci.umls.server.model.content.Subset;
import com.wci.umls.server.model.meta.IdType;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.rest.impl.SecurityServiceRestImpl;
import com.wci.umls.server.services.ContentService;
import com.wci.umls.server.services.helpers.ProgressEvent;
import com.wci.umls.server.services.helpers.ProgressListener;

/**
 * Converter for RxNorm files.
 */
public class NdcSourceDataLoader implements SourceDataLoader {

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The source data. */
  private SourceData sourceData;

  /** The terminology. */
  private String terminology;

  /** The version. */
  private String version;

  /** The prefix. */
  private String prefix;

  /** The props. */
  private Properties props;

  /**
   * Instantiates an empty {@link NdcSourceDataLoader}.
   */
  public NdcSourceDataLoader() {
    // n/a
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  @Override
  public String getName() {
    return "NDC Source Data Loader";
  }

  /**
   * Convert.
   *
   * @throws Exception the exception
   */
  @Override
  public void compute() throws Exception {

    // check pre-requisites
    if (sourceData.getSourceDataFiles().size() == 0) {
      throw new Exception(
          "No source data files specified for source data object "
              + sourceData.getName());
    }
    if (sourceData.getLoader().isEmpty()) {
      throw new Exception(
          "No source data loader specified for source data object "
              + sourceData.getName());
    }

    // find the data directory from the first sourceDataFile
    String inputDir = sourceData.getSourceDataFiles().get(0).getPath();

    if (!new File(inputDir).isDirectory()) {
      throw new LocalException(
          "Source data directory is not a directory: " + inputDir);
    }

    SourceDataService sourceDataService = new SourceDataServiceJpa();
    sourceDataService.updateSourceData(sourceData);

    // Use content service rest because it has "loadRrfTerminology"
    final Properties config = ConfigUtility.getConfigProperties();
    final SecurityServiceRest securityService = new SecurityServiceRestImpl();
    final String adminAuthToken =
        securityService.authenticate(config.getProperty("admin.user"),
            config.getProperty("admin.password")).getAuthToken();
    
    try {
      sourceData.setLoaderStatus(SourceData.Status.LOADING);
      sourceDataService.updateSourceData(sourceData);
      // Load RRF
      final NdcLoaderAlgorithm algorithm = new NdcLoaderAlgorithm();
      algorithm.setTerminology(terminology);
      algorithm.setVersion(version);
      algorithm.setInputDir(inputDir);
      algorithm.compute();
      algorithm.close();

      // Compute transitive closure
      // Obtain each terminology and run transitive closure on it with the
      // correct id type
      // Refresh caches after metadata has changed in loader
      ContentService contentService = new ContentServiceJpa();
      for (final Terminology t : contentService.getTerminologyLatestVersions()
          .getObjects()) {
        // Only compute for organizing class types
        if (t.getOrganizingClassType() != null) {
          TransitiveClosureAlgorithm algo = new TransitiveClosureAlgorithm();
          algo.setTerminology(t.getTerminology());
          algo.setVersion(t.getVersion());
          algo.setIdType(t.getOrganizingClassType());
          // some terminologies may have cycles, allow these for now.
          algo.setCycleTolerant(true);
          algo.compute();
          algo.close();
        }
      }

      // Compute tree positions
      // Refresh caches after metadata has changed in loader
      for (final Terminology t : contentService.getTerminologyLatestVersions()
          .getObjects()) {
        // Only compute for organizing class types
        if (t.getOrganizingClassType() != null) {
          TreePositionAlgorithm algo = new TreePositionAlgorithm();
          algo.setTerminology(t.getTerminology());
          algo.setVersion(t.getVersion());
          algo.setIdType(t.getOrganizingClassType());
          // some terminologies may have cycles, allow these for now.
          algo.setCycleTolerant(true);
          // compute "semantic types" for concept hierarchies
          if (t.getOrganizingClassType() == IdType.CONCEPT) {
            algo.setComputeSemanticType(true);
          }
          algo.compute();
          algo.close();
        }
      }

      // Compute label sets - after transitive closure
      // for each subset, compute the label set
      for (final Terminology t : contentService.getTerminologyLatestVersions()
          .getObjects()) {
        for (final Subset subset : contentService
            .getConceptSubsets(t.getTerminology(), t.getVersion(), Branch.ROOT)
            .getObjects()) {
          final ConceptSubset conceptSubset = (ConceptSubset) subset;
          if (conceptSubset.isLabelSubset()) {
            Logger.getLogger(getClass())
                .info("  Create label set for subset = " + subset);
            LabelSetMarkedParentAlgorithm algo3 =
                new LabelSetMarkedParentAlgorithm();
            algo3.setSubset(conceptSubset);
            algo3.compute();
            algo3.close();
          }
        }
      }
      // Clean-up

      ConfigUtility
          .deleteDirectory(new File(inputDir, "/RRF-sorted-temp/"));

      sourceData.setLoaderStatus(SourceData.Status.FINISHED);
      sourceDataService.updateSourceData(sourceData);

    } catch (Exception e) {
      sourceData.setLoaderStatus(SourceData.Status.FAILED);
      sourceDataService.updateSourceData(sourceData);
      throw new Exception("Loading source data failed - " + sourceData, e);
    } finally {
      sourceDataService.close();
    }
  }

  /* see superclass */
  @Override
  public void reset() throws Exception {
    // n/a
  }

  /**
   * Fires a {@link ProgressEvent}.
   * @param pct percent done
   * @param note progress note
   */
  public void fireProgressEvent(int pct, String note) {
    ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(getClass()).info("    " + pct + "% " + note);
  }

  /* see superclass */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /* see superclass */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /* see superclass */
  @Override
  public void cancel() {
    throw new UnsupportedOperationException("cannot cancel.");
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    props = new Properties();
    props.putAll(p);
    if (props.containsKey("prefix")) {
      prefix = props.getProperty("prefix");
    }
  }

  /* see superclass */
  @Override
  public void setSourceData(SourceData sourceData) {
    this.sourceData = sourceData;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a
  }

  @Override
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  @Override
  public void setVersion(String version) {
    this.version = version;
  }
}
