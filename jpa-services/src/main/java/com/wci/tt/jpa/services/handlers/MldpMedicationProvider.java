/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.StopAnalyzer;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredDataContextJpa;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.jpa.infomodels.MedicationOutputModel;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.content.ConceptList;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Concept;
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
    inputMatcher.configureContext(DataContextType.NAME, null, null, null, null,
        null, null);

    DataContextMatcher outputMatcher = new DataContextMatcher();
    outputMatcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        MedicationOutputModel.class.getName(), null, null);
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

      // Bail if we find any reason to believe this is NOT a procedure.
      for (final ScoredResult result : record.getNormalizedResults()) {
        final String value = result.getValue().toLowerCase();

        // RULES about words
        // TODO Add non-med rules here
        // if (false) {
        // Logger.getLogger(getClass()).debug(" matched NON-MED pattern");
        // return results;
        // }

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
    // TODO Create medication output context
    final DataContext outputContext = record.getProviderOutputContext();

    // Validate input/output context
    validate(inputContext, outputContext);

    // Set up return value
    final List<ScoredResult> results = new ArrayList<ScoredResult>();

    // do something with the string "A/B C.D E F-G"

    // TODO Determine type

    // TODO Put the MedicationOutputModel (extends infomodel) into the value of
    // ScoredResult
    MedicationOutputModel model = new MedicationOutputModel();
    model.setInputString(inputString);
    model.setNormalizedString(record.getNormalizedResults().get(0).getValue());

    // Construct all subsequences
    List<String> subsequences = new ArrayList<>();

    String[] splitTerms = inputString.split(" ");
    System.out.println("split terms: ");

    for (int i = 0; i < splitTerms.length; i++) {
      System.out.println("  " + splitTerms[i]);
    }

    for (int i = 0; i < splitTerms.length; i++) {
      for (int j = i; j < splitTerms.length; j++) {
        String subsequence = "";
        for (int k = i; k <= j; k++) {
          subsequence += splitTerms[k] + " ";
        }
        // skip duplicates
        if (!subsequences.contains(subsequence.trim())) {
          subsequences.add(subsequence.trim());
        }
      }
    }

    // sort by decreasing length
    subsequences.sort(new Comparator<String>() {
      @Override
      public int compare(String u1, String u2) {
        return u2.length() - u1.length();
      }
    });

    System.out.println("subsequences");
    for (String s : subsequences) {
      System.out.println("  " + s);
    }

    // Ordered removal
    String[] orderedFeatures = {
        "BrandName", "Ingredient", "Strength", "DoseForm", "DoseFormQualifier",
        "Route"
    };

    final ContentService service = new ContentServiceJpa();
    String remainingString = inputString;
    // cycle over subsequences
    for (final String subsequence : subsequences) {

      System.out.println("*** CHECKING SUBSEQUENCE: " + subsequence);
      boolean matched = false;

      // if subsequence no longer appears, skip
      if (!remainingString.contains(subsequence)) {
        System.out.println("subsequence no longer in string: " + subsequence
            + " | " + remainingString);
        continue;
      }

      // cycle over ordered featuers
      for (final String feature : orderedFeatures) {
        if (matched) {
          break;
        }

        final ConceptList matches =
            service.findConcepts(inputContext.getTerminology(),
                inputContext.getVersion(), Branch.ROOT,
                "semanticTypes.semanticType:" + feature
                    + " AND atoms.nameNorm:\""
                    + ConfigUtility.normalize(subsequence) + "\"",
                null);
        for (final Concept match : matches.getObjects()) {
          System.out
              .println("Potential match found on concept " + match.getName());
          for (final Atom atom : match.getAtoms()) {
            System.out.println("  Checking: " + atom.getName());
            if (atom.getName().toLowerCase()
                .equals(subsequence.toLowerCase())) {
              System.out
                  .println("    Match found for subsequence " + subsequence);
              model.getRemovedTerms().add(subsequence);
              remainingString = remainingString.replaceAll(subsequence, "");
              matched = true;
              System.out.println("  NEW REMAINING: " + remainingString);
            }
          }
        }
      }

    }

    // stop words
    final List<String> tokens = Arrays.asList(remainingString.split("\\s"));
    for (String token : remainingString.split("\\s")) {
      if (StopAnalyzer.ENGLISH_STOP_WORDS_SET.contains(token)) {
        tokens.remove(token);
      }
    }
    String remaining = "";
    for (String token : tokens) {
      remaining += token + " ";
    }

    model.setRemainingString(remaining.trim());

    ScoredResult result = new ScoredResultJpa(model.getModelValue(), 1.0f);
    results.add(result);

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

      if (service.findConcepts("HKFT-MED", "latest", Branch.ROOT,
          "atoms.name:\"" + word + "\"", null).size() > 0) {
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
