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
import com.wci.tt.jpa.infomodels.ConditionModel;
import com.wci.tt.jpa.infomodels.SiteModel;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.services.ContentService;

/**
 * MLDP provider for mapping raw terms to conditions.
 */
public class MldpConditionProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  /**
   * Instantiates an empty {@link MldpConditionProvider}.
   *
   * @throws Exception the exception
   */
  public MldpConditionProvider() throws Exception {

    // Configure input/output matchers
    DataContextMatcher inputMatcher = new DataContextMatcher();
    inputMatcher.configureContext(DataContextType.NAME, null, "Condition", null,
        null, null, null);
    DataContextMatcher outputMatcher = new DataContextMatcher();
    outputMatcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        ConditionModel.class.getName(), null, null);
    addMatcher(inputMatcher, outputMatcher);

  }

  /* see superclass */
  @Override
  public String getName() {
    return "MDLP Condition Provider Handler";
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

      // Bail if we find any reason to believe this is NOT a procedure.
      for (final ScoredResult result : record.getNormalizedResults()) {
        final String value = result.getValue().toLowerCase();

        // RULES about words
        if (value.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*")
            || value.matches("\\d{4}") || value.endsWith(" abnormal")
            || value.endsWith(" carrier") || value.endsWith(" counseling")
            || value.endsWith(" encounter") || value.endsWith(" exam")
            || value.endsWith(" examination") || value.endsWith(" glucose")
            || value.endsWith(" monitoring") || value.endsWith(" screening")
            || value.endsWith(" state") || value.endsWith(" status")
            || value.contains("discussed with") || value.contains("follow up")
            || value.startsWith("increased frequency ")
            || value.startsWith("decreased frequency ")
            || value.startsWith("use of ") || value.startsWith("s/p ")
            || value.startsWith("counseling ") || value.startsWith("elevated ")
            || value.startsWith("encounter ") || value.startsWith("monitoring ")
            || value.startsWith("need ") || value.startsWith("counseling ")
            || value.contains("electrocardiogram ")
            || value.contains(" monitoring ")) {
          Logger.getLogger(getClass()).debug("  matched NON-CONDITION pattern");
          return results;
        }

        if (value.matches(".* with .* without .*")
            || value.matches(".* with .* and .*")
            || value.contains("complicating") || value.contains("due to")
            || value.contains(" with ") || value.contains(" without ")
            || value.endsWith(" remission")) {
          Logger.getLogger(getClass()).debug("  matched MULTIPLE pattern");
          return results;
        }

        // RULE about condition words
        else if (!hasConditionWords(service, value)) {
          return results;
        }
        if (value.length() > 150) {
          Logger.getLogger(getClass())
              .debug("  matched MULTIPLE (length) condition");
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

    // conditions
    final ConditionModel model = new ConditionModel();

    // extract site info
    final SiteModel site = new SiteModel();

    return results;
  }

  /* see superclass */
  @Override
  public void addFeedback(String inputString, DataContext context,
    String feedbackString, DataContext outputContext) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeFeedback(String inputString, DataContext context,
    DataContext outputContext) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public float getLogBaseValue() {
    return 0;
  }

  @Override
  public void close() throws Exception {
    // n/a
  }

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
   * Checks for condition words.
   *
   * @param service the service
   * @param value the value
   * @return true, if successful
   * @throws Exception the exception
   */
  private boolean hasConditionWords(ContentService service, String value)
    throws Exception {
    final String[] words = value.split(" ");
    for (final String word : words) {
      if (word.isEmpty()) {
        continue;
      }

      if (service
          .findConcepts("HKT", "latest", Branch.ROOT,
              "semanticTypes:condition AND atoms.name:\"" + word + "\"", null)
          .size() > 0) {
        Logger.getLogger(getClass()).debug("  condition word = " + word);
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
