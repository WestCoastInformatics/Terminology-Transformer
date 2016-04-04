/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.hk2.utilities.reflection.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.services.handlers.NormalizerHandler;

/**
 * NDC implementation of {@link NormalizerHandler}.
 * 
 * This class demonstrates a "naive" implementation of the Normalizer.
 * 
 * Class created to prove that supporting functionality works, not to provide
 * meaningful results.
 */
public class NdcNormalizer extends AbstractNormalizer
    implements NormalizerHandler {

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
  public List<ScoredResult> normalize(String inputStr, DataContext context)
    throws Exception {
    // Normalize the NDC code inputStr
    // reformat any 3-segment NDC into an 11-digit NDC without hyphens
    // the NDC11 format (no hyphens) is known as the HIPPA NDC format
    /*
     * convert into a 5-4-2 configuration (without hyphens) by padding to the
     * left with zeros # * official NDC10 formats: # - 4-4-2 # - 5-3-2 # - 5-4-1
     * # * formats handled by RxNorm normalization: # - 6-4-2 # - 6-4-1 # -
     * 6-3-2 # - 6-3-1 # - 5-4-2 # - 5-4-1 # - 5-3-2 # - 4-4-2 # * additionally,
     * RxNorm normalization applies the following rules # - consider invalid
     * NDCs with alpha characters # - remove the leading 0 ONLY from 12-digit
     * NDCs from VANDF starting with a 0 # - replace * with 0 (regardless of
     * source, but intended for MTHFDA)
     */
    String ndc11 = "";
    // replace * with 0 (regardless of source, but intended for MTHFDA)
    inputStr = inputStr.replace('*', '0');

    int hyphenCt = StringUtils.countMatches(inputStr, "-");
    String[] segments = new String[] {};
    if (hyphenCt == 2) {
      segments = inputStr.split("-");

      String segment1 = segments[0];
      String segment2 = segments[1];
      String segment3 = segments[2];

      if (inputStr.matches("\\/^\\d{6}\\-\\d{4}\\-\\d{2}$\\/o")
          || inputStr.matches("\\/^\\d{6}\\-\\d{4}\\-\\d{1}$\\/o")
          || inputStr.matches("\\/^\\d{6}\\-\\d{3}\\-\\d{2}$\\/o")
          || inputStr.matches("\\/^\\d{6}\\-\\d{3}\\-\\d{1}$\\/o")
          || inputStr.matches("\\/^\\d{5}\\-\\d{4}\\-\\d{2}$\\/o")
          || inputStr.matches("\\/^\\d{5}\\-\\d{4}\\-\\d{1}$\\/o")
          || inputStr.matches("\\/^\\d{5}\\-\\d{3}\\-\\d{2}$\\/o")
          || inputStr.matches("\\/^\\d{4}\\-\\d{4}\\-\\d{2}$\\/o")
      ) {
        // # valid hyphenated NDC format (RxNorm)
        System.out.println("format = %d-%d-%d\n" + segment1.length()
            + segment2.length() + segment3.length());

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
        System.out.println("format = %d-%d-%d\n" + segment1.length()
            + segment2.length() + segment3.length());
        System.out.println("invalid hyphenated NDC format = $format");
      }
    } else if (hyphenCt > 2) {
      System.out.println("invalid hyphenated NDC format = %s");
    } else if (inputStr.length() == 11) {
      ndc11 = inputStr;
    } else if (inputStr.length() == 12) {
      ndc11 = inputStr.substring(1);
    }

    // Simply return data passed in for this "naive" case. As such, the
    // score is
    // set to '1'.
    List<ScoredResult> results = new ArrayList<ScoredResult>();

    // Ensure that input is valid.
    if (!ndc11.equals("")) {
      ScoredResult r = new ScoredResultJpa();
      r.setValue(ndc11);
      r.setScore(1);
      results.add(r);
    }

    return results;
  }

  /* see superclass */
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
    return true;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }
}
