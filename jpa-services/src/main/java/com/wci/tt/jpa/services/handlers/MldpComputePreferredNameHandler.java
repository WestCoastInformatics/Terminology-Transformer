/*
 *    Copyright 2015 West Coast Informatics, LLC
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

import com.wci.umls.server.helpers.PrecedenceList;
import com.wci.umls.server.jpa.AbstractConfigurable;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Relationship;
import com.wci.umls.server.services.handlers.ComputePreferredNameHandler;

/**
 * Implementation {@link ComputePreferredNameHandler} for data with term-type
 * ordering.
 */
public class MldpComputePreferredNameHandler extends AbstractConfigurable
    implements ComputePreferredNameHandler {

  /** The tty rank map. */
  private Map<Long, Map<String, String>> ttyRankMap = new HashMap<>();

  /** The terminology rank map. */
  private Map<Long, Map<String, String>> terminologyRankMap = new HashMap<>();

  /**
   * Instantiates an empty {@link MldpComputePreferredNameHandler}.
   */
  public MldpComputePreferredNameHandler() {
    // n/a
  }

  /* see superclass */
  @Override
  public String computePreferredName(final Collection<Atom> atoms,
    final PrecedenceList list) throws Exception {
    String name = null;
    
    // immediately return PT, otherwise use first non-PT value
    for (Atom atom : atoms) {
      if (atom.getTermType().equals("PT")) {
        return atom.getName();
      }
      if (name == null) {
        name = atom.getName();
      }
    }
    return name;
  }

  /* see superclass */
  @Override
  public List<Atom> sortAtoms(Collection<Atom> atoms, PrecedenceList list)
    throws Exception {

    final List<Atom> sortedAtoms = new ArrayList<>(atoms);
    Collections.sort(sortedAtoms, new Comparator<Atom>() {
      @Override
      public int compare(Atom a1, Atom a2) {
        if ("PT".equals(a1.getTermType())) {
          return -1;
        }
        if ("PT".equals(a2.getTermType())) {
          return 1;
        }
        return a1.getTermType().compareTo(a2.getTermType());
      }
    });
    return sortedAtoms;
  }

  /**
   * Returns the rank.
   *
   * @param atom the atom
   * @param list the list
   * @return the rank
   * @throws Exception the exception
   */
  public String getRank(final Atom atom, final PrecedenceList list)
    throws Exception {

    return null;
  }

  /**
   * Returns the rank for the relationship.
   *
   * @param <T> the
   * @param relationship the rel
   * @param list the list
   * @return the rank
   * @throws Exception the exception
   */
  public <T extends Relationship<?, ?>> String getRank(T relationship,
    PrecedenceList list) throws Exception {
    return null;
  }

  /**
   * Cache list.
   *
   * @param list the list
   * @throws Exception the exception
   */
  public void cacheList(PrecedenceList list) throws Exception {

   
  }

  /* see superclass */
  @Override
  public String getName() {
    return "MLDP Compute Preferred Name Handler";
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public <T extends Relationship<?, ?>> List<T> sortRelationships(
    Collection<T> rels, PrecedenceList list) throws Exception {
    return null;
  }

}
