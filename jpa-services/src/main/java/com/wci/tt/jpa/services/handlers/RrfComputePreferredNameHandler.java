/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.wci.tt.helpers.KeyValuePair;
import com.wci.tt.helpers.PrecedenceList;
import com.wci.tt.jpa.services.MetadataServiceJpa;
import com.wci.tt.model.content.Atom;
import com.wci.tt.services.MetadataService;
import com.wci.tt.services.handlers.ComputePreferredNameHandler;

/**
 * Implementation {@link ComputePreferredNameHandler} for data with term-type
 * ordering.
 */
public class RrfComputePreferredNameHandler implements
    ComputePreferredNameHandler {

  /** The UMLS terminology. */
  public String umlsTerminology;

  /** The UMLS version. */
  public String umlsVersion;

  /** The list. */
  private PrecedenceList list = null;

  /** The tty rank map. */
  private Map<String, String> ttyRankMap = new HashMap<>();

  /**
   * Instantiates an empty {@link RrfComputePreferredNameHandler}.
   */
  public RrfComputePreferredNameHandler() {
    // n/a
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) {
    umlsTerminology = p.getProperty("terminology");
    umlsVersion = p.getProperty("version");
  }

  /* see superclass */
  @Override
  public String computePreferredName(Collection<Atom> atoms) throws Exception {
    
    // Ignore computation, simply get name
    // TODO FIgure out how we want to rank things
    return atoms.iterator().next().getName();
    
    /**
    // Use ranking algorithm from MetamorphoSys
    // [termgroupRank][lrr][inverse SUI][inverse AUI]
    // LRR isn't available here so just don't worry about it.
    String maxRank = "";
    Atom maxAtom = null;
    for (Atom atom : atoms) {
      String rank = getRank(atom);
      if (maxAtom == null) {
        maxAtom = atom;
        maxRank = rank;
      } else if (rank.compareTo(maxRank) > 0) {
        maxAtom = atom;
        maxRank = rank;
      }
    }

    if (maxAtom != null) {
      return maxAtom.getName();
    }
    return null;
    
    */
  }

  /* see superclass */
  @Override
  public List<Atom> sortByPreference(Collection<Atom> atoms) throws Exception {

    List<Atom> sortedAtoms = new ArrayList<>(atoms);
    // Get each atom rank
    final Map<Atom, String> atomRanks = new HashMap<>();
    for (Atom atom : atoms) {
      final String rank = getRank(atom);
      atomRanks.put(atom, rank);
    }
    // Sort by atom rank - this works because atom ranks are designed to be
    // fixed-length strings that are directly comparable
    Collections.sort(sortedAtoms, new Comparator<Atom>() {
      @Override
      public int compare(Atom o1, Atom o2) {
        return atomRanks.get(o2).compareTo(atomRanks.get(o1));
      }
    });
    return sortedAtoms;
  }

  /**
   * Returns the rank.
   *
   * @param atom the atom
   * @return the rank
   * @throws Exception the exception
   */
  protected String getRank(Atom atom) throws Exception {
    if (list == null) {
      // If the "umlsTerminology" and version are not set, use info from the
      // atom
      // This will assume that all term types are present.
      // IF we encounter an exception (because say RRF-single loader was used
      // for >1 terminology)
      // this will fail later.
      if (umlsTerminology == null && umlsVersion == null) {
        umlsTerminology = atom.getTerminology();
        umlsVersion = atom.getVersion();
      }
      cacheList();
    }
    String rank = null;
    if (!ttyRankMap.containsKey(atom.getTerminology() + "/"
        + atom.getTermType())) {
      Logger.getLogger(getClass()).error("  terminology = " + atom.getTerminology());
      Logger.getLogger(getClass()).error("  termType = " + atom.getTermType());
      // See caveats in the cacheList call above for more info
      throw new Exception(
          "Atom terminology/type are not present in the default precedence list.");
    }
    if (atom.getStringClassId() != null && !atom.getStringClassId().isEmpty()) {
      rank =
          (atom.isObsolete() ? 0 : 1)
              + (atom.isSuppressible() ? 0 : 1)
              + ttyRankMap
                  .get(atom.getTerminology() + "/" + atom.getTermType())
              + (10000000000L - Long.parseLong(atom.getStringClassId()
                  .substring(1))) + (100000000000L - atom.getId());
    } else {
      rank =
          (atom.isObsolete() ? 0 : 1)
              + (atom.isSuppressible() ? 0 : 1)
              + ttyRankMap
                  .get(atom.getTerminology() + "/" + atom.getTermType());
    }
    return rank;
  }

  /**
   * Cache list.
   *
   * @throws Exception the exception
   */
  private void cacheList() throws Exception {
    MetadataService service = new MetadataServiceJpa();
    list = service.getDefaultPrecedenceList(umlsTerminology, umlsVersion);
    service.close();
    List<KeyValuePair> list2 = list.getPrecedence().getKeyValuePairs();
    int ct = 1;
    for (int i = list2.size() - 1; i >= 0; i--) {
      String padded = "0000" + ct++;
      padded = padded.substring(padded.length() - 4);
      final KeyValuePair pair = list2.get(i);
      ttyRankMap.put(pair.getKey() + "/" + pair.getValue(), padded);
    }
    Logger.getLogger(getClass()).info(
        "  default precedence list = " + ttyRankMap);
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

}
