/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredDataContextJpa;
import com.wci.tt.jpa.infomodels.IngredientModel;
import com.wci.tt.jpa.infomodels.MedicationModel;
import com.wci.tt.jpa.infomodels.ProcedureModel;
import com.wci.tt.jpa.infomodels.SiteModel;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.services.ContentService;

/**
 * MLDP provider for mapping raw terms to procedures.
 */
public class MldpMedicationProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  /**
   * Instantiates an empty {@link MldpMedicationProvider}.
   *
   * @throws Exception the exception
   */
  public MldpMedicationProvider() throws Exception {

    // Configure input/output matchers
    DataContextMatcher inputMatcher = new DataContextMatcher();
    inputMatcher.configureContext(DataContextType.NAME, null, "Med", null,
        null, null, null);
    DataContextMatcher outputMatcher = new DataContextMatcher();
    outputMatcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        ProcedureModel.class.getName(), null, null);
    addMatcher(inputMatcher, outputMatcher);
  }

  /* see superclass */
  @Override
  public String getName() {
    return "MDLP Med Provider Handler";
  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(TransformRecord record)
    throws Exception {

    final DataContext inputContext = record.getInputContext();

    final List<ScoredDataContext> results = new ArrayList<>();
    // ONLY handle NAME and TEXT
    if (inputContext.getType() != DataContextType.NAME
        && inputContext.getType() != DataContextType.TEXT) {
      return results;
    }

    final ContentService service = new ContentServiceJpa();

    try {

      // Handle procedures

      // Bail if we find any reason to believe this is NOT a procedure.
      for (final ScoredResult result : record.getNormalizedResults()) {
        final String value = result.getValue().toLowerCase();

        // RULES about words
        // TODO Add non-med rules here
        if (false) {
          Logger.getLogger(getClass()).debug("  matched NON-MED pattern");
          return results;
        }

        // RULE about procedure words
        if (!hasMedicationWords(service, value)) {
          return results;
        }

        if (value.length() > 150) {
          Logger.getLogger(getClass())
              .debug("  matched MULTIPLE (length) procedure");
        }
      }

      results.add(new ScoredDataContextJpa(inputContext));
      return results;

    } catch (Exception e) {
      throw e;
    } finally {
      service.close();
    }

  }

  /**
   * Process.
   *
   * @param record the record
   * @return the list
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public List<ScoredResult> process(TransformRecord record) throws Exception {
    Logger.getLogger(getClass())
        .debug("  process - " + record.getInputString());

    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    final DataContext outputContext = record.getProviderOutputContext();

    // Validate input/output context
    validate(inputContext, outputContext);

    // Set up return value
    final List<ScoredResult> results = new ArrayList<ScoredResult>();

    // TODO

    return results;
  }

  /* see superclass */
  @Override
  public void addFeedback(String inputString, DataContext context,
    String feedbackString, DataContext outputContext) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public void removeFeedback(String inputString, DataContext context,
    DataContext outputContext) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public float getLogBaseValue() {
    return 0;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public boolean isPreCheckValid(TransformRecord record) throws Exception {
    // n/a
    return true;
  }

  /* see superclass */
  @Override
  public Map<String, Float> filterResults(
    Map<String, Float> providerEvidenceMap, TransformRecord record)
    throws Exception {
    return providerEvidenceMap;
  }

  /**
   * Checks for procedure words.
   *
   * @param service the service
   * @param value the value
   * @return true, if successful
   * @throws Exception the exception
   */
  private boolean hasMedicationWords(ContentService service, String value)
    throws Exception {
    final String[] words = value.split(" ");
    for (final String word : words) {
      if (word.isEmpty()) {
        continue;
      }

      if (service
          .findConcepts("HKFT-MED", "latest", Branch.ROOT,
              "atoms.name:\"" + word + "\"", null)
          .size() > 0) {
        Logger.getLogger(getClass()).debug("  med word = " + word);
        return true;
      }

    }
    return false;
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {
    // n/a
  }
}
