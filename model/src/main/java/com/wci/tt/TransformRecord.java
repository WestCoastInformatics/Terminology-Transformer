/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt;

import java.util.List;
import java.util.Map;

import com.wci.umls.server.helpers.HasId;
import com.wci.umls.server.helpers.HasLastModified;

/**
 * Generically represents a tuple used for processing. It references the input,
 * normalized, and output data strings as well as the identified input context
 * and required output context.
 */
public interface TransformRecord extends HasId, HasLastModified {

  /**
   * Returns the input string.
   *
   * @return the input string
   */
  public String getInputString();

  /**
   * Sets the input string.
   *
   * @param inputString the input string
   */
  public void setInputString(String inputString);

  /**
   * Returns the normalized input strings.
   *
   * @return the normalized input strings
   */
  public List<String> getNormalizedInputStrings();

  /**
   * Sets the normalized input strings.
   *
   * @param normalizedInputStrings the normalized input strings
   */
  public void setNormalizedInputStrings(List<String> normalizedInputStrings);

  /**
   * Returns the input context.
   *
   * @return the input context
   */
  public DataContext getInputContext();

  /**
   * Sets the input context.
   *
   * @param inputContext the input context
   */
  public void setInputContext(DataContext inputContext);

  /**
   * Returns the output string.
   *
   * @return the output string
   */
  public String getOutputString();

  /**
   * Sets the output string.
   *
   * @param outputString the output string
   */
  public void setOutputString(String outputString);

  /**
   * Returns the output context.
   *
   * @return the output context
   */
  public DataContext getOutputContext();

  /**
   * Sets the output context.
   *
   * @param outputContext the output context
   */
  public void setOutputContext(DataContext outputContext);

  /**
   * Returns the characteristics.
   *
   * @return the characteristics
   */
  public Map<String, String> getCharacteristics();

  /**
   * Sets the characteristics.
   *
   * @param characteristics the characteristics
   */
  public void setCharacteristics(Map<String, String> characteristics);

  /**
   * Returns the statistics.
   *
   * @return the statistics
   */
  public Map<String, Double> getStatistics();

  /**
   * Sets the statistics.
   *
   * @param statistics the statistics
   */
  public void setStatistics(Map<String, Double> statistics);

}
