/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredResultJpa;

/**
 * A normalizer for NLDP cases - using specific acronym lists.
 */
public class MldpNormalizer extends AbstractNormalizer {

  /** The quality. */
  private float quality;

  /** The semantic type. */
  private String semanticType = null;

  /** The abbr type. */
  private String abbrType = null;

  /**
   * Instantiates an empty {@link MldpNormalizer}.
   */
  public MldpNormalizer() {
    // n/a
  }

  /* see superclass */
  @Override
  public String getName() {
    return "MLDP Normalizer Handler";
  }

  /* see superclass */
  @Override
  public List<ScoredResult> normalize(String inputString, DataContext context)
    throws Exception {
    Logger.getLogger(getClass()).debug("  normalize - " + inputString);

    if (inputString == null) {
      return new ArrayList<>();
    }
    final List<ScoredResult> results = new ArrayList<>();

    // Convert all whitespace to spaces
    final String clean = inputString.replaceAll("[\\s]", " ");
    
    // expand typed acronyms
    results.add(new ScoredResultJpa(expandAcronyms(clean, " ", abbrType),
        getQuality()));

    return results;
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    if (p == null) {
      throw new Exception("A quality property is required");
    }
    if (!p.containsKey("quality")) {
      throw new Exception("A quality property is required");
    }
    if (!p.containsKey("abbrType")) {
      throw new Exception("A abbrType property is required");
    }
    if (!p.containsKey("semanticType")) {
      throw new Exception("A semanticType property is required");
    }

    try {
      quality = Float.parseFloat(p.getProperty("quality"));
      if (quality < 0 || quality > 1) {
        throw new Exception();
      }
    } catch (Exception e) {
      throw new Exception(
          "quality property must be a float value between 0 and 1");
    }

    abbrType = p.getProperty("abbrType");

    semanticType = p.getProperty("semanticType");
  }

  /* see superclass */
  @Override
  public float getQuality() {
    return quality;
  }

  /* see superclass */
  @Override
  public boolean accepts(DataContext inputContext) throws Exception {
    // Handle text-only types with matching semantic type
    return inputContext.getSemanticType().equals(semanticType)
        && (inputContext.getType() == DataContextType.TEXT
            || inputContext.getType() == DataContextType.NAME);
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }
  
  @Override
  public void checkProperties(Properties arg0) throws Exception {
    // n/a
  }
}
