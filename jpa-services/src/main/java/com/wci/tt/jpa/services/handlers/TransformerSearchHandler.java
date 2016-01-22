/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.hibernate.search.jpa.FullTextQuery;

import com.wci.umls.server.helpers.HasLastModified;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.PfscParameter;
import com.wci.umls.server.jpa.services.helper.IndexUtility;
import com.wci.umls.server.services.handlers.SearchHandler;

/**
 * Default implementation of {@link SearchHandler}. This provides an algorithm
 * to aide in lucene searches.
 */
public class TransformerSearchHandler implements SearchHandler {

  /** The score map. */
  private Map<Long, Float> scoreMap = new HashMap<>();

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a - no properties
  }

  /* see superclass */
  @Override
  public <T extends HasLastModified> List<T> getQueryResults(String terminology,
    String version, String branch, String query, String literalField,
    Class<?> fieldNamesKey, Class<T> clazz, PfsParameter pfs, int[] totalCt,
    EntityManager manager) throws Exception {

    // Build an escaped form of the query with wrapped quotes removed
    // This will be used for literal/exact searching
    String escapedQuery = query;
    if (query.startsWith("\"") && query.endsWith("\"")) {
      escapedQuery = escapedQuery.substring(1);
      escapedQuery = escapedQuery.substring(0, query.length() - 2);
    }
    escapedQuery = "\"" + QueryParserBase.escape(escapedQuery) + "\"";

    // Build a combined query with an OR between query typed and exact match
    String combinedQuery = null;
    // For a fielded query search, simply perform the search as written
    // no need for modifications. Also if no literal search field is supplied
    if (query.isEmpty() || query.contains(":") || literalField == null) {
      combinedQuery = query;
    } else {
      combinedQuery = (query.isEmpty() ? "" : query + " OR ") + literalField
          + ":" + escapedQuery + "^20.0";
    }

    // Add terminology conditions
    StringBuilder terminologyClause = new StringBuilder();
    if (terminology != null && !terminology.equals("") && version != null
        && !version.equals("")) {
      terminologyClause.append(
          " AND terminology:" + terminology + " AND version:" + version);
    }

    // Assemble query
    StringBuilder finalQuery = new StringBuilder();
    if (query.isEmpty()) {
      // Just use PFS and skip the leading "AND"
      finalQuery.append(terminologyClause.substring(5));
    } else if (combinedQuery.contains(" OR ")) {
      // Use parens
      finalQuery.append("(").append(combinedQuery).append(")")
          .append(terminologyClause);
    } else {
      // Don't use parens
      finalQuery.append(combinedQuery).append(terminologyClause);

    }
    FullTextQuery fullTextQuery = null;
    try {
      Logger.getLogger(getClass()).debug("query = " + finalQuery);
      fullTextQuery = IndexUtility.applyPfsToLuceneQuery(clazz, fieldNamesKey,
          finalQuery.toString(), pfs, manager);
    } catch (ParseException e) {
      // If there's a parse exception, try the literal query
      Logger.getLogger(getClass()).debug("query = " + finalQuery);
      fullTextQuery = IndexUtility.applyPfsToLuceneQuery(clazz, fieldNamesKey,
          escapedQuery + terminologyClause, pfs, manager);
    }

    // Apply paging and sorting parameters for the PFSC case
    // This is needed for the combined search with "search criteria"
    if (!(pfs instanceof PfscParameter)) {
      totalCt[0] = fullTextQuery.getResultSize();
    } else if (pfs instanceof PfscParameter
        && ((PfscParameter) pfs).getSearchCriteria().isEmpty()) {
      // Get result size if we know it.
      totalCt[0] = fullTextQuery.getResultSize();
    } else {
      // If with search criteria, save paging
      fullTextQuery.setFirstResult(0);
      fullTextQuery.setMaxResults(Integer.MAX_VALUE);
      totalCt[0] = fullTextQuery.getResultSize();
    }

    // Use this code to see the actual score values
    fullTextQuery.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);
    final List<T> classes = new ArrayList<>();
    @SuppressWarnings("unchecked")
    final List<Object[]> results = fullTextQuery.getResultList();
    for (final Object[] result : results) {
      Object score = result[0];
      @SuppressWarnings("unchecked")
      T t = (T) result[1];
      classes.add(t);
      scoreMap.put(t.getId(), Float.parseFloat(score.toString()));
    }

    return classes;

  }

  /* see superclass */
  @Override
  public Map<Long, Float> getScoreMap() {
    return scoreMap;
  }

  /* see superclass */
  @Override
  public String getName() {
    return "Transformer search handler";
  }
}