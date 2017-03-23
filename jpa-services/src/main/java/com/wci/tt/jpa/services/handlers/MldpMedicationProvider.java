/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;

/**
 * MLDP provider for mapping raw terms to procedures.
 */
public class MldpMedicationProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  /** Package pattern */
  private final static Pattern pkPattern =
      Pattern.compile("\\{.*\\}.*pack.*", Pattern.CASE_INSENSITIVE);

  /** Brand name pattern */
  private final static Pattern bnPattern = Pattern.compile("\\[(.*)\\]");

  /** List of stopwords */
  private final static String stopwordPattern =
      "\\b(a|an|and|are|as|at|be|but|by|for|if|in|into|is|it|no|not|of|on|or|such|that|the|their|then|there|these|they|this|to|was|will|with)\\b";

  private final static HashMap<String, Concept> atomToConceptMap =
      new HashMap<>();

  // TODO This is not thread-safe, but fine for single user
  private boolean cacheMode = false;

  private boolean debugMode = false;

  private final static int defaultPhraseLength = 25;

  private final static int defaultLongTermWordThreshold = 50;

  private static int longTermWordThreshold = -1;

  private static int maxPhraseLength = -1;

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

  private void setParameters(DataContext inputContext) throws Exception {
    // check parameter: cache mode
    // NOTE: Once cached, cache is static until a process request disables it
    // via cacheMode parameter false or absent
    if (inputContext.getParameters().containsKey("cacheMode")
        && inputContext.getParameters().get("cacheMode").equals("true")) {
      if (!cacheMode) {
        enableCacheMode(inputContext);
      }
    } else {
      disableCacheMode();
    }

    // check parameter: phrase length
    if (inputContext.getParameters().containsKey("longTermWordThreshold")
        && inputContext.getParameters().get("longTermWordThreshold") != null) {
      try {
        longTermWordThreshold = Integer
            .valueOf(inputContext.getParameters().get("longTermWordThreshold"));
      } catch (Exception e) {
        throw new Exception("Parameter phraseLength not a valid integer");
      }
    } else {
      longTermWordThreshold = defaultLongTermWordThreshold;
    }

    // check parameter: long term word count length threshold
    if (inputContext.getParameters().containsKey("phraseLength")
        && inputContext.getParameters().get("phraseLength") != null) {
      try {
        maxPhraseLength =
            Integer.valueOf(inputContext.getParameters().get("phraseLength"));
      } catch (Exception e) {
        throw new Exception(
            "Parameter longTermWordThreshold not a valid integer");
      }
    } else {
      maxPhraseLength = defaultPhraseLength;
    }
  }

  public void disableCacheMode() throws Exception {

    atomToConceptMap.clear();
    cacheMode = false;
  }

  public void enableCacheMode(DataContext inputContext) throws Exception {

    atomToConceptMap.clear();

    final ContentService service = new ContentServiceJpa();

    // get all concepts for input context
    final ConceptList concepts =
        service.findConcepts(inputContext.getTerminology(),
            inputContext.getVersion(), Branch.ROOT, null, null);

    Logger.getLogger(getClass())
        .info("Caching atoms for " + concepts.getTotalCount() + " concepts");

    for (final Concept concept : concepts.getObjects()) {
      if (concept.getSemanticTypes().size() == 1) {

        // shallow-copy with semantic types
        final Concept c = new ConceptJpa(concept, false);
        c.setSemanticTypes(concept.getSemanticTypes());

        for (final Atom atom : concept.getAtoms()) {
          atomToConceptMap.put(atom.getName().toLowerCase(), c);
        }
      } else {
        // NOTE: Warning here primarily for debug, not intended for full
        // validation
        Logger.getLogger(getClass())
            .info("WARNING: Expected one feature on concept "
                + concept.getTerminologyId() + " but found "
                + concept.getSemanticTypes().size());
      }
    }
    cacheMode = true;
    service.close();
    Logger.getLogger(getClass()).info("  done");
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

      // if no normalized results, consider garbage
      if (record.getNormalizedResults().size() == 0) {
        final ScoredDataContext context = new ScoredDataContextJpa();
        context.setSpecialty("Garbage");
        results.add(context);
      }

      for (final ScoredResult result : record.getNormalizedResults()) {
        final String value = result.getValue().toLowerCase();
        final ScoredDataContext outputContext = new ScoredDataContextJpa();

        // RULE about packages
        if (pkPattern.matcher(value).find()) {
          outputContext.setSpecialty("Package");
        }

        // RULE about immunizations
        else if (hasImmunizationWords(service, value)) {
          outputContext.setSpecialty("Immunization");
        }

        // RULE about allergy words
        else if (hasAllergyWords(service, value)) {
          outputContext.setSpecialty("Allergy");
        }

        // RULE about medication words
        else if (hasMedicationWords(service, value)) {
          outputContext.setSpecialty("Medication");
        }

        else if (value.split("\\w+").length > longTermWordThreshold) {
          outputContext.setSpecialty("Long");
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

  private List<String> getSubsequences(String string) {
    final List<String> subsequences = new ArrayList<>();
    String inputString = string;

    // brand names check: not limited by phrase length
    Matcher matcher = bnPattern.matcher(inputString);
    while (matcher.find()) {
      if (debugMode) {
        System.out.println("Found potential brand name: " + matcher.group(1));
      }
      // add the entire sequence surrounded by brackets
      subsequences.add(matcher.group(1));
    }

    String[] splitTerms = inputString.split(" ");

    for (int i = 0; i < splitTerms.length; i++) {
      for (int j = i; j < Math.min(i + maxPhraseLength,
          splitTerms.length); j++) {
        String subsequence = "";
        for (int k = i; k <= j; k++) {
          subsequence += splitTerms[k] + " ";
        }
        subsequence = subsequence.trim();
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

    return subsequences;
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

    debugMode = "true".equals(System.getProperty("mldpDebug"));

    if (debugMode) {
      System.out.println("** DEBUG MODE **");
    }

    Long startTime = System.currentTimeMillis();

    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    final DataContext outputContext = record.getProviderOutputContext();
    outputContext.setInfoModelClass(MedicationOutputModel.class.getName());

    // Validate input/output context
    validate(inputContext, outputContext);

    // set parameters from input context
    setParameters(inputContext);

    if (!cacheMode) {
      System.out.println("************************* Not in cache mode");
    }

    if (maxPhraseLength == -1) {
      throw new Exception("Maximum phrase length has no default value");
    }
    if (longTermWordThreshold == -1) {
      throw new Exception("Long term word threshold has no default value");
    }

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
        List<String> subsequences = getSubsequences(inputString);

        if (debugMode) {
          System.out
              .println("Checking " + subsequences.size() + " subsequences");
        }

        // Ordered removal
        // NOTE: Brand Name MUST be first
        // NOTE: DoseForm before DoseFormQualifier
        // NOTE: Strength before Ingredient due to e.g.
        // Ingredient: dimethicone 200
        // Strength : 200 MG/ML
        // Raw Term : dimethicone 200 MG/ML
        String[] orderedFeatures = {
            "BrandName", "Strength", "Ingredient", "DoseForm",
            "DoseFormQualifier", "Route", "ReleasePeriod"
        };

        // save ingredients and strengths along route
        // KEY: the matched term (from atom match)
        // VALUE: The concept containing atom
        final Map<String, Concept> ingredients = new HashMap<>();
        final Map<String, Concept> strengths = new HashMap<>();

        // NOTE: Must be surrounded with buffer space, see replaceAll below
        String remainingString = " " + inputString + " ";

        // check against features in priority order
        for (final String feature : orderedFeatures) {

          // check if empty string or subsequence no longer exists
          if (normalizeString(remainingString).isEmpty()) {
            break;
          }

          for (final String subsequence : subsequences) {

            if (!remainingString.contains(subsequence)) {
              continue;
            }

            Concept matchConcept = null;

            if (cacheMode) {
              final Concept concept =
                  atomToConceptMap.get(subsequence.toLowerCase());
              if (concept != null) {

                if (feature.equals(
                    concept.getSemanticTypes().get(0).getSemanticType())) {
                  if (debugMode) {
                    System.out.println(" subsequence matched in feature: "
                        + feature + ": " + subsequence);
                  }
                  matchConcept = concept;
                }
              }

            } else {

              // TODO Slight optimization opportunity
              // Subsequences searched per feature, skip if no previous results
              // Not priority since this block only runs for on-demand process
              // Cache mode is used for bulk operations

              final ConceptList concepts =
                  service
                      .findConcepts(inputContext.getTerminology(),
                          inputContext.getVersion(), Branch.ROOT,
                          "atoms.nameNorm:\""
                              + ConfigUtility.normalize(subsequence) + "\"",
                          null);

              // if matches found
              if (concepts.getTotalCount() > 0) {
                // cycle over potential matches
                for (final Concept concept : concepts.getObjects()) {
                  if (matchConcept != null) {
                    // break concept loop
                    break;
                  }

                  // if match is in current semantic type
                  if (concept.getSemanticTypes().get(0).getSemanticType()
                      .equals(feature)) {

                    // cycle over atoms to find match
                    for (final Atom atom : concept.getAtoms()) {

                      // NOTE: case insensitive comparison
                      if (atom.getName().toLowerCase()
                          .equals(subsequence.toLowerCase())) {
                        if (debugMode) {
                          System.out.println(" subsequence matched in feature: "
                              + feature + ": " + subsequence);
                        }
                        matchConcept = concept;
                        // break concept loop
                        break;
                      }
                    }
                  }
                }

              }
            }

            // if concept matched by either method, add to model and update term
            if (matchConcept != null) {

              // process removed and remaining terms
              outputModel.getRemovedTerms().add(subsequence);

              // NOTE: Match either space-surrounded or bracket-surrounded
              // (to catch fully enclosed terms in brackets)
              // TODO Consider expanding to parentheses/braces
              final String pattern =
                  "[\\s\\[]" + Pattern.quote(subsequence) + "[\\s\\]]";

              if (debugMode) {
                System.out.println("pattern  : " + pattern);
                System.out.println("remaining: " + remainingString);
              }
              remainingString = remainingString.replaceAll(pattern, " ");

              // add to the medication model based on feature
              switch (matchConcept.getSemanticTypes().get(0)
                  .getSemanticType()) {
                case "BrandName":
                  medModel.setBrandName(new ValueRawModel(
                      matchConcept.getTerminologyId(), matchConcept.getName()));
                  break;
                // NOTE: Ingredients and strengths are combined below
                case "Ingredient":
                  ingredients.put(subsequence, matchConcept);
                  break;
                case "Strength":
                  strengths.put(subsequence, matchConcept);
                  break;
                case "DoseForm":
                  medModel.setDoseForm(new ValueRawModel(
                      matchConcept.getTerminologyId(), matchConcept.getName()));
                  break;
                case "DoseFormQualifier":
                  medModel.setDoseFormQualifier(new ValueRawModel(
                      matchConcept.getTerminologyId(), matchConcept.getName()));
                  break;
                case "Route":
                  medModel.setRoute(new ValueRawModel(
                      matchConcept.getTerminologyId(), matchConcept.getName()));
                  break;
                case "ReleasePeriod":
                  medModel.setReleasePeriod(new ValueRawModel(
                      matchConcept.getTerminologyId(), matchConcept.getName()));
                  break;
                default:
                  // do nothing

              }
            }
          }
        }

        // after all phrases/features checked, match ingredients and strengths
        if (ingredients.size() > 0) {

          // compute initial match
          // mismatch will still attempt to match strength where available
          boolean ingrStrMatch = true;

          if (debugMode) {
            System.out.println("Checking ingredients and strengths");
            System.out.println(inputString);
            System.out.println("ingrs : " + ingredients);
            System.out.println("strs  : " + strengths);
          }

          // match ingredients and strengths (naive form for now)
          // always expect sequence "Ingredient Strength"
          // e.g. Alanine 5.7 MG/ML / Arginine 3.16 MG/ML
          // NOTE: Ratios/concentrations not yet handled, see TODO above

          // check ingredients against strengths and construct
          for (final String ingredient : ingredients.keySet()) {
            final IngredientModel ingrModel = new IngredientModel();
            ingrModel.setIngredient(new ValueRawModel(ingredient,
                ingredients.get(ingredient).getTerminologyId()));
            for (final String strength : strengths.keySet()) {
              final String pattern = ".*\\b"
                  + Pattern.quote(ingredient + " " + strength) + "\\b.*";
              if (debugMode) {
                System.out.println("  Checking: " + pattern);
              }
              if (inputString.matches(pattern)) {
                if (debugMode) {
                  System.out.println("    Found ingr/str match: " + ingredient
                      + " " + strength);
                }
                ingrModel.setStrength(new ValueRawModel(strength,
                    strengths.get(strength).getTerminologyId()));

              }
            }
            if (ingrModel.getStrength() == null) {
              ingrStrMatch = false;
            }
            if (inputString.contains("Plerixafor")) {
              System.out.println("  test case: " + inputString);
              System.out
                  .println("    test ingredient: " + ingrModel.toString());
            }
            medModel.getIngredients().add(ingrModel);
          }

        }

        outputModel.setRemainingString(remainingString.trim());
        outputModel
            .setNormalizedRemainingString(normalizeString(remainingString));

      }

    }

    catch (Exception e) {
      if (debugMode) {
        e.printStackTrace();
      }
      outputModel.setType("Process Error");
    } finally {
      service.close();
    }

    if (debugMode) {
      System.out.println("remaining str: " + outputModel.getRemainingString());
      System.out.println(
          "norm rem str : " + outputModel.getNormalizedRemainingString());
    }

    // set the constructed medication model
    outputModel.setModel(medModel);

    ScoredResult result = new ScoredResultJpa();
    // NOTE: Removed for speed, construction of value happens in MldpServiceJpa
    // result.setValue(outputModel.getModelValue());
    result.setValue(outputModel.getModelValue());
    result.setModel(outputModel);

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
      case "Medication - Modeling Required":
        if (outputModel.getNormalizedRemainingString().isEmpty()) {
          result.setScore(0.75f);
        } else {
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

    if (debugMode) {
      System.out.println(
          "Time for term: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    return results;
  }

  private String normalizeString(String string) {

    final String normalizedString =
        string.toLowerCase().replaceAll(stopwordPattern, "")
            .replaceAll(ConfigUtility.PUNCTUATION_REGEX, "").trim();
    return normalizedString;
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

  private final String allergyPattern = "(antigen|ragweed)";

  private boolean hasAllergyWords(ContentService service, String value)
    throws Exception {

    if (value.toLowerCase().matches(".*\\b" + allergyPattern + "(\\b|\\B).*")) {
      return true;
    }
    return false;
  }

  private final String immunizationPattern =
      "(tdap|vaccine|measles|influenza|meningitidis)";

  private boolean hasImmunizationWords(ContentService service, String value)
    throws Exception {

    if (value.toLowerCase()
        .matches(".*\\b" + immunizationPattern + "(\\b|\\B).*")) {
      return true;
    }

    return false;
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {
    // n/a
  }

}
