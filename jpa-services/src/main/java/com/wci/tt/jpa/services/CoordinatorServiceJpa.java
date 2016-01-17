/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.wci.tt.DataContext;
import com.wci.tt.ScoredDataContext;
import com.wci.tt.helpers.ConfigUtility;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.DataContextTupleJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleJpa;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.handlers.ConverterHandler;
import com.wci.tt.services.handlers.NormalizerHandler;
import com.wci.tt.services.handlers.ProviderHandler;

/**
 * The Class CoordinatorServiceJpa.
 */
public class CoordinatorServiceJpa extends RootServiceJpa implements
    CoordinatorService {

  /** The config properties. */
  protected static Properties config = null;

  /** The normalizer handler . */
  static List<NormalizerHandler> normalizerHandlerMap = new ArrayList<>();

  /** The provider handler . */
  static List<ProviderHandler> providerHandlerMap = new ArrayList<>();

  /** The converter handler . */
  static List<ConverterHandler> converterHandlerMap = new ArrayList<>();

  /** The converter handler . */
  static List<String> availableSpecialties = new ArrayList<>();

  /** The converter handler . */
  static List<String> availableSemanticTypes = new ArrayList<>();

  static {
    /** Add normalizers found in Config to Map. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }

      String key = "normalizer.handlers";
      boolean defaultHandlerFound = false;

      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }

        // Add handlers to map
        NormalizerHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, NormalizerHandler.class);

        normalizerHandlerMap.add(handlerService);

        if (handlerName.equals(ConfigUtility.DEFAULT)) {
          defaultHandlerFound = true;
        }
      }

      if (!defaultHandlerFound) {
        throw new Exception("normalizer.handlers." + ConfigUtility.DEFAULT
            + " expected and does not exist.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      normalizerHandlerMap = null;
    }

    /** Add providers found in Config to Map. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }

      String key = "provider.handlers";
      boolean defaultHandlerFound = false;

      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }

        // Add handlers to map
        ProviderHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ProviderHandler.class);

        providerHandlerMap.add(handlerService);

        if (handlerName.equals(ConfigUtility.DEFAULT)) {
          defaultHandlerFound = true;
        }
      }

      if (!defaultHandlerFound) {
        throw new Exception("provider.handlers." + ConfigUtility.DEFAULT
            + " expected and does not exist.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      providerHandlerMap = null;
    }

    /** Add converters found in Config to Map. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }

      String key = "converter.handlers";
      boolean defaultHandlerFound = false;

      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }

        // Add handlers to map
        ConverterHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ConverterHandler.class);

        converterHandlerMap.add(handlerService);

        if (handlerName.equals(ConfigUtility.DEFAULT)) {
          defaultHandlerFound = true;
        }
      }

      if (!defaultHandlerFound) {
        throw new Exception("converter.handlers." + ConfigUtility.DEFAULT
            + " expected and does not exist.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      converterHandlerMap = null;
    }

    /** Add specialties found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }

      String key = "specialties.available";

      for (String specialty : config.getProperty(key).split(",")) {
        if (specialty.isEmpty()) {
          continue;
        }

        availableSpecialties.add(specialty);
      }
    } catch (Exception e) {
      e.printStackTrace();
      availableSpecialties = null;
    }

    /** Add semanticTypes found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }

      String key = "semanticTypes.available";

      for (String semanticType : config.getProperty(key).split(",")) {
        if (semanticType.isEmpty()) {
          continue;
        }

        availableSemanticTypes.add(semanticType);
      }
    } catch (Exception e) {
      e.printStackTrace();
      availableSemanticTypes = null;
    }
  }

  /**
   * Instantiates an empty {@link CoordinatorServiceJpa}.
   *
   * @throws Exception the exception
   */
  public CoordinatorServiceJpa() throws Exception {
  }

  /* see superclass */
  @Override
  public List<NormalizerHandler> getNormalizers() throws Exception {
    return normalizerHandlerMap;
  }

  /* see superclass */
  @Override
  public List<ProviderHandler> getProviders() throws Exception {
    return providerHandlerMap;
  }

  /* see superclass */
  @Override
  public List<ConverterHandler> getConverters() throws Exception {
    return converterHandlerMap;
  }

  /* see superclass */
  @Override
  public List<String> getSpecialties() throws Exception {
    return availableSpecialties;
  }

  /* see superclass */
  @Override
  public List<String> getSemanticTypes() throws Exception {
    return availableSemanticTypes;
  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(String inputStr,
    DataContext inputContext) throws Exception {
    List<ScoredDataContext> allIdentifiedResults = new ArrayList<>();

    // STEP 1: Call accept per provider: Generates map of provider to list of
    // supported data contexts
    Map<ProviderHandler, List<DataContext>> acceptableProviderContexts =
        getAcceptableProviderContexts(inputContext);

    // Get list of all Data Contexts
    List<DataContext> acceptableContexts = new ArrayList<>();
    for (ProviderHandler key : acceptableProviderContexts.keySet()) {
      acceptableContexts.addAll(acceptableProviderContexts.get(key));
    }

    // STEPS 2: Generate normalized content per accepted data contexts
    List<DataContextTuple> normalizedInputTuples =
        getNormalizedContent(inputStr, acceptableContexts);

    // STEP 3: Call identify per each provider's accepted data contexts on data
    // context's associated normalized results
    for (ProviderHandler handler : acceptableProviderContexts.keySet()) {
      List<DataContext> providerContext =
          acceptableProviderContexts.get(handler);

      for (DataContextTuple normalizedTuple : normalizedInputTuples) {
        if (providerContext.contains(normalizedTuple.getDataContext())) {
          // Provider has Data Context. Identify with normalized string
          // associated with Data Context
          List<ScoredDataContext> identifiedResults =
              handler.identify(normalizedTuple.getData(),
                  normalizedTuple.getDataContext());

          for (ScoredDataContext identifiedResult : identifiedResults) {
            allIdentifiedResults.add((ScoredDataContextJpa) identifiedResult);
          }
        }
      }
    }

    return conductIdentifiedThresholdAnalysis(allIdentifiedResults);
  }

  /* see superclass */
  @Override
  public List<DataContextTuple> process(String inputStr,
    DataContext inputContext, DataContext outputContext) throws Exception {
    List<DataContextTuple> returnTuples = new ArrayList<>();

    // Step 1: Identify contexts acceptable by Provider/Converter pairs
    Map<DataContext, Map<ProviderHandler, Set<ConverterHandler>>> triplets =
        identifyTripletsToProcess(inputContext);

    // STEPS 2: Generate normalized content per accepted data contexts
    List<DataContextTuple> normalizedInputTuples =
        getNormalizedContent(inputStr, triplets.keySet());

    for (DataContext tripletContext : triplets.keySet()) {
      Map<ProviderHandler, Set<ConverterHandler>> providerMap =
          triplets.get(tripletContext);

      // Step 3: Call process and convert for each accepted context on data
      // context's associated normalized results
      for (DataContextTuple normalizedTuple : normalizedInputTuples) {
        if (tripletContext.equals(normalizedTuple.getDataContext())) {
          // Found data context valid for triplet. Now have normalizedTuple &
          // valid triplet
          for (ProviderHandler pHandler : providerMap.keySet()) {
            // Get list of Provider.process() results associated with context
            List<ScoredResult> allProcessedResults =
                pHandler.process(normalizedTuple.getData(), tripletContext,
                    outputContext);

            List<ScoredResult> processedResults =
                conductProcessThresholdAnalysis(allProcessedResults);

            // Get List of Converters associated with Provider/Context
            Set<ConverterHandler> converters = providerMap.get(pHandler);

            // Get Converted results
            for (ScoredResult result : processedResults) {
              for (ConverterHandler cHandler : converters) {
                DataContextTuple tuple =
                    cHandler.convert(result.getValue(), tripletContext,
                        outputContext);

                returnTuples.add((DataContextTupleJpa) tuple);
              }
            }
          }
        }
      }
    }

    return returnTuples;
  }

  /**
   * Identify triplets to process.
   *
   * @param inputContext the input context
   * @return the map
   * @throws Exception the exception
   */
  private Map<DataContext, Map<ProviderHandler, Set<ConverterHandler>>> identifyTripletsToProcess(
    DataContext inputContext) throws Exception {
    Map<DataContext, Map<ProviderHandler, Set<ConverterHandler>>> triplets =
        new HashMap<>();
    Map<ProviderHandler, List<DataContext>> acceptableProviderContexts =
        new HashMap<>();
    Map<ConverterHandler, List<DataContext>> acceptableConverterContexts =
        new HashMap<>();

    // Call accept per provider and converter on input Context
    acceptableProviderContexts = getAcceptableProviderContexts(inputContext);
    acceptableConverterContexts = getAcceptableConverterContexts(inputContext);

    // Identify valid triplets of context/provider/converter
    for (ProviderHandler pHandler : acceptableProviderContexts.keySet()) {
      for (DataContext pContext : acceptableProviderContexts.get(pHandler)) {
        for (ConverterHandler cHandler : acceptableConverterContexts.keySet()) {
          for (DataContext cContext : acceptableConverterContexts.get(cHandler)) {
            if (pContext.equals(cContext)) {
              // Have pair of Provider & Converter that handles DataContext
              if (!triplets.containsKey(cContext)) {
                Map<ProviderHandler, Set<ConverterHandler>> pair =
                    new HashMap<>();
                triplets.put(cContext, pair);
              }

              if (!triplets.get(cContext).containsKey(pHandler)) {
                Set<ConverterHandler> converters = new HashSet<>();
                triplets.get(cContext).put(pHandler, converters);
              }

              // Add pair of Provider & Converter that handles Context to
              // triplet collection
              triplets.get(cContext).get(pHandler).add(cHandler);
            }
          }
        }
      }
    }

    return triplets;
  }

  /**
   * Returns the normalized content.
   *
   * @param inputStr the input str
   * @param contexts the contexts
   * @return the normalized content
   * @throws Exception the exception
   */
  private List<DataContextTuple> getNormalizedContent(String inputStr,
    Collection<DataContext> contexts) throws Exception {
    List<ScoredDataContextTuple> normalizedResults = new ArrayList<>();

    // STEP 1: Normalize input per normalizer
    for (NormalizerHandler handler : getNormalizers()) {
      for (DataContext context : contexts) {
        for (ScoredResult r : handler.normalize(inputStr, context)) {
          // Create Tuple
          ScoredDataContextTuple tuple = new ScoredDataContextTupleJpa();

          tuple.setData(r.getValue());
          tuple.setScore(r.getScore());
          tuple.setDataContext(context);

          normalizedResults.add(tuple);
        }
      }
    }

    // STEP 2: Collate results with an optional threshold constraint
    return conductNormalizedThresholdAnalysis(normalizedResults);
  }

  /**
   * Returns the acceptable provider contexts.
   *
   * @param inputContext the input context
   * @return the acceptable provider contexts
   * @throws Exception the exception
   */
  private Map<ProviderHandler, List<DataContext>> getAcceptableProviderContexts(
    DataContext inputContext) throws Exception {
    Map<ProviderHandler, List<DataContext>> acceptableHandlerContexts =
        new HashMap<>();

    for (ProviderHandler handler : getProviders()) {
      acceptableHandlerContexts.put(handler, handler.accepts(inputContext));
    }

    return acceptableHandlerContexts;
  }

  /**
   * Returns the acceptable converter contexts.
   *
   * @param inputContext the input context
   * @return the acceptable converter contexts
   * @throws Exception the exception
   */
  private Map<ConverterHandler, List<DataContext>> getAcceptableConverterContexts(
    DataContext inputContext) throws Exception {
    Map<ConverterHandler, List<DataContext>> acceptableHandlerContexts =
        new HashMap<>();

    for (ConverterHandler handler : getConverters()) {
      acceptableHandlerContexts.put(handler, handler.accepts(inputContext));
    }

    return acceptableHandlerContexts;
  }

  /**
   * Conduct threshold analysis across all ScoredDataContext created via
   * identify().
   *
   * @param identifiedResults the identified results
   * @return the list
   */
  private List<ScoredDataContext> conductIdentifiedThresholdAnalysis(
    List<ScoredDataContext> identifiedResults) {
    List<ScoredDataContext> returnedResults = new ArrayList<>();

    // For now, very basic routine of returning any result whose score is
    // greater than 0
    for (ScoredDataContext r : identifiedResults) {
      if (r.getScore() > 0) {
        returnedResults.add(r);
      }
    }

    return (returnedResults);
  }

  /**
   * Conduct threshold analysis across all ScoredResult created via process().
   *
   * @param processedResults the processed results
   * @return the list
   */
  private List<ScoredResult> conductProcessThresholdAnalysis(
    List<ScoredResult> processedResults) {
    List<ScoredResult> returnedResults = new ArrayList<>();

    // For now, very basic routine of returning any result whose score is
    // greater than 0
    for (ScoredResult r : processedResults) {
      if (r.getScore() > 0) {
        returnedResults.add(r);
      }
    }

    return (returnedResults);
  }

  /**
   * Conduct threshold analysis across all ScoredDataContextTuples created via
   * normalize().
   *
   * @param results the results
   * @return the list
   */
  private List<DataContextTuple> conductNormalizedThresholdAnalysis(
    List<ScoredDataContextTuple> results) {
    List<DataContextTuple> returnedResults = new ArrayList<>();

    // For now, very basic routine of returning any result whose score is
    // greater than 0
    for (ScoredDataContextTuple r : results) {
      if (r.getScore() > 0) {
        DataContextTuple tuple = new DataContextTupleJpa();

        tuple.setData(r.getData());
        tuple.setDataContext(r.getDataContext());

        returnedResults.add(tuple);
      }
    }

    return (returnedResults);
  }
}
