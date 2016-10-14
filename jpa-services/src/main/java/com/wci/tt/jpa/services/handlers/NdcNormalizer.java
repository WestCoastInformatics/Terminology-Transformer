/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredResultJpa;

/**
 * A converter for NDC codes. Accepts a variety of forms of 9, 10, and 11 digit
 * NDC codes and standardizes them for RXNORM representation.
 */
public class NdcNormalizer extends AbstractNormalizer {

  /** The quality. */
  private float quality;

  /**
   * Instantiates an empty {@link NdcNormalizer}.
   */
  public NdcNormalizer() {
    // n/a
  }

  /* see superclass */
  @Override
  public String getName() {
    return "NDC Normalizer Handler";
  }

  /* see superclass */
  @Override
  public List<ScoredResult> normalize(String inputString, DataContext context)
    throws Exception {
    Logger.getLogger(getClass()).debug("  normalize - " + inputString);

    if (inputString == null) {
      return new ArrayList<>();
    }

    String linputString = inputString;

    /**
     * <pre>
     *   
     * reformat any 3-segment NDC into an 11-digit NDC without hyphens
     *
     *  NB:
     *  * some valid NDCs contain non-numeric digits (product code and package code)
     *   e.g., 53157-AS3-BO 8 POUCH in 1 BOX (53157-AS3-BO)  > 3 BAG in 1 POUCH (53157-AS3-PO)  > 110 mL in 1 BAG (53157-AS3-BG) 
     * * the NDC11 format (no hyphens) is known as the HIPPA NDC format
     * * convert into a 5-4-2 configuration (without hyphens) by padding to the left with zeros
     * * official NDC10 formats:
     *   - 4-4-2
     *   - 5-3-2
     *   - 5-4-1
     * * formats handled by RxNorm normalization:
     *   - 6-4-2
     *   - 6-4-1
     *   - 6-3-2
     *   - 6-3-1
     *   - 5-4-2
     *   - 5-4-1
     *   - 5-3-2
     *   - 4-4-2
     *   - 4-4
     * * additionally, RxNorm normalization applies the following rules
     *   - consider invalid NDCs with alpha characters
     *   - remove the leading 0 ONLY from 12-digit NDCs from VANDF starting with a 0
     *   - replace * with 0 (regardless of source, but intended for MTHFDA)
     * ----------
     *
     * </pre>
     */

    String ndc11 = null;
    // replace * with 0 (regardless of source, but intended for MTHFDA)
    linputString = linputString.replace('*', '0');

    int hyphenCt = StringUtils.countMatches(linputString, "-");
    String[] segments = new String[] {};

    if (hyphenCt == 2) {
      segments = linputString.split("-");

      String segment1 = segments[0];
      String segment2 = segments[1];
      String segment3 = segments[2];

      if (linputString.matches("^[A-Z\\d{6}]\\-\\d{4}\\-\\d{2}$")
          || linputString.matches("^\\d{6}\\-\\d{4}\\-\\d{1}$")
          || linputString.matches("^\\d{6}\\-\\d{3}\\-\\d{2}$")
          || linputString.matches("^\\d{6}\\-\\d{3}\\-\\d{1}$")
          || linputString.matches("^\\d{5}\\-\\d{4}\\-\\d{2}$")
          || linputString.matches("^\\d{5}\\-\\d{4}\\-\\d{1}$")
          || linputString.matches("^\\d{5}\\-\\d{3}\\-\\d{2}$")
          || linputString.matches("^\\d{4}\\-\\d{4}\\-\\d{2}$")) {
        // # valid hyphenated NDC format (RxNorm)

        // # *** make segment 1 have 5 digits
        if (segment1.length() == 6) {
          // # remove the first digit
          segment1 = segment1.substring(1);
        } else if (segment1.length() == 4) {
          // # pad segment with 0 to the left (to 5 digits)
          segment1 = "0" + segment1;
        }
        // # *** make segment 2 have 4 digits
        if (segment2.length() == 3) {
          // # pad segment with 0 to the left (to 4 digits)
          segment2 = "0" + segment2;
        }
        // # *** make segment 3 have 2 digits
        if (segment3.length() == 1) {
          // # pad segment with 0 to the left (to 2 digits)
          segment3 = "0" + segment3;
        }
        // # reassemble the segments without hyphens
        ndc11 = segment1 + segment2 + segment3;
      } else {
        // # invalid hyphenated NDC format
        Logger.getLogger(getClass())
            .warn("invalid hyphenated NDC format " + linputString);
        return new ArrayList<>();
      }
      // Comment out for now, because splsetid breaks this condition
      /*
       * } else if (hyphenCt > 2) { throw new LocalException(
       * "NDC code has invalid hyphenated NDC format.");
       */
    } else if (linputString.length() == 36) {
      ndc11 = linputString;
    } else if (linputString.length() == 11) {
      ndc11 = linputString;
    } else if (linputString.length() == 12) {
      ndc11 = linputString.substring(1);
    } else {
      Logger.getLogger(getClass())
          .warn("NDC code has an invalid format - " + linputString);
      return new ArrayList<>();
    }

    // Simply return data passed in for this "naive" case. As such, the
    // score is
    // set to '1'.
    List<ScoredResult> results = new ArrayList<ScoredResult>();

    // Ensure that input is valid.
    if (ndc11 != null) {
      Logger.getLogger(getClass()).debug("    normalized = " + ndc11);
      ScoredResult r = new ScoredResultJpa();
      r.setValue(ndc11);
      r.setScore(1);
      results.add(r);
    }

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

    try {
      quality = Float.parseFloat(p.getProperty("quality"));
      if (quality < 0 || quality > 1) {
        throw new Exception();
      }
    } catch (Exception e) {
      throw new Exception(
          "quality property must be a float value between 0 and 1");
    }
  }

  /* see superclass */
  @Override
  public float getQuality() {
    return quality;
  }

  /* see superclass */
  @Override
  public boolean accepts(DataContext inputContext) throws Exception {
    return inputContext != null && "NDC".equals(inputContext.getTerminology())
        && inputContext.getType() == DataContextType.CODE;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }
}
