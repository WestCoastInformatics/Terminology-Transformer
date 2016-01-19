/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services;

import java.util.List;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredDataContext;
import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.services.handlers.ConverterHandler;
import com.wci.tt.services.handlers.NormalizerHandler;
import com.wci.tt.services.handlers.ProviderHandler;

/**
 * Generically represents a service for handling top-level
 * terminology-transformation routines.
 */
public interface CoordinatorService extends RootService {

  /**
   * Returns the normalizers.
   *
   * @return the normalizers
   * @throws Exception the exception
   */
  public List<NormalizerHandler> getNormalizers() throws Exception;

  /**
   * Returns the providers.
   *
   * @return the providers
   * @throws Exception the exception
   */
  public List<ProviderHandler> getProviders() throws Exception;

  /**
   * Returns the converters.
   *
   * @return the converters
   * @throws Exception the exception
   */
  public List<ConverterHandler> getConverters() throws Exception;

  /**
   * Returns the specialties.
   *
   * @return the specialties
   * @throws Exception the exception
   */
  public List<String> getSpecialties() throws Exception;

  /**
   * Returns the semantic types.
   *
   * @return the semantic types
   * @throws Exception the exception
   */
  public List<String> getSemanticTypes() throws Exception;

  /**
   * Returns the information models.
   *
   * @return the information models
   * @throws Exception the exception
   */
  public List<String> getInformationModels() throws Exception;

  /**
   * Finds which data contexts are legible across all providers.
   * 
   * To do so, the method takes the input string and input data context and
   * looks at all Providers to determine their probability of being able to
   * process the input string.
   * 
   * Steps: 
   *   1) Calls accept per provider 
   *     - Generates map of provider to list of supported data contexts 
   *   2) Generate normalized content per accepted data contexts 
   *     - Generates collated List of DataContextTuple 
   *   3) Call identify for each provider's accepted data context on data context's 
   *   associated normalized results 
   *     - Generates List of ScoredDataContext
   *
   * The associated score may be examined to determine if the probability score 
   * passes a statistical threshold.  This includes for:
   *   - For all content generated via all normalize() calls
   *   - For all content generated via all identify() calls
   *
   * @param inputStr the input string
   * @param inputContext the input context
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredDataContext> identify(String inputStr,
    DataContext inputContext) throws Exception;

  /**
   * Assembles the possible combinations of Providers & Converters based on
   * input Context. Then normalizes content across all normalizers per data
   * context. For each Provider/Converter combination, puts pair's data context
   * and associated normalized content through process & convert methods. All
   * data is returned.
   * 
   * Steps: 
   *   1) Identify contexts acceptable by Provider/Converter pairs  
   *     - Generates triplet (DataContext to Provider to Set<Converters>) 
   *   2) Generate normalized content per accepted data contexts 
   *     - Generates collated List of DataContextTuple 
   *   3) Calls process and convert for each accepted context on data 
   *   context's associated normalized results
   *     - Generates List of DataContextTuples
   *     
   * The associated score may be examined to determine if the probability score 
   * passes a statistical threshold.  This includes for:
   *   - For all content generated via all normalize() calls
   *   - For all content generated via all process() calls
   *   
   * @param inputStr the input string
   * @param inputContext the input context
   * @param outputContext the output context
   * @return the list
   * @throws Exception the exception
   */
  public List<ScoredDataContextTuple> process(String inputStr,
    DataContext inputContext, DataContext outputContext) throws Exception;
}
