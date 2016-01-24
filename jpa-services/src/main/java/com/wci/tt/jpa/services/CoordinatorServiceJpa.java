/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.DataContextType;
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

      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }

        // Add handlers to List
        NormalizerHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, NormalizerHandler.class);

        normalizerHandlerList.add(handlerService);
      }

      if (normalizerHandlerList.isEmpty()) {
        throw new Exception(
            "normalizer.handlers must have one value but none exist");
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

      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }

        // Add handlers to List
        ProviderHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ProviderHandler.class);

        providerHandlerList.add(handlerService);
      }

      if (providerHandlerList.isEmpty()) {
        throw new Exception(
            "provider.handlers must have one value but none exist");
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

      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }

        // Add handlers to List
        ConverterHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ConverterHandler.class);

        converterHandlerList.add(handlerService);
      }

      if (converterHandlerList.isEmpty()) {
        throw new Exception(
            "converter.handlers must have one value but none exist");
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
    DataContext requiredInputContext) throws Exception {
    List<ScoredDataContext> allIdentifiedResults = new ArrayList<>();

    if (inputStr != null && !inputStr.isEmpty()) {
      // STEP 1: Call accept per provider: Generates map of provider to list of
      // supported data contexts
      Map<ProviderHandler, List<DataContext>> supportedProviderContexts =
          getSupportedProviders(requiredInputContext);

      // Get list of all Data Contexts
      List<DataContext> supportedContexts = new ArrayList<>();
      for (ProviderHandler key : supportedProviderContexts.keySet()) {
        supportedContexts.addAll(supportedProviderContexts.get(key));
      }

      // STEPS 2: Generate normalized content per accepted data contexts
      List<DataContextTuple> normalizedInputTuples =
          getNormalizedContent(inputStr, requiredInputContext);

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
    DataContext requiredInputContext, DataContext requiredOutputContext)
    throws Exception {
    // Ensure valid data to work with
    if (inputStr != null && !inputStr.isEmpty()
        && requiredOutputContext != null && !requiredOutputContext.isEmpty()) {

      // Step 1: Identify the output contexts supported by Providers that
      // accepts() the inputContext
      Map<ProviderHandler, List<DataContext>> supportedProviders =
          getSupportedProviders(requiredInputContext);

      // Grab provider output contexts
      List<DataContext> outputContexts = new ArrayList<>();
      for (List<DataContext> outputs : supportedProviders.values()) {
        outputContexts.addAll(outputs);
      }

      // Step 2: Identify the Converters that handle the provider output
      // contexts (as inputs)
      // && whose output context contains the requested outputContext
      Map<DataContext, List<ConverterHandler>> supportedConverters =
          getSupportedConverters(outputContexts, requiredOutputContext);

      // Step 3: Generate normalized content per accepted data contexts
      List<DataContextTuple> normalizedInputTuples =
          getNormalizedContent(inputStr, requiredInputContext);

      // Step 4: Call process and convert for each accepted context on data
      // context's associated normalized results
      return processAndConvert(normalizedInputTuples, requiredInputContext,
          requiredOutputContext, supportedProviders, supportedConverters);
    }

    return new ArrayList<ScoredDataContextTuple>();
  }

  /**
   * Process and convert.
   *
   * @param normalizedInputTuples the normalized input tuples
   * @param requiredInputContext the required input context
   * @param requiredOutputContext the required output context
   * @param supportedProviders the supported providers
   * @param supportedConverters the supported converters
   * @return the list
   * @throws Exception the exception
   */
  private List<ScoredDataContextTuple> processAndConvert(
    List<DataContextTuple> normalizedInputTuples,
    DataContext requiredInputContext, DataContext requiredOutputContext,
    Map<ProviderHandler, List<DataContext>> supportedProviders,
    Map<DataContext, List<ConverterHandler>> supportedConverters)
    throws Exception {
    List<ScoredDataContextTuple> results = new ArrayList<>();

    for (ProviderHandler pHandler : supportedProviders.keySet()) {

      if (supportedProviders.containsKey(pHandler)) {
        for (DataContext intermediateContext : supportedProviders.get(pHandler)) {

          if (supportedConverters.containsKey(intermediateContext)) {
            for (ConverterHandler cHandler : supportedConverters
                .get(intermediateContext)) {
              // <pre>
              // Have a Provider/Converter pair that:
              // 1) Has proper intermediateContext such that:
              // the output of provider is the input to the converter
              // 2) the Provider supports the requiredInputContext
              // 3) The Converter supports the requiredOutputContext
              // </pre>

              List<ScoredResult> processedResults = new ArrayList<>();
              for (DataContextTuple inputTuple : normalizedInputTuples) {
                // TODO: Handle Dups (b/c of scoring)
                processedResults.addAll(pHandler.process(inputTuple.getData(),
                    requiredInputContext, intermediateContext));
              }

              // TODO: Analyze contents to identify threshold

              List<DataContextTuple> convertedResults = new ArrayList<>();
              for (ScoredResult pResult : processedResults) {
                // TODO: Handle Dups (b/c of scoring)
                convertedResults.add(cHandler.convert(pResult.getValue(),
                    intermediateContext, requiredOutputContext));

                // TODO: Analyze contents to identify threshold

                for (DataContextTuple cResult : convertedResults) {
                  ScoredDataContextTuple finalResult =
                      new ScoredDataContextTupleJpa();

                  finalResult.setDataContext(cResult.getDataContext());
                  finalResult.setData(cResult.getData());
                  finalResult.setScore(pResult.getScore());

                  // TODO: Handle Dups (b/c of scoring)

                  results.add(finalResult);
                }
              }
            }
          }
        }
      }
    }

    // TODO: Analyze contents to identify threshold

    return results;
  }

  /**
   * Returns the normalized content.
   *
   * @param inputStr the input str
   * @param requiredInputContext the contexts
   * @return the normalized content
   * @throws Exception the exception
   */
  private List<DataContextTuple> getNormalizedContent(String inputStr,
    DataContext requiredInputContext) throws Exception {
    List<ScoredDataContextTuple> normalizedResults = new ArrayList<>();

    // STEP 1: Normalize input per normalizer
    for (NormalizerHandler handler : getNormalizers()) {
      for (ScoredResult r : handler.normalize(inputStr, requiredInputContext)) {
        // Create Tuple
        ScoredDataContextTuple tuple = new ScoredDataContextTupleJpa();

        tuple.setData(r.getValue());
        tuple.setScore(r.getScore());
        tuple.setDataContext(requiredInputContext);

        normalizedResults.add(tuple);
      }
    }

    // STEP 2: Collate results with an optional threshold constraint
    return conductNormalizedThresholdAnalysis(normalizedResults);
  }

  /**
   * Returns the supported provider contexts.
   *
   * @param requiredInputContext the input context
   * @return the supported provider contexts
   * @throws Exception the exception
   */
  private Map<ProviderHandler, List<DataContext>> getSupportedProviders(
    DataContext requiredInputContext) throws Exception {
    Map<ProviderHandler, List<DataContext>> supportedHandlerContexts =
        new HashMap<>();

    for (ProviderHandler handler : getProviders()) {
      List<DataContext> supportedContexts =
          handler.accepts(requiredInputContext);

      if (!supportedContexts.isEmpty()) {
        supportedHandlerContexts.put(handler, supportedContexts);
      }
    }

    return supportedHandlerContexts;
  }

  /**
   * Returns the supported converter contexts.
   *
   * @param possibleInputContexts the possible input contexts
   * @param requiredOutputContext the required output context
   * @return the supported converter contexts
   * @throws Exception the exception
   */
  private Map<DataContext, List<ConverterHandler>> getSupportedConverters(
    List<DataContext> possibleInputContexts, DataContext requiredOutputContext)
    throws Exception {
    Map<DataContext, List<ConverterHandler>> supportedHandlerContexts =
        new HashMap<>();

    for (DataContext inputContext : possibleInputContexts) {
      for (ConverterHandler handler : getConverters()) {
        List<DataContext> supportedContexts = handler.accepts(inputContext);

        for (DataContext c : supportedContexts) {
          // Only add those contexts which match the requiredOutputContext
          if (isAcceptableOutputContext(c, requiredOutputContext)) {
            if (!supportedHandlerContexts.keySet().contains(inputContext)) {
              // First time seeing Key, so create new map entry
              List<ConverterHandler> cHandlers = new ArrayList<>();
              supportedHandlerContexts.put(inputContext, cHandlers);
            }

            // Add to Map
            supportedHandlerContexts.get(inputContext).add(handler);
          }
        }
      }
    }

    return supportedHandlerContexts;
  }

  /**
   * Indicates whether or not acceptable output context is the case.
   *
   * @param testContext the test context
   * @param requiredOutputContext the required output context
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  private boolean isAcceptableOutputContext(DataContext testContext,
    DataContext requiredOutputContext) {
    if (testContext.getType() != DataContextType.UNKNOWN) {
      if (testContext.getType() != requiredOutputContext.getType()) {
        return false;
      }

      if (testContext.getType() == DataContextType.INFO_MODEL) {
        if (!testContext.getInfoModelName().equalsIgnoreCase(
            requiredOutputContext.getInfoModelName())) {
          return false;
        }
      }
    }

    if (!testContextValue(testContext.getCustomer(),
        requiredOutputContext.getCustomer())
        || !testContextValue(testContext.getCustomer(),
            requiredOutputContext.getCustomer())
        || !testContextValue(testContext.getCustomer(),
            requiredOutputContext.getCustomer())
        || !testContextValue(testContext.getCustomer(),
            requiredOutputContext.getCustomer())
        || !testContextValue(testContext.getCustomer(),
            requiredOutputContext.getCustomer())) {
      return false;
    }

    return true;
  }

  /**
   * Test context value.
   *
   * @param testStr the test str
   * @param requiredStr the required str
   * @return true, if successful
   */
  private boolean testContextValue(String testStr, String requiredStr) {
    if (requiredStr != null && !requiredStr.isEmpty()) {
      if (!testStr.equalsIgnoreCase(requiredStr)) {
        return false;
      }
    }

    return true;
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
