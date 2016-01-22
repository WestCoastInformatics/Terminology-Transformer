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
import java.util.stream.Collectors;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.infomodels.InfoModel;
import com.wci.tt.jpa.helpers.DataContextTupleJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleJpa;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.handlers.ConverterHandler;
import com.wci.tt.services.handlers.NormalizerHandler;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.services.RootServiceJpa;

/**
 * The Class CoordinatorServiceJpa.
 */
public class CoordinatorServiceJpa extends RootServiceJpa implements
    CoordinatorService {

  /** The config properties. */
  protected static Properties config = null;

  /** The normalizer handler . */
  static List<NormalizerHandler> normalizerHandlerList = new ArrayList<>();

  /** The provider handler . */
  static List<ProviderHandler> providerHandlerList = new ArrayList<>();

  /** The converter handler . */
  static List<ConverterHandler> converterHandlerList = new ArrayList<>();

  /** The information models. */
  static List<InfoModel<?>> informationModelList = new ArrayList<>();

  /** The config-specified specialties available. */
  static List<String> availableSpecialties = new ArrayList<>();

  /** The config-specified semantic types available. */
  static List<String> availableSemanticTypes = new ArrayList<>();

  static {
    /** Add normalizers found in Config to List. */
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

        // Add handlers to List
        NormalizerHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, NormalizerHandler.class);

        normalizerHandlerList.add(handlerService);

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
      normalizerHandlerList = null;
    }

    /** Add providers found in Config to List. */
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

        // Add handlers to List
        ProviderHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ProviderHandler.class);

        providerHandlerList.add(handlerService);

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
      providerHandlerList = null;
    }

    /** Add converters found in Config to List. */
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

        // Add handlers to List
        ConverterHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ConverterHandler.class);

        converterHandlerList.add(handlerService);

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
      converterHandlerList = null;
    }

    /** Add information Models found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }

      String key = "info.model.models";

      for (String informationModel : config.getProperty(key).split(",")) {
        if (informationModel.isEmpty()) {
          continue;
        }
        // Add handlers to List
        InfoModel<?> model =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                informationModel, InfoModel.class);

        informationModelList.add(model);
      }
    } catch (Exception e) {
      e.printStackTrace();
      informationModelList = null;
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
    super();

    if (normalizerHandlerList == null) {
      throw new Exception(
          "Normalizer Handlers did not properly initialize, serious error.");
    }

    if (providerHandlerList == null) {
      throw new Exception(
          "Provider Handlers did not properly initialize, serious error.");
    }

    if (converterHandlerList == null) {
      throw new Exception(
          "Converter Handlers did not properly initialize, serious error.");
    }

    if (informationModelList == null) {
      throw new Exception(
          "The Information Models did not properly initialize, serious error.");
    }

    if (availableSpecialties == null) {
      throw new Exception(
          "The Available Specialties list did not properly initialize, serious error.");
    }

    if (availableSemanticTypes == null) {
      throw new Exception(
          "The Available Semantic Types list did not properly initialize, serious error.");
    }
  }

  /* see superclass */
  @Override
  public List<NormalizerHandler> getNormalizers() throws Exception {
    return normalizerHandlerList;
  }

  /* see superclass */
  @Override
  public List<ProviderHandler> getProviders() throws Exception {
    return providerHandlerList;
  }

  /* see superclass */
  @Override
  public List<ConverterHandler> getConverters() throws Exception {
    return converterHandlerList;
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
  public List<String> getInformationModels() throws Exception {
    return informationModelList.stream().map(model -> model.getName())
        .collect(Collectors.toList());
  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(String inputStr,
    DataContext inputContext) throws Exception {
    List<ScoredDataContext> allIdentifiedResults = new ArrayList<>();

    if (inputStr != null && !inputStr.isEmpty()) {
      // STEP 1: Call accept per provider: Generates map of provider to list of
      // supported data contexts
      Map<ProviderHandler, List<DataContext>> supportedProviderContexts =
          getSupportedProviderContexts(inputContext);

      // Get list of all Data Contexts
      List<DataContext> supportedContexts = new ArrayList<>();
      for (ProviderHandler key : supportedProviderContexts.keySet()) {
        supportedContexts.addAll(supportedProviderContexts.get(key));
      }

      // STEPS 2: Generate normalized content per accepted data contexts
      List<DataContextTuple> normalizedInputTuples =
          getNormalizedContent(inputStr, supportedContexts);

      // STEP 3: Call identify per each provider's accepted data contexts on
      // data
      // context's associated normalized results
      for (ProviderHandler handler : supportedProviderContexts.keySet()) {
        List<DataContext> providerContext =
            supportedProviderContexts.get(handler);

        for (DataContextTuple normalizedTuple : normalizedInputTuples) {
          if (providerContext.contains(normalizedTuple.getDataContext())) {
            // Provider has Data Context. Identify with normalized string
            // associated with Data Context
            List<ScoredDataContext> identifiedResults =
                handler.identify(normalizedTuple.getData(),
                    normalizedTuple.getDataContext());

            for (ScoredDataContext identifiedResult : identifiedResults) {
              allIdentifiedResults =
                  handleDuplicateContexts(allIdentifiedResults,
                      identifiedResult);
            }
          }
        }
      }
    }

    return conductIdentifiedThresholdAnalysis(allIdentifiedResults);
  }

  /* see superclass */
  @Override
  public List<ScoredDataContextTuple> process(String inputStr,
    DataContext inputContext, DataContext outputContext) throws Exception {
    List<ScoredDataContextTuple> returnTuples = new ArrayList<>();

    // Ensure valid data to work with
    if (inputStr != null && !inputStr.isEmpty() && outputContext != null
        && !outputContext.isEmpty()) {

      // Step 1: Identify output contexts supported by Provider/Converter pairs
      Map<ProviderHandler, Map<ConverterHandler, Set<DataContext>>> outputTriplets =
          identifyOutputTripletsToProcess(outputContext);

      // Step 2: Identify input contexts supported by Provider/Converter pairs
      Map<DataContext, Map<ProviderHandler, Set<ConverterHandler>>> inputTriplets =
          identifyInputTripletsToProcess(inputContext, outputTriplets);

      // Step 3: Generate normalized content per accepted data contexts
      List<DataContextTuple> normalizedInputTuples =
          getNormalizedContent(inputStr, inputTriplets.keySet());

      // Step 4: Call process and convert for each accepted context on data
      // context's associated normalized results
      for (DataContext inputTripletContext : inputTriplets.keySet()) {
        Map<ProviderHandler, Set<ConverterHandler>> inputProviderMap =
            inputTriplets.get(inputTripletContext);

        for (DataContextTuple normalizedTuple : normalizedInputTuples) {
          if (inputTripletContext.equals(normalizedTuple.getDataContext())) {
            // Found input data context valid for triplet. Now have
            // normalizedTuple &
            // valid input triplet
            returnTuples.addAll(processAndConvertInputDataContext(
                inputTripletContext, inputProviderMap, normalizedTuple,
                outputTriplets, returnTuples));
          }
        }
      }
    }

    return returnTuples;
  }

  /**
   * Process and convert input data context.
   *
   * @param inputContext the input context
   * @param inputProviderMap the input provider map
   * @param normalizedTuple the normalized tuple
   * @param outputTriplets the output triplets
   * @param returnTuples the return tuples
   * @return the list
   * @throws Exception the exception
   */
  private List<ScoredDataContextTuple> processAndConvertInputDataContext(
    DataContext inputContext,
    Map<ProviderHandler, Set<ConverterHandler>> inputProviderMap,
    DataContextTuple normalizedTuple,
    Map<ProviderHandler, Map<ConverterHandler, Set<DataContext>>> outputTriplets,
    List<ScoredDataContextTuple> returnTuples) throws Exception {
    // For all Providers that support the input Data Context
    for (ProviderHandler inputProvider : inputProviderMap.keySet()) {
      // Obtain the Map of converter to Set of Output Contexts
      Map<ConverterHandler, Set<DataContext>> outputConverterMap =
          outputTriplets.get(inputProvider);

      // For all Converters paired with the Provider supported by the the input
      // context
      for (ConverterHandler inputConverter : inputProviderMap
          .get(inputProvider)) {
        // For all output contexts supported by the Provider/Converter pair
        for (DataContext outputContext : outputConverterMap.get(inputConverter)) {
          // Have the input context and an output context supported by the
          // Provider/Converter Pair
          // Get list of Provider.process() results associated with context
          List<ScoredResult> allProcessedResults =
              inputProvider.process(normalizedTuple.getData(), inputContext,
                  outputContext);

          List<ScoredResult> processedResults =
              conductProcessThresholdAnalysis(allProcessedResults);

          for (ScoredResult result : processedResults) {
            DataContextTuple tuple =
                inputConverter.convert(result.getValue(), inputContext,
                    outputContext);

            ScoredDataContextTuple scoredTuple =
                new ScoredDataContextTupleJpa();

            scoredTuple.setDataContext(tuple.getDataContext());
            scoredTuple.setData(tuple.getData());
            scoredTuple.setScore(result.getScore());

            returnTuples = handleDuplicateContexts(returnTuples, tuple, result);
          }
        }
      }
    }

    return conductProcessAndConvertThresholdAnalysis(returnTuples);
  }

  /**
   * Identify output triplets to process.
   *
   * @param outputContext the output context
   * @return the map
   * @throws Exception the exception
   */
  private Map<ProviderHandler, Map<ConverterHandler, Set<DataContext>>> identifyOutputTripletsToProcess(
    DataContext outputContext) throws Exception {
    Map<ProviderHandler, Map<ConverterHandler, Set<DataContext>>> triplets =
        new HashMap<>();
    Map<ProviderHandler, List<DataContext>> supportedProviderContexts =
        new HashMap<>();
    Map<ConverterHandler, List<DataContext>> supportedConverterContexts =
        new HashMap<>();

    // Call accept per provider and converter on input Context
    supportedProviderContexts = getSupportedProviderContexts(outputContext);
    supportedConverterContexts = getSupportedConverterContexts(outputContext);

    // Identify valid triplets of context/provider/converter
    for (ProviderHandler pHandler : supportedProviderContexts.keySet()) {
      for (DataContext pContext : supportedProviderContexts.get(pHandler)) {
        for (ConverterHandler cHandler : supportedConverterContexts.keySet()) {
          for (DataContext cContext : supportedConverterContexts.get(cHandler)) {
            if (pContext.equals(cContext)) {
              // Have pair of Provider & Converter that handles DataContext
              if (!triplets.containsKey(pHandler)) {
                Map<ConverterHandler, Set<DataContext>> pair = new HashMap<>();
                triplets.put(pHandler, pair);
              }

              if (!triplets.get(pHandler).containsKey(cHandler)) {
                Set<DataContext> contexts = new HashSet<>();
                triplets.get(pHandler).put(cHandler, contexts);
              }

              // Add pair of Provider & Converter that handles Context to
              // triplet collection
              triplets.get(pHandler).get(cHandler).add(cContext);
            }
          }
        }
      }
    }

    return triplets;
  }

  /**
   * Identify triplets to process.
   *
   * @param inputContext the input context
   * @param outputTriplets the output triplets
   * @return the map
   * @throws Exception the exception
   */
  private Map<DataContext, Map<ProviderHandler, Set<ConverterHandler>>> identifyInputTripletsToProcess(
    DataContext inputContext,
    Map<ProviderHandler, Map<ConverterHandler, Set<DataContext>>> outputTriplets)
    throws Exception {
    Map<DataContext, Map<ProviderHandler, Set<ConverterHandler>>> triplets =
        new HashMap<>();
    Map<ProviderHandler, List<DataContext>> supportedProviderContexts =
        new HashMap<>();
    Map<ConverterHandler, List<DataContext>> supportedConverterContexts =
        new HashMap<>();

    // Call accept per provider and converter on input Context
    supportedProviderContexts = getSupportedProviderContexts(inputContext);
    supportedConverterContexts = getSupportedConverterContexts(inputContext);

    // Identify valid triplets of context/provider/converter
    for (ProviderHandler pHandler : supportedProviderContexts.keySet()) {
      for (DataContext pContext : supportedProviderContexts.get(pHandler)) {
        for (ConverterHandler cHandler : supportedConverterContexts.keySet()) {
          for (DataContext cContext : supportedConverterContexts.get(cHandler)) {
            if (pContext.equals(cContext)) {
              // Have pair of Provider & Converter that handles DataContext
              if (inputTripletAllowed(pHandler, cHandler, outputTriplets)) {
                // input provider & converter pair able to handler an output
                // context
                triplets =
                    addInputTriplet(triplets, pHandler, cHandler, cContext);
              }
            }
          }
        }
      }
    }

    return triplets;
  }

  /**
   * Adds the input triplet.
   *
   * @param triplets the triplets
   * @param pHandler the handler
   * @param cHandler the c handler
   * @param cContext the c context
   * @return the map
   */
  private Map<DataContext, Map<ProviderHandler, Set<ConverterHandler>>> addInputTriplet(
    Map<DataContext, Map<ProviderHandler, Set<ConverterHandler>>> triplets,
    ProviderHandler pHandler, ConverterHandler cHandler, DataContext cContext) {
    if (!triplets.containsKey(cContext)) {
      Map<ProviderHandler, Set<ConverterHandler>> pair = new HashMap<>();
      triplets.put(cContext, pair);
    }

    if (!triplets.get(cContext).containsKey(pHandler)) {
      Set<ConverterHandler> converters = new HashSet<>();
      triplets.get(cContext).put(pHandler, converters);
    }

    // Add pair of Provider & Converter that handles Context to
    // triplet collection
    triplets.get(cContext).get(pHandler).add(cHandler);

    return triplets;
  }

  /**
   * Input triplet allowed.
   *
   * @param pHandler the handler
   * @param cHandler the c handler
   * @param outputTriplets the output triplets
   * @return true, if successful
   */
  private boolean inputTripletAllowed(ProviderHandler pHandler,
    ConverterHandler cHandler,
    Map<ProviderHandler, Map<ConverterHandler, Set<DataContext>>> outputTriplets) {
    return (outputTriplets.containsKey(pHandler) && outputTriplets
        .get(pHandler).containsKey(cHandler));
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
   * Returns the supported provider contexts.
   *
   * @param inputContext the input context
   * @return the supported provider contexts
   * @throws Exception the exception
   */
  private Map<ProviderHandler, List<DataContext>> getSupportedProviderContexts(
    DataContext inputContext) throws Exception {
    Map<ProviderHandler, List<DataContext>> supportedHandlerContexts =
        new HashMap<>();

    for (ProviderHandler handler : getProviders()) {
      supportedHandlerContexts.put(handler, handler.accepts(inputContext));
    }

    return supportedHandlerContexts;
  }

  /**
   * Returns the supported converter contexts.
   *
   * @param inputContext the input context
   * @return the supported converter contexts
   * @throws Exception the exception
   */
  private Map<ConverterHandler, List<DataContext>> getSupportedConverterContexts(
    DataContext inputContext) throws Exception {
    Map<ConverterHandler, List<DataContext>> supportedHandlerContexts =
        new HashMap<>();

    for (ConverterHandler handler : getConverters()) {
      supportedHandlerContexts.put(handler, handler.accepts(inputContext));
    }

    return supportedHandlerContexts;
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

  /**
   * Conduct process and convert threshold analysis.
   *
   * @param initialTuples the initial tuples
   * @return the list
   */
  private List<ScoredDataContextTuple> conductProcessAndConvertThresholdAnalysis(
    List<ScoredDataContextTuple> initialTuples) {
    List<ScoredDataContextTuple> returnedResults = new ArrayList<>();

    // For now, very basic routine of returning any result whose score is
    // greater than 0
    for (ScoredDataContextTuple r : initialTuples) {
      if (r.getScore() > 0) {
        returnedResults.add(r);
      }
    }

    return (returnedResults);
  }

  /**
   * Handle duplicate contexts.
   *
   * @param currentResults the current results
   * @param newResult the new result
   * @return the list
   * @throws Exception the exception
   */
  private List<ScoredDataContext> handleDuplicateContexts(
    List<ScoredDataContext> currentResults, ScoredDataContext newResult)
    throws Exception {
    List<ScoredDataContext> returnContexts = new ArrayList<>();

    boolean matchFound = false;
    // For now, sum scores if another data context/data pair match the tuple's
    for (ScoredDataContext currentResult : currentResults) {
      if (currentResult.getCustomer().equals(newResult.getCustomer())
          && currentResult.getSemanticType()
              .equals(newResult.getSemanticType())
          && currentResult.getSpecialty().equals(newResult.getSpecialty())
          && currentResult.getInfoModelName().equals(
              newResult.getInfoModelName())
          && currentResult.getTerminology().equals(newResult.getTerminology())
          && currentResult.getType() == newResult.getType()
          && currentResult.getVersion().equals(newResult.getVersion())) {
        matchFound = true;

        // For now, sum scores if another data context matches the tuple's
        // TODO: Add more than just this basic Naive Solution (create
        // interface?)
        currentResult.setScore(currentResult.getScore() + newResult.getScore());
      }

      returnContexts.add(currentResult);
    }

    if (!matchFound) {
      ScoredDataContext newScoredContext = new ScoredDataContextJpa();

      newScoredContext.setCustomer(newResult.getCustomer());
      newScoredContext.setInfoModelName(newResult.getInfoModelName());
      newScoredContext.setSemanticType(newResult.getSemanticType());
      newScoredContext.setSpecialty(newResult.getSpecialty());
      newScoredContext.setTerminology(newResult.getTerminology());
      newScoredContext.setType(newResult.getType());
      newScoredContext.setVersion(newResult.getVersion());
      newScoredContext.setScore(newResult.getScore());

      returnContexts.add(newScoredContext);
    }

    return returnContexts;
  }

  /**
   * Handle duplicate contexts.
   *
   * @param currentTuples the current tuples
   * @param newTuple the new tuple
   * @param result the result
   * @return the list
   * @throws Exception the exception
   */
  private List<ScoredDataContextTuple> handleDuplicateContexts(
    List<ScoredDataContextTuple> currentTuples, DataContextTuple newTuple,
    ScoredResult result) throws Exception {
    List<ScoredDataContextTuple> returnTuples = new ArrayList<>();

    boolean pairFound = false;
    for (ScoredDataContextTuple currentTuple : currentTuples) {
      if (currentTuple.getDataContext().equals(newTuple.getDataContext())
          && (currentTuple.getData().equals(newTuple.getData()))) {
        pairFound = true;

        // For now, sum scores if another data context/data pair match the
        // tuple's
        // TODO: Add more than just this basic Naive Solution (create
        // interface?)
        currentTuple.setScore(currentTuple.getScore() + result.getScore());
      }

      returnTuples.add(currentTuple);
    }

    if (!pairFound) {
      ScoredDataContextTuple newScoredTuple = new ScoredDataContextTupleJpa();

      newScoredTuple.setDataContext(newTuple.getDataContext());
      newScoredTuple.setData(newTuple.getData());
      newScoredTuple.setScore(result.getScore());

      returnTuples.add(newScoredTuple);
    }

    return returnTuples;
  }

}
