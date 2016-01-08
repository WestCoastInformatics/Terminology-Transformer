/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextQuery;

import com.wci.tt.helpers.FieldedStringTokenizer;
import com.wci.tt.helpers.HasLastModified;
import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.PfscParameter;
import com.wci.tt.jpa.services.helper.IndexUtility;
import com.wci.tt.services.handlers.SearchHandler;

/**
 * Default implementation of {@link SearchHandler}. This provides an algorithm
 * to aide in lucene searches.
 */
public class DefaultSearchHandler implements SearchHandler {

  /** The acronym expansion map. */
  private Map<String, Set<String>> acronymExpansionMap = new HashMap<>();

  /** The spell checker. */
  private SpellChecker spellChecker = null;

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {

    // Initialize acronyms map
    if (p.containsKey("acronymsFile")) {
      BufferedReader in =
          new BufferedReader(new FileReader(new File(
              p.getProperty("acronymsFile"))));
      String line;
      while ((line = in.readLine()) != null) {
        String[] tokens = FieldedStringTokenizer.split(line, "\t");
        if (!acronymExpansionMap.containsKey(tokens[0])) {
          acronymExpansionMap.put(tokens[0], new HashSet<String>(2));
        }
        acronymExpansionMap.get(tokens[0]).add(tokens[1]);
      }
      in.close();
    } else {
      throw new Exception("Required property acronymsFile not present.");
    }

    // Initialize spell checker
    if (p.containsKey("spellingFile") && p.containsKey("spellingIndex")) {
      // expect properties to have "spellingFile" and "spellingIndex"
      File dir = new File(p.getProperty("spellingIndex"));
      Directory directory = FSDirectory.open(dir);
      spellChecker =
          new SpellChecker(directory, new LuceneLevenshteinDistance());
      IndexWriterConfig indexWriterConfig =
          new IndexWriterConfig(Version.LATEST, new WhitespaceAnalyzer());
      spellChecker.indexDictionary(
          new PlainTextDictionary(new File(p.getProperty("spellingFile"))),
          indexWriterConfig, false);

    } else {
      throw new Exception(
          "Required property spellingFile or spellingIndex not present.");
    }
  }

  /* see superclass */
  @Override
  public <T extends HasLastModified> List<T> getQueryResults(
    String terminology, String version, String branch, String query,
    String literalField, Class<?> fieldNamesKey, Class<T> clazz,
    PfsParameter pfs, int[] totalCt, EntityManager manager) throws Exception {

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
      combinedQuery =
          (query.isEmpty() ? "" : query + " OR ") + literalField + ":"
              + escapedQuery + "^20.0";
      // create an exact expansion entry. i.e. if the search term exactly
      // matches something in the acronyms file, then use additional "OR"
      // clauses
      if (acronymExpansionMap.containsKey(query)) {
        for (String expansion : acronymExpansionMap.get(query)) {
          combinedQuery +=
              " OR " + literalField + ":\"" + expansion + "\"" + "^20.0";
        }
      }
    }

    // Check for spelling mistakes (if not a fielded search)
    if (!query.contains(":") && !query.isEmpty()) {
      boolean flag = false;
      StringBuilder correctedQuery = new StringBuilder();
      for (String token : FieldedStringTokenizer.split(query,
          " \t-({[)}]_!@#%&*\\:;\"',.?/~+=|<>$`^")) {
        if (token.length() == 0) {
          continue;
        }
        if (spellChecker.exist(token.toLowerCase())) {
          if (correctedQuery.length() != 0) {
            correctedQuery.append(" ");
          }
          correctedQuery.append(token);
        } else {
          String[] suggestions =
              spellChecker.suggestSimilar(token.toLowerCase(), 5, .8f);
          if (suggestions.length > 0) {
            flag = true;
            if (correctedQuery.length() != 0) {
              correctedQuery.append(" ");
            }
            correctedQuery
                .append(FieldedStringTokenizer.join(suggestions, " "));
          }
        }
      }
      if (flag) {
        combinedQuery +=
            " OR " + literalField + ":\"" + correctedQuery + "\"" + "^10.0";
      }
    }

    // Add terminology conditions
    StringBuilder terminologyClause = new StringBuilder();
    if (terminology != null && !terminology.equals("") && version != null
        && !version.equals("")) {
      terminologyClause.append(" AND terminology:" + terminology
          + " AND version:" + version);
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
      System.out.println("  query1 = " + finalQuery);
      Logger.getLogger(getClass()).debug("query = " + finalQuery);
      fullTextQuery =
          IndexUtility.applyPfsToLuceneQuery(clazz, fieldNamesKey,
              finalQuery.toString(), pfs, manager);
    } catch (ParseException e) {
      // If there's a parse exception, try the literal query
      System.out.println("  query2 = " + finalQuery);
      Logger.getLogger(getClass()).debug("query = " + finalQuery);
      fullTextQuery =
          IndexUtility.applyPfsToLuceneQuery(clazz, fieldNamesKey, escapedQuery
              + terminologyClause, pfs, manager);
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

    // Only look to other algorithms if this is NOT a fielded query
    // and the query exists
    if (query != null && !query.isEmpty() && !query.contains(":")) {

      // If at this point there are zero results,
      // Run the query through acronym expansion
      if (totalCt[0] == 0) {
        // use wordInd tokenization
        String[] tokens =
            FieldedStringTokenizer.split(query,
                " \t-({[)}]_!@#%&*\\:;\"',.?/~+=|<>$`^");
        StringBuilder newQuery = new StringBuilder();
        boolean found = false;
        for (String token : tokens) {
          if (newQuery.length() != 0) {
            newQuery.append(" ");
          }
          // replace with acronym or keep the same
          if (acronymExpansionMap.containsKey(token.toUpperCase())) {
            found = true;
            newQuery.append(FieldedStringTokenizer.join(new ArrayList<>(
                acronymExpansionMap.get(token)), " "));
          } else {
            newQuery.append(token);
          }
        }
        // Try the query again (if at least one expansion was found)
        if (found) {
          System.out.println("  query3 = " + newQuery.toString()
              + terminologyClause);
          fullTextQuery =
              IndexUtility.applyPfsToLuceneQuery(clazz, fieldNamesKey,
                  newQuery.toString() + terminologyClause, pfs, manager);
          totalCt[0] = fullTextQuery.getResultSize();
        }
      }

      // If at this point there are zero results,
      // Run the query through spelling correction
      if (totalCt[0] == 0) {
        // use wordInd tokenization
        String[] tokens =
            FieldedStringTokenizer.split(query,
                " \t-({[)}]_!@#%&*\\:;\"',.?/~+=|<>$`^");
        StringBuilder newQuery = new StringBuilder();
        newQuery.append("(");
        boolean found = false;
        for (String token : tokens) {
          if (newQuery.length() != 0) {
            newQuery.append(" ");
          }
          if (spellChecker.exist(token.toLowerCase())) {
            newQuery.append(token);
          } else if (!token.isEmpty()) {
            String[] suggestions =
                spellChecker.suggestSimilar(token.toLowerCase(), 5, .8f);
            found = suggestions.length > 0;
            newQuery.append(FieldedStringTokenizer.join(suggestions, " "));
          }
        }
        newQuery.append(")");

        // Try the query again (if replacement found)
        if (found) {
          System.out.println("  query4 = " + newQuery.toString()
              + terminologyClause);
          fullTextQuery =
              IndexUtility.applyPfsToLuceneQuery(clazz, fieldNamesKey,
                  newQuery.toString() + terminologyClause, pfs, manager);
          totalCt[0] = fullTextQuery.getResultSize();
        }
      }

      // TODO: if still zero, do wildcard search at the end of each term of the
      // original query
      // e.g. a* b* c*
      if (totalCt[0] == 0) {
        // use wordInd tokenization
        String[] tokens =
            FieldedStringTokenizer.split(query,
                " \t-({[)}]_!@#%&*\\:;\"',.?/~+=|<>$`^");
        StringBuilder newQuery = new StringBuilder();
        for (String token : tokens) {
          if (newQuery.length() != 0) {
            newQuery.append(" ");
          }
          if (token.length() > 0) {
            newQuery.append(token).append("*");
          }
        }
        // Try the query again
        System.out.println("  query5 = " + newQuery.toString()
            + terminologyClause);
        fullTextQuery =
            IndexUtility.applyPfsToLuceneQuery(clazz, fieldNamesKey,
                newQuery.toString() + terminologyClause, pfs, manager);
        totalCt[0] = fullTextQuery.getResultSize();

      }
    
    }

    // execute the query
    @SuppressWarnings("unchecked")
    List<T> classes = fullTextQuery.getResultList();

    // Use this code to see the actual score values
    // fullTextQuery.setProjection(FullTextQuery.SCORE, FullTextQuery.ID);
    // List<T> classes = new ArrayList<>();
    // List<Object[]> obj = fullTextQuery.getResultList();
    // for (Object[] objArray : obj) {
    // Object score = objArray[0];
    // long id = (Long)objArray[1];
    // T t = getComponent( id, clazz);
    // classes.add(t);
    // Logger.getLogger(getClass()).info(t.getName() + " = " + score);
    // }

    return classes;

  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }
}
