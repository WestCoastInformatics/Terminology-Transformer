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
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.content.ConceptList;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;

/**
 * MLDP provider for mapping raw terms to procedures.
 */
public class MldpMedicationProvider extends AbstractAcceptsHandler
    implements ProviderHandler {

  private final static String bnPatternStr = "\\[(.*)\\]";

  private final static String stopwordPattern =
      ".&\\b(a|an|and|are|as|at|be|but|by|for|if|in|into|is|it|no|not|of|on|or|such|that|the|their|then|there|these|they|this|to|was|will|with)\\b.*";

  private final static Pattern bnPattern = Pattern.compile(bnPatternStr);

  final static HashMap<String, Concept> atomToConceptMap = new HashMap<>();

  private static boolean cacheMode = false;

  private boolean debugMode = false;
  
  private static int phraseLength = 7;

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

  public void disableCacheMode() throws Exception {
    atomToConceptMap.clear();
    cacheMode = false;
  }

  public void enableCacheMode(DataContext inputContext) throws Exception {
    if (debugMode) {
      System.out.println("enableCacheMode - current cacheMode: " + cacheMode);
    }
    atomToConceptMap.clear();

    PfsParameter tempPfs = new PfsParameterJpa();
    // tempPfs.setMaxResults(10);
    // tempPfs.setStartIndex(0);
    final ContentService service = new ContentServiceJpa();
    final ConceptList concepts =
        service.findConcepts(inputContext.getTerminology(),
            inputContext.getVersion(), Branch.ROOT, null, tempPfs);

    Logger.getLogger(getClass())
        .info("Caching atoms for " + concepts.getTotalCount() + " concepts");

    for (final Concept concept : concepts.getObjects()) {
      if (concept.getSemanticTypes().size() == 1) {

        // shallow-copy with semantic types
        final Concept c = new ConceptJpa(concept, false);
        c.setSemanticTypes(concept.getSemanticTypes());

        for (final Atom atom : concept.getAtoms()) {
          atomToConceptMap.put(atom.getName().toLowerCase(), c);
          // if
          // (atom.getName().toLowerCase().matches(".*(vincamine|oral|capsule).*"))
          // {
          // System.out.println("caching atom: " + atom.getName());
          // }
        }
      } else {
        Logger.getLogger(getClass())
            .info("WARNING: Expected one feature on concept "
                + concept.getTerminologyId() + " but found "
                + concept.getSemanticTypes().size());
      }
    }
    cacheMode = true;
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
      if (record.getNormalizedResults().size() == 0) {
        final ScoredDataContext context = new ScoredDataContextJpa();
        context.setSpecialty("Garbage");
        results.add(context);
      }

      for (final ScoredResult result : record.getNormalizedResults()) {
        final String value = result.getValue().toLowerCase();
        final ScoredDataContext outputContext = new ScoredDataContextJpa();

        // TODO Decide length constraint
        if (value.split("\\w+").length > 25) {
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

  private List<String> getSubsequences(String string) {
    final List<String> subsequences = new ArrayList<>();
    String inputString = string;
    
    
    // brand names check: not limited by phrase length
    Matcher matcher = bnPattern.matcher(inputString);
    while (matcher.find()) {
      if (debugMode) {
        System.out.println("Found potential brand name: " + matcher.group(1));
      }
      subsequences.add(matcher.group(1));
    }
    
    String[] splitTerms = inputString.split(" ");
    if (debugMode) {
      System.out.println("split terms: ");
      for (int i = 0; i < splitTerms.length; i++) {
        System.out.println(" " + splitTerms[i]);
      }
    }
    
    for (int i = 0; i < splitTerms.length; i++) {
      for (int j = i; j < Math.min(i + phraseLength, splitTerms.length); j++) {
        String subsequence = "";
        for (int k = i; k <= j; k++) {
          subsequence += splitTerms[k] + " ";
        }
        subsequence = subsequence.trim();
        if (debugMode) {
          System.out.println("constructed: " + subsequence);
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

    // TODO Remove after dev testing
    debugMode = "true".equals(System.getProperty("mldpdebug"));

    Long startTime = System.currentTimeMillis();

    // TODO Pass as input the type key value itself instead of the string
    // Update based on type

    final String inputString = record.getInputString();
    final DataContext inputContext = record.getInputContext();
    // TODO Create medication output context
    final DataContext outputContext = record.getProviderOutputContext();

    // Validate input/output context
    validate(inputContext, outputContext);

    // System.out.println(inputContext.getParameters());

    // check for cache mode
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

        // Ordered removal
        String[] orderedFeatures = {
            "BrandName", "Ingredient", "Strength", "DoseForm",
            "DoseFormQualifier", "Route", "ReleasePeriod"
        };

        final Set<Concept> ingredients = new HashSet<>();
        final Set<Concept> strengths = new HashSet<>();

        String remainingString = inputString;

        // cycle over subsequences
        for (final String subsequence : subsequences) {
          if (debugMode) {

            System.out.println("subsequence: " + subsequence);
            System.out.println(" remaining: " + remainingString);

          }

          // check if empty string or subsequence no longer exists
          if (normalizeString(remainingString).isEmpty()) {
            break;
          }
          if (!remainingString.contains(subsequence)) {
            continue;
          }

          Concept matchConcept = null;

          // if in cache mode, use cache
          if (cacheMode) {
            // System.out.println(" Cache mode");
            final Concept concept =
                atomToConceptMap.get(subsequence.toLowerCase());
            if (concept != null) {
              if (debugMode) {
                System.out.println(
                    " found concept from atom " + subsequence.toLowerCase());
              }
              // check feature match
              for (String feature : orderedFeatures) {
                if (feature.equals(
                    concept.getSemanticTypes().get(0).getSemanticType())) {
                  if (debugMode) {
                    System.out.println(" in feature: " + feature);
                  }
                  matchConcept = concept;
                  break;
                }
              }
            }
          }

          // if not in cache mode, find all potential matches
          else {

            final ConceptList concepts =
                service.findConcepts(inputContext.getTerminology(),
                    inputContext.getVersion(), Branch.ROOT, "atoms.nameNorm:\""
                        + ConfigUtility.normalize(subsequence) + "\"",
                    null);

            // if matches found
            if (concepts.getTotalCount() > 0) {

              // cycle over ordered featuers
              for (final String feature : orderedFeatures) {
                if (matchConcept != null) {
                  break;
                }

                // cycle over potential matches
                for (final Concept concept : concepts.getObjects()) {
                  if (matchConcept != null) {
                    break;
                  }

                  if (debugMode) {
                    System.out.println(" found concept from atom "
                        + subsequence.toLowerCase());
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
                          System.out.println(" in feature: " + feature);
                        }
                        matchConcept = concept;
                        break;
                      }
                    }
                  }
                }
              }
            }

          }

          if (matchConcept != null) {

            // process removed and remaining terms
            outputModel.getRemovedTerms().add(subsequence);
            if (debugMode) {
              System.out.println("  replacement pattern: \\b("
                  + Pattern.quote(subsequence) + ")\\B*\\b*");
            }
            remainingString = remainingString.replaceAll(
                "\\b(" + Pattern.quote(subsequence) + ")\\B*\\b*", "");

            // add to the medication model based on feature
            switch (matchConcept.getSemanticTypes().get(0).getSemanticType()) {
              case "BrandName":
                medModel.setBrandName(new ValueRawModel(
                    matchConcept.getTerminologyId(), matchConcept.getName()));
                break;
              case "Ingredient":
                ingredients.add(matchConcept);
                break;
              case "Strength":
                strengths.add(matchConcept);
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

        // compute ingredients and strengths
        if (ingredients.size() > 0) {

          // match ingredients and strengths
          final HashMap<Integer, Concept> ingrPos = new HashMap<>();
          final HashMap<Integer, Concept> strPos = new HashMap<>();

          for (final Concept concept : ingredients) {
            int pos = inputString.toLowerCase()
                .indexOf(concept.getName().toLowerCase());
            while (pos >= 0) {
              ingrPos.put(pos, concept);
              pos = inputString.indexOf(concept.getName(), pos + 1);
            }
          }

          for (final Concept concept : strengths) {
            int pos = inputString.toLowerCase()
                .indexOf(concept.getName().toLowerCase());
            while (pos >= 0) {
              strPos.put(pos, concept);
              pos = inputString.toLowerCase()
                  .indexOf(concept.getName().toLowerCase(), pos + 1);
            }
          }

          // TODO Remove this once decision made about e.g. 1 ML ingredient
          // 5MG/ML
          if (ingredients.size() != strengths.size()) {
            if (debugMode) {
              System.out.println("ingr/str mismatch");
            }
            outputModel.setType("Ingr/Str Mismatch");
          }

          if (ingredients.size() > 4
              && inputString.matches("oral|tablet|capsule")) {
            if (debugMode) {
              System.out.println("multivitamin");
            }
            outputModel.setType("Multivitamin");
          } else {

            final List<Integer> orderedIngrPos =
                new ArrayList<>(ingrPos.keySet());
            Collections.sort(orderedIngrPos);
            final List<Integer> orderedStrPos =
                new ArrayList<>(strPos.keySet());
            Collections.sort(orderedStrPos);

            for (int i = 0; i < orderedIngrPos.size(); i++) {
              final IngredientModel ingrModel = new IngredientModel();

              final ValueRawModel ingr = new ValueRawModel();
              ingr.setRaw(
                  ingrPos.get(orderedIngrPos.get(i)).getTerminologyId());
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

          }
        }

        outputModel.setRemainingString(remainingString.trim());
        outputModel
            .setNormalizedRemainingString(normalizeString(remainingString));

      }

    }

    catch (Exception e) {
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
      case "Ingr/Str Mismatch":
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
