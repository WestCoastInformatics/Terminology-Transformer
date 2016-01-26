/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.infomodels.InfoModel;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.handlers.ConverterHandler;
import com.wci.tt.services.handlers.NormalizerHandler;
import com.wci.tt.services.handlers.ProviderHandler;
import com.wci.tt.services.handlers.SourceDataLoader;
import com.wci.tt.services.handlers.ThresholdHandler;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.services.RootServiceJpa;

/**
 * JPA and JAXB-enabled implementation of {@link CoordinatorService}.
 */
public class CoordinatorServiceJpa extends RootServiceJpa
    implements CoordinatorService {

  /** The config properties. */
  protected static Properties config = null;

  /** The threshold. */
  static ThresholdHandler threshold = null;

  /** The source data loaders. */
  static Map<String, SourceDataLoader> loaders = new HashMap<>();

  /** The normalizer handler . */
  static Map<String, NormalizerHandler> normalizers = new HashMap<>();

  /** The provider handler . */
  static Map<String, ProviderHandler> providers = new HashMap<>();

  /** The converter handler . */
  static Map<String, ConverterHandler> converters = new HashMap<>();

  /** The information models. */
  static Map<String, InfoModel<?>> infoModels = new HashMap<>();

  /** The config-specified specialties available. */
  static List<String> specialties = new ArrayList<>();

  /** The config-specified semantic types available. */
  static List<String> semanticTypes = new ArrayList<>();

  static {

    /** threshold handler - only one */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "threshold.handler";
      String handlerName = config.getProperty(key);
      // Add handlers to List
      threshold = ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
          handlerName, ThresholdHandler.class);

      if (threshold == null)
        throw new Exception(
            "threshold.handler must have exactly one value but none exists");
    } catch (Exception e) {
      e.printStackTrace();
      threshold = null;
    }

    /** Configure loaders */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "source.data.loader.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }
        // Add handlers to List
        SourceDataLoader handler =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, SourceDataLoader.class);
        loaders.put(handlerName, handler);
      }
      if (loaders.isEmpty()) {
        throw new Exception(
            "source.data.loader.handler must have one value but none exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
      loaders = null;
    }

    /** Add normalizers found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "normalizer.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }
        // Add handlers to List
        NormalizerHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, NormalizerHandler.class);
        normalizers.put(handlerName, handlerService);
      }
      if (normalizers.isEmpty()) {
        throw new Exception(
            "normalizer.handler must have one value but none exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
      normalizers = null;
    }

    /** Add providers found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "provider.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }
        // Add handlers to List
        ProviderHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ProviderHandler.class);
        providers.put(handlerName, handlerService);
      }
      if (providers.isEmpty()) {
        throw new Exception(
            "provider.handler must have one value but none exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
      providers = null;
    }

    /** Add converters found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "converter.handler";
      for (String handlerName : config.getProperty(key).split(",")) {
        if (handlerName.isEmpty()) {
          continue;
        }
        // Add handlers to List
        ConverterHandler handlerService =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                handlerName, ConverterHandler.class);
        converters.put(handlerName, handlerService);
      }
      if (converters.isEmpty()) {
        throw new Exception(
            "converter.handler must have one value but none exist");
      }
    } catch (Exception e) {
      e.printStackTrace();
      converters = null;
    }

    /** Add information Models found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "info.model";
      for (String informationModel : config.getProperty(key).split(",")) {
        if (informationModel.isEmpty()) {
          continue;
        }
        // Add handlers to List
        InfoModel<?> model =
            ConfigUtility.newStandardHandlerInstanceWithConfiguration(key,
                informationModel, InfoModel.class);

        infoModels.put(informationModel, model);
      }
    } catch (Exception e) {
      e.printStackTrace();
      infoModels = null;
    }

    /** Add specialties found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "specialties.available";
      for (String specialty : config.getProperty(key).split(";")) {
        if (specialty.isEmpty()) {
          continue;
        }
        specialties.add(specialty);
      }
    } catch (Exception e) {
      e.printStackTrace();
      specialties = null;
    }

    /** Add semanticTypes found in Config to List. */
    try {
      if (config == null) {
        config = ConfigUtility.getConfigProperties();
      }
      String key = "semanticTypes.available";
      for (String semanticType : config.getProperty(key).split(";")) {
        if (semanticType.isEmpty()) {
          continue;
        }
        semanticTypes.add(semanticType);
      }
    } catch (Exception e) {
      e.printStackTrace();
      semanticTypes = null;
    }

  }

  /**
   * Instantiates an empty {@link CoordinatorServiceJpa}.
   *
   * @throws Exception the exception
   */
  public CoordinatorServiceJpa() throws Exception {
    super();

    if (normalizers == null) {
      throw new Exception(
          "Normalizer Handlers did not properly initialize, serious error.");
    }

    if (providers == null) {
      throw new Exception(
          "Provider Handlers did not properly initialize, serious error.");
    }

    if (converters == null) {
      throw new Exception(
          "Converter Handlers did not properly initialize, serious error.");
    }

    if (infoModels == null) {
      throw new Exception(
          "The Information Models did not properly initialize, serious error.");
    }

    if (specialties == null) {
      throw new Exception(
          "The Available Specialties list did not properly initialize, serious error.");
    }

    if (semanticTypes == null) {
      throw new Exception(
          "The Available Semantic Types list did not properly initialize, serious error.");
    }
  }

  /* see superclass */
  @Override
  public Map<String, NormalizerHandler> getNormalizers() throws Exception {
    return normalizers;
  }

  /* see superclass */
  @Override
  public Map<String, ProviderHandler> getProviders() throws Exception {
    return providers;
  }

  /* see superclass */
  @Override
  public Map<String, ConverterHandler> getConverters() throws Exception {
    return converters;
  }

  /* see superclass */
  @Override
  public List<String> getSpecialties() throws Exception {
    return specialties;
  }

  /* see superclass */
  @Override
  public List<String> getSemanticTypes() throws Exception {
    return semanticTypes;
  }

  /* see superclass */
  @Override
  public Map<String, InfoModel<?>> getInformationModels() throws Exception {
    return infoModels;
  }

  /* see superclass */
  @Override
  public List<ScoredDataContext> identify(String inputStr,
    DataContext requiredInputContext) throws Exception {
    Logger.getLogger(getClass())
        .info("Identify - " + inputStr + ", " + requiredInputContext);
    final List<ScoredDataContext> allIdentifiedResults = new ArrayList<>();

    // Nothing identified
    if (inputStr == null || inputStr.isEmpty()) {
      return new ArrayList<>();
    }

    // STEP 1: Call accept per provider: Generates map of provider to list of
    // supported data contexts
    final List<ProviderHandler> providers =
        getIdentifyProviders(requiredInputContext);
    Logger.getLogger(getClass()).debug("  providers = " + providers);

    // STEPS 2: Generate normalized content per accepted data contexts
    // All output data context tuples should be compatible with providers list
    final List<ScoredResult> normalizedResults =
        normalize(inputStr, requiredInputContext, true);

    // STEP 3: Call identify per each provider's accepted data contexts on
    // data context's associated normalized results
    final Set<ScoredDataContext> identifiedResults = new HashSet<>();
    for (final ProviderHandler provider : providers) {
      for (final ScoredResult result : normalizedResults) {
        final List<ScoredDataContext> identifiedResult =
            provider.identify(result.getValue(), requiredInputContext);

        if (identifiedResult == null) {
          throw new Exception("Provider returned null results from identify - "
              + provider.getName());
        } else {
          // this should unique the results because use of a set
          identifiedResults.addAll(identifiedResult);
        }
      }
    }

    // TODO: aggregate, like in process

    // Apply threshold and return the results
    Logger.getLogger(getClass())
        .debug("  all results = " + allIdentifiedResults);
    final List<ScoredDataContext> list =
        threshold.applyThreshold(allIdentifiedResults);
    Logger.getLogger(getClass()).debug("  threshold results = " + list);
    return list;
  }

  /* see superclass */
  @Override
  public List<ScoredResult> process(String inputStr,
    DataContext requiredInputContext, DataContext requiredOutputContext)
      throws Exception {
    Logger.getLogger(getClass()).info("Process - " + inputStr);
    Logger.getLogger(getClass())
        .debug("  input context = " + requiredInputContext);
    Logger.getLogger(getClass())
        .debug("  output context = " + requiredOutputContext);

    // no processors
    if (inputStr == null || inputStr.isEmpty() || requiredOutputContext == null
        || requiredOutputContext == null || requiredOutputContext.isEmpty()) {
      return new ArrayList<>();
    }

    // STEP 1: Identify providers/converters from input context to output
    // context.
    final Map<ProviderHandler, List<DataContext>> providerMap =
        getProcessProviders(requiredInputContext);
    Logger.getLogger(getClass()).debug("  providers = " + providerMap);

    // Collect provider output contexts from the provider map
    final Set<DataContext> providerOutputContexts = new HashSet<>();
    for (final ProviderHandler provider : providerMap.keySet()) {
      providerOutputContexts.addAll(providerMap.get(provider));
    }
    Logger.getLogger(getClass())
        .debug("  provider output contexts = " + providerOutputContexts);

    // Step 2: Identify the Converters that handle the provider output
    // contexts (as inputs)
    // && whose output context contains the requested outputContext
    final Map<DataContext, List<ConverterHandler>> converterMap =
        new HashMap<>();
    for (final DataContext converterInputContext : providerOutputContexts) {
      final List<ConverterHandler> converters =
          getConverters(converterInputContext, requiredOutputContext);
      converterMap.put(converterInputContext, converters);
    }
    Logger.getLogger(getClass()).debug("  converters = " + converterMap);

    // Step 3: Generate normalized content per accepted data contexts
    List<ScoredResult> normalizedResults =
        normalize(inputStr, requiredInputContext, true);

    // Step 4: Process and collate the results
    final Map<ProviderHandler, List<ScoredResult>> allEvidenceMap =
        new HashMap<>();
    // for each provider
    for (final ProviderHandler provider : providerMap.keySet()) {

      // Evidence from ths provider
      final Map<String, Float> providerEvidenceMap = new HashMap<>();
      // for each output context it generates
      for (final DataContext outputContext : providerMap.get(provider)) {
        // for each supported converter
        for (final ConverterHandler converter : converterMap
            .get(outputContext)) {
          // for each normalized result
          for (final ScoredResult normalizedInputStr : normalizedResults) {

            // Obtain the processed results
            for (final ScoredResult result : provider.process(
                normalizedInputStr.getValue(), requiredInputContext,
                outputContext)) {

              // Obtain the final product
              final DataContextTuple tuple = converter.convert(
                  result.getValue(), outputContext, requiredOutputContext);

              // Compute the score for this piece of evidence
              // Weight by provider quality
              // Weight by normalized input score
              // TODO: this weighting algorithm can be abstracted
              final float score = threshold.weightResult(result.getScore(),
                  normalizedInputStr.getScore(), provider.getQuality());

              // Put in providerEvidenceMap if we don't have an entry yet
              // or this one has a higher score.
              Logger.getLogger(getClass())
                  .debug("  evidence = " + provider.getName() + ", "
                      + converter.getName() + ", "
                      + normalizedInputStr.getValue() + " = " + score + ", "
                      + tuple.getData());

              if (!providerEvidenceMap.containsKey(tuple.getData())
                  || providerEvidenceMap.get(tuple.getData()) < score) {
                providerEvidenceMap.put(tuple.getData(), score);
              }

            } // end process
          } // end normalized results
        } // end converter map
      } // end intermediate output context

      // Add evidence from this provider to the overall list
      final List<ScoredResult> providerResults = new ArrayList<>();
      for (final String key : providerEvidenceMap.keySet()) {
        final ScoredResult providerResult = new ScoredResultJpa();
        providerResult.setValue(key);
        providerResult.setScore(providerEvidenceMap.get(key));
      }
      allEvidenceMap.put(provider, providerResults);

    }

    // Now aggregate the evidence across all providers and sort
    final List<ScoredResult> aggregatedResults =
        threshold.aggregate(allEvidenceMap);
    Collections.sort(aggregatedResults);
    Logger.getLogger(getClass())
        .debug("  final evidence = " + aggregatedResults);
    return aggregatedResults;
  }

  /* see superclass */
  @Override
  public List<ScoredResult> normalize(String inputStr,
    DataContext requiredInputContext, boolean includeOrig) throws Exception {
    Logger.getLogger(getClass())
        .info("Normalize - " + inputStr + ", " + requiredInputContext);
    List<ScoredResult> normalizedResults = new ArrayList<>();

    // STEP 1: Normalize input per normalizer
    final Map<String, Float> scoreMap = new HashMap<>();
    for (final NormalizerHandler normalizer : getNormalizers().values()) {

      // Get the max score for each value of the various normalizers
      for (final ScoredResult result : normalizer.normalize(inputStr,
          requiredInputContext)) {
        // Retain highest score per value
        final float score = result.getScore() * normalizer.getQuality();
        if (!scoreMap.containsKey(result.getValue())
            || scoreMap.get(result.getValue()) < score) {
          // Weight score by normalizer quality
          scoreMap.put(result.getValue(), score);
        }
      }
    }

    // Add original data if desired
    if (includeOrig) {
      Logger.getLogger(getClass()).debug("  include orig = true");
      scoreMap.put(inputStr, 1.0f);
    } else {
      Logger.getLogger(getClass()).debug("  include orig = false");
    }

    // Put scoreMap into normalizedResults
    for (final String key : scoreMap.keySet()) {
      final ScoredResult result = new ScoredResultJpa();
      result.setValue(key);
      result.setScore(scoreMap.get(key));
    }

    // Apply threshold to scores and return
    normalizedResults = threshold.applyThreshold(normalizedResults);
    Logger.getLogger(getClass())
        .debug("  normalized results = " + normalizedResults);
    return normalizedResults;
  }

  /* see superclass */
  @Override
  public Map<String, SourceDataLoader> getSourceDataLoaders() throws Exception {
    return loaders;
  }

  /**
   * Returns the supported provider contexts. Returns an empty list if there are
   * no supported providers.
   *
   * @param inputContext the input context
   * @return the supported provider contexts
   * @throws Exception the exception
   */
  private List<ProviderHandler> getIdentifyProviders(DataContext inputContext)
    throws Exception {
    final List<ProviderHandler> providers = new ArrayList<>();

    // Ask each provider if it accepts
    for (final ProviderHandler provider : getProviders().values()) {
      final List<DataContext> supportedContexts =
          provider.accepts(inputContext);
      if (supportedContexts == null) {
        throw new Exception("Provider unexpectedly returned null for accepts - "
            + provider.getName());
      }
      if (!supportedContexts.isEmpty()) {
        providers.add(provider);
      }
    }
    return providers;
  }

  /**
   * Returns the process providers mapped to the output contexts supported for
   * the specified input context.
   *
   * @param inputContext the input context
   * @return the process providers
   * @throws Exception the exception
   */
  private Map<ProviderHandler, List<DataContext>> getProcessProviders(
    DataContext inputContext) throws Exception {

    final Map<ProviderHandler, List<DataContext>> providerMap = new HashMap<>();

    // Ask each provider if it accepts
    for (final ProviderHandler provider : getProviders().values()) {
      final List<DataContext> supportedContexts =
          provider.accepts(inputContext);
      if (supportedContexts == null) {
        throw new Exception("Provider unexpectedly returned null for accepts - "
            + provider.getName());
      }
      if (!supportedContexts.isEmpty()) {
        providerMap.put(provider, supportedContexts);

      }
    }
    return providerMap;
  }

  /**
   * Returns the converters.
   *
   * @param inputContext the input context
   * @param outputContext the output context
   * @return the converters
   * @throws Exception the exception
   */
  private List<ConverterHandler> getConverters(DataContext inputContext,
    DataContext outputContext) throws Exception {
    final List<ConverterHandler> converters = new ArrayList<>();

    // Ask each converter if it accepts
    for (final ConverterHandler converter : getConverters().values()) {
      final List<DataContext> supportedContexts =
          converter.accepts(inputContext);
      if (supportedContexts == null) {
        throw new Exception(
            "Converter unexpectedly returned null for accepts - "
                + converter.getName());
      }
      if (!supportedContexts.isEmpty()) {
        // See if output matches any supported Contexts
        for (final DataContext matchContext : supportedContexts) {
          if (DataContextMatcher.matches(outputContext, matchContext)) {
            converters.add(converter);
            break;
          }
        }
      }
    }
    return converters;
  }

}
