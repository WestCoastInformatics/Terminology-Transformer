/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredDataContextJpa;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.jpa.helpers.ValueRawModel;
import com.wci.tt.jpa.infomodels.IngredientModel;
import com.wci.tt.jpa.infomodels.MedicationModel;
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

  final String enclosingPunctuationPatternStr = "[\\[\\{\\(](.+)[\\]\\}\\)]";

  final String stopwordPattern =
      ".&\\b(a|an|and|are|as|at|be|but|by|for|if|in|into|is|it|no|not|of|on|or|such|that|the|their|then|there|these|they|this|to|was|will|with)\\b.*";

  final Pattern enclosingPunctuationPattern =
      Pattern.compile(enclosingPunctuationPatternStr);

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
      if (record.getNormalizedResults().size() == 0) {
        final ScoredDataContext context = new ScoredDataContextJpa();
        context.setSpecialty("Garbage");
        results.add(context);
      }

      for (final ScoredResult result : record.getNormalizedResults()) {
        final String value = result.getValue().toLowerCase();
        final ScoredDataContext outputContext = new ScoredDataContextJpa();

        // TODO Decide length constraint
        if (value.split("\\w+").length > 15) {
          outputContext.setSpecialty("Long");
        }

        // RULES about words
        // TODO Add non-med rules here
        // if (false) {
        // Logger.getLogger(getClass()).debug(" matched NON-MED pattern");
        // return results;
        // }

        // RULE about immunization words
        if (hasImmunizationWords(service, value)) {
          outputContext.setSpecialty("Immunization");
        }

        // RULE about medication words
        else if (hasMedicationWords(service, value)) {
          outputContext.setSpecialty("Medication");
        }

        // OTHERWISE consider garbage
        else {
          outputContext.setSpecialty("Garbage");
        }

        results.add(outputContext);

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
    // System.out.println(" process - " + record.getInputString());

    Long startTime = System.currentTimeMillis();

    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    // TODO Create medication output context
    final DataContext outputContext = record.getProviderOutputContext();

    // Validate input/output context
    validate(inputContext, outputContext);

    // Set up return value
    final List<ScoredResult> results = new ArrayList<ScoredResult>();

    // identify
    List<ScoredDataContext> idContext = identify(record);

    // TODO Put the MedicationOutputModel (extends infomodel) into the value of
    // ScoredResult
    MedicationOutputModel outputModel = new MedicationOutputModel();
    MedicationModel medModel = new MedicationModel();
    outputModel.setInputString(inputString);

    final ContentService service = new ContentServiceJpa();

    try {
      outputModel
          .setNormalizedString(record.getNormalizedResults().get(0).getValue());
      outputModel.setType(idContext.get(0).getSpecialty());

      // if not medication, do not process
      if (!outputModel.getType().equals("Medication")) {
        outputModel.setRemainingString(inputString);
      }

      // otherwise continue
      else {

        // Construct all subsequences
        List<String> subsequences = new ArrayList<>();

        String[] splitTerms = inputString.split(" ");
        // System.out.println("split terms: ");

        // for (int i = 0; i < splitTerms.length; i++) {
        // System.out.println(" " + splitTerms[i]);
        // }

        for (int i = 0; i < splitTerms.length; i++) {
          for (int j = i; j < Math.min(i + 7, splitTerms.length); j++) {
            String subsequence = "";
            for (int k = i; k <= j; k++) {
              subsequence += splitTerms[k] + " ";
            }
            // skip duplicates
            if (!subsequences.contains(subsequence.trim())) {
              subsequences.add(subsequence.trim());
            }

            // if enclosed in punctuation, add enclosed term (wil be checked
            // after
            // full term)
            Matcher matcher = enclosingPunctuationPattern.matcher(subsequence);
            if (matcher.find()) {
              subsequences.add(matcher.group(1).trim());
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

        // System.out.println("subsequences");
        // for (String s : subsequences) {
        // System.out.println(" " + s);
        // }

        // Ordered removal
        String[] orderedFeatures = {
            "BrandName", "Ingredient", "Strength", "DoseForm",
            "DoseFormQualifier", "Route", "ReleasePeriod"
        };

        final Set<Atom> ingredients = new HashSet<>();
        final Set<Atom> strengths = new HashSet<>();

        String remainingString = inputString;
        // cycle over subsequences
        for (final String subsequence : subsequences) {

          boolean matched = false;

          // check if empty string or subsequence no longer exists
          if (normalizeString(remainingString).isEmpty()) {
            break;
          }
          if (!remainingString.contains(subsequence)) {
            continue;
          }

          final ConceptList matches = service.findConcepts(
              inputContext.getTerminology(), inputContext.getVersion(),
              Branch.ROOT,
              "atoms.nameNorm:\"" + ConfigUtility.normalize(subsequence) + "\"",
              null);

          // if matches found
          if (matches.getTotalCount() > 0) {

            // cycle over ordered featuers
            for (final String feature : orderedFeatures) {
              if (matched) {
                break;
              }

              // cycle over potential matches
              for (final Concept match : matches.getObjects()) {
                if (matched) {
                  break;
                }

                // if match is in current semantic type
                if (match.getSemanticTypes().get(0).getSemanticType()
                    .equals(feature)) {

                  // cycle over atoms to find match
                  for (final Atom atom : match.getAtoms()) {
                    if (matched) {

                      break;
                    }

                    // NOTE: case insensitive comparison
                    if (atom.getName().toLowerCase()
                        .equals(subsequence.toLowerCase())) {

                      // process removed and remaining terms
                      outputModel.getRemovedTerms().add(subsequence);
                      remainingString = remainingString
                          .replaceAll("\\b" + subsequence + "\\b", "");
                      matched = true;

                      // add to the medication model based on feature
                      switch (feature) {
                        case "BrandName":
                          medModel.setBrandName(new ValueRawModel(
                              atom.getTerminologyId(), atom.getName()));
                          break;
                        case "Ingredient":
                          ingredients.add(atom);
                          break;
                        case "Strength":
                          strengths.add(atom);
                          break;

                        case "DoseForm":
                          medModel.setDoseForm(new ValueRawModel(
                              atom.getTerminologyId(), atom.getName()));
                          break;
                        case "DoseFormQualifier":
                          medModel.setDoseFormQualifier(new ValueRawModel(
                              atom.getTerminologyId(), atom.getName()));
                          break;
                        case "Route":
                          medModel.setRoute(new ValueRawModel(
                              atom.getTerminologyId(), atom.getName()));
                          break;
                        case "ReleasePeriod":
                          medModel.setReleasePeriod(new ValueRawModel(
                              atom.getTerminologyId(), atom.getName()));
                          break;
                        default:
                          // do nothing
                      }
                    }
                  }
                }
              }
            }
          }

        }

        if (ingredients.size() > 0) {

          // match ingredients and strengths
          final HashMap<Integer, Atom> ingrPos = new HashMap<>();
          final HashMap<Integer, Atom> strPos = new HashMap<>();

          for (final Atom atom : ingredients) {
            int pos =
                inputString.toLowerCase().indexOf(atom.getName().toLowerCase());
            while (pos >= 0) {

              ingrPos.put(pos, atom);
              pos = inputString.indexOf(atom.getName(), pos + 1);
            }
          }

          for (final Atom atom : strengths) {
            int pos =
                inputString.toLowerCase().indexOf(atom.getName().toLowerCase());
            while (pos >= 0) {

              strPos.put(pos, atom);
              pos = inputString.toLowerCase()
                  .indexOf(atom.getName().toLowerCase(), pos + 1);

            }
          }

          final List<Integer> orderedIngrPos =
              new ArrayList<>(ingrPos.keySet());
          Collections.sort(orderedIngrPos);
          final List<Integer> orderedStrPos = new ArrayList<>(strPos.keySet());
          Collections.sort(orderedStrPos);

          for (int i = 0; i < orderedIngrPos.size(); i++) {
            final IngredientModel ingrModel = new IngredientModel();

            final ValueRawModel ingr = new ValueRawModel();
            ingr.setRaw(ingrPos.get(orderedIngrPos.get(i)).getTerminologyId());
            ingr.setValue(ingrPos.get(orderedIngrPos.get(i)).getName());
            final ValueRawModel str = new ValueRawModel();
            if (orderedStrPos.size() > i) {
              str.setRaw(strPos.get(orderedStrPos.get(i)).getTerminologyId());
              str.setValue(strPos.get(orderedStrPos.get(i)).getName());
            } else {
              str.setValue("NO STRENGTH FOUND");
            }
            ingrModel.setIngredient(ingr);
            ingrModel.setStrength(str);
            medModel.getIngredients().add(ingrModel);
          }

          for (int pos = 0; pos < ingrPos.keySet().size(); pos++) {

          }

        }

        outputModel.setRemainingString(remainingString.trim());
        outputModel
            .setNormalizedRemainingString(normalizeString(remainingString));

        if (!outputModel.getNormalizedRemainingString().isEmpty()) {
          System.out.println("inputString: " + inputString);
          System.out
              .println("  remaining: " + outputModel.getRemainingString());

        }
      }
    } catch (

    Exception e) {
      outputModel.setType("Process Error");
    } finally {

      service.close();
    }

    // if (!outputModel.getRemainingString().isEmpty()) {
    // System.out
    // .println(" final remaining - " + outputModel.getRemainingString());
    // }

    // System.out.println(medModel.getModelValue());

    // set the constructed medication model
    outputModel.setModel(medModel);

    ScoredResult result = new ScoredResultJpa();
    result.setValue(outputModel.getModelValue());

    // NOTE: See MldpServiceJpa.processTerms for score values
    switch (outputModel.getType()) {
      case "Medication":

        if (outputModel.getNormalizedRemainingString().isEmpty()) {
          // represents PUBLISHED -- complete coverage
          result.setScore(1.0f);
        } else {
          // represents NEEDS_REVIEW -- incomplete coverage
          result.setScore(0.0f);
        }

        break;
      case "Process Error":
        // NOTE: Value arbitrary
        result.setScore(0.25f);
        break;
      default:
        // represents REVIEW_DONE -- excluded terms
        result.setScore(0.5f);

    }

    results.add(result);

    // System.out.println((System.currentTimeMillis() - startTime) + " ms");

    return results;
  }

  private String normalizeString(String string) {

    return string.replaceAll(stopwordPattern, "")
        .replaceAll(ConfigUtility.PUNCTUATION_REGEX, "").trim();
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

  private final String immunizationPattern = "(tdap|vaccine|measles|influenza)";

  private boolean hasImmunizationWords(ContentService service, String value)
    throws Exception {

    if (value.toLowerCase().matches(".*\\b" + immunizationPattern + "\\b.*")) {
      return true;
    }

    return false;
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {
    // n/a
  }
}
