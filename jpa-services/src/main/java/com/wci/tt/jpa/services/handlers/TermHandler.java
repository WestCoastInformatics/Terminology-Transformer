/*
 *    Copyright 2016 Fitgate Inc.
 */
package com.wci.tt.jpa.services.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.wci.tt.services.handlers.AbbreviationHandler;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.AbstractConfigurable;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueListJpa;
import com.wci.umls.server.model.workflow.WorkflowStatus;
import com.wci.umls.server.services.ProjectService;
import com.wci.umls.server.services.helpers.PushBackReader;

/**
 * Default implementation of {@link AbbreviationHandler} for handling
 * terms.
 */
public class TermHandler extends AbstractConfigurable
    implements AbbreviationHandler {

  /** The service. */
  private ProjectService service = null;

  /** Whether to check review status on import */
  private boolean reviewFlag = true;

  /* see superclass */
  @Override
  public String getName() {
    return "Default term handler";
  }

  /* see superclass */
  @Override
  public void setService(ProjectService service) {
    this.service = service;
  }

  @Override
  public void setReviewFlag(boolean reviewFlag) {
    this.reviewFlag = reviewFlag;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // do nothing
  }
  
  // the original transaction-per-operation state of the service
  private boolean origTPO;

  /**
   * Recurse helper function to compute potential reviews in terms.
   *
   * @param abbrsToCheck the abbrs to check
   * @param computedConflicts the computed conflicts
   * @return the review for terms helper
   * @throws Exception the exception
   */
  private List<TypeKeyValue> getReviewForAbbreviationsHelper(String rootType,
    List<TypeKeyValue> abbrsToCheck, List<TypeKeyValue> computedConflicts)
    throws Exception {

    // if computed reviews is null, instantiate set from the terms to
    // check
    List<TypeKeyValue> lcomputedConflicts = computedConflicts;
    if (lcomputedConflicts == null) {
      lcomputedConflicts = new ArrayList<>(abbrsToCheck);
    }

    // if no further terms to process, return the passed reviews list
    if (abbrsToCheck == null || abbrsToCheck.size() == 0) {
      // quality check -- remove any conflicts with null or strictly whitespace
      // values
      final List<TypeKeyValue> finalResults = new ArrayList<>();
      for (final TypeKeyValue abbr : lcomputedConflicts) {
        if (abbr.getValue() != null && !abbr.getValue().trim().isEmpty()) {
          finalResults.add(abbr);
        }
      }
      return finalResults;
    }

    // construct query from all keys and values
    // NOTE: Could be optimized for values already searched, but don't expect
    // this to be very big
    final List<String> clauses = new ArrayList<>();
    for (TypeKeyValue abbr : abbrsToCheck) {
      if (abbr.getType().equals(rootType)) {
        // clause: key OR value matches for term within root type
        clauses.add("(type:\"" + rootType + "\" AND NOT id:" + abbr.getId()
            + " AND (keySort:\"" + abbr.getKey() + "\" OR value:\""
            + abbr.getValue() + "\"))");

       // clause: value matches for term not in root type
        clauses.add("(value:\""
            + abbr.getValue() + "\" AND NOT type:\"" + rootType + "\")");
      }
    }
    
    
    // if no query, no more to search, return local conflicts
    if (clauses.size() == 0) {
      return lcomputedConflicts;
    }

    String query = "";
    for (int i = 0; i < clauses.size(); i++) {
      query += clauses.get(i) + (i == clauses.size() - 1 ? "" : " OR ");
    }
 
    
    // otherwise continue
    PfsParameter pfs = new PfsParameterJpa();
    pfs.setMaxResults(-1);
    pfs.setStartIndex(-1);

    // get all matching results
    final List<TypeKeyValue> results =
        service.findTypeKeyValuesForQuery(query, pfs).getObjects();

    // compute new results from results and reviews
    final List<TypeKeyValue> newResults = new ArrayList<>();

    // check each result against already computed conflicts
    // to update computed conflicts and determine which
    // results are new
    // NOTE: Check by id instead of set operations to catch duplicates
    for (TypeKeyValue result : results) {
      boolean exists = false;
      for (TypeKeyValue conflict : lcomputedConflicts) {
        if (result.getId().equals(conflict.getId())) {
          exists = true;
        }
      }
      if (!exists) {
        newResults.add(result);
        lcomputedConflicts.add(result);
      }
    }

    // call with new results and updated reviews
    return getReviewForAbbreviationsHelper(rootType, newResults,
        lcomputedConflicts);
  }

  /* see superclass */
  @Override
  public TypeKeyValueList getReviewForAbbreviation(TypeKeyValue abbr)
    throws Exception {
    List<TypeKeyValue> reviews = getReviewForAbbreviationsHelper(abbr.getType(),
        new ArrayList<>(Arrays.asList(abbr)), null);
    TypeKeyValueList list = new TypeKeyValueListJpa();
    list.setTotalCount(reviews.size());
    list.setObjects(reviews);
    return list;
  }

  /* see superclass */
  @Override
  public TypeKeyValueList getReviewForAbbreviations(List<TypeKeyValue> abbrList)
    throws Exception {
    List<TypeKeyValue> reviews = getReviewForAbbreviationsHelper(
        abbrList.get(0).getType(), abbrList, null);
    TypeKeyValueList list = new TypeKeyValueListJpa();
    list.setTotalCount(reviews.size());
    list.setObjects(reviews);
    return list;
  }

  /* see superclass */
  @Override
  public ValidationResult validateAbbreviationFile(String terminology,
    InputStream inFile) throws Exception {
    Logger.getLogger(getClass()).info("Validate term file");
    return importHelper(terminology, inFile, false);
  }

  /* see superclass */
  @Override
  public ValidationResult importAbbreviationFile(String terminology,
    InputStream inFile) throws Exception {
    Logger.getLogger(getClass()).info("Import term file");
    return this.importHelper(terminology, inFile, true);
  }

  /* see superclass */
  @Override
  public boolean isHeaderLine(String line) {
    return line != null && (line.toLowerCase().startsWith("term")
        || line.toLowerCase().startsWith("lowWord"));
  }

  /**
   * Import helper.
   *
   * @param type the type
   * @param in the in
   * @param executeImport the execute import
   * @return the validation result
   * @throws Exception the exception
   */
  private ValidationResult importHelper(String terminology, InputStream in,
    boolean executeImport) throws Exception {
    ValidationResult result = new ValidationResultJpa();
    PushBackReader pbr = null;
    try {

      List<TypeKeyValue> newAbbrs = new ArrayList<>();

      // Read from input stream
      Reader reader = new InputStreamReader(in, "UTF-8");
      pbr = new PushBackReader(reader);
      int lineCt = 0;
      int commentCt = 0;
      int ignoredCt = 0;
      int addedCt = 0;
      int toAddCt = 0;
      int errorCt = 0;
      int dupPairCt = 0;
      int dupKeyCt = 0;
      int dupValCt = 0;
      PfsParameter pfs = new PfsParameterJpa();

      String line = pbr.readLine();

      // check for empty contents
      if (line == null) {
        return result;
      }

      // if execute, prepare services and existing
      if (executeImport) {
        origTPO = service.getTransactionPerOperation();
        service.setTransactionPerOperation(false);
        service.beginTransaction();
      }

      // skip header line if present
      if (isHeaderLine(line)) {
        lineCt++;
        result.getComments().add("Skipped header line: " + line);
        line = pbr.readLine();
      }

      // start processing
      do {
        lineCt++;
        line = line.replace("[^S\t ", "");
        final String fields[] = line.split("\t");

        // skip comment lines
        if (fields.length > 0 && fields[0] != null
            && fields[0].startsWith("##")) {
          commentCt++;
          continue;
        }

      

        // CHECK: Must be fields
        if (fields.length == 0) {
          result.getErrors().add("Line " + lineCt + ": Empty line");
        }

        // CHECK: Must be no more than one fields
        else if (fields.length > 1) {
          errorCt++;
          if (!executeImport) {
            String fieldsStr = "";
            for (int i = 0; i < fields.length; i++) {
              fieldsStr += "[" + fields[i] + "] ";
            }
            result.getErrors()
                .add("Line " + lineCt + ": Expected one fields but found " + fields.length + ": " + fieldsStr);
          }
        }

        // CHECK: Key must be non-null and non-empty
        else if (fields[0] == null || fields[0].trim().isEmpty()) {
          result.getErrors()
              .add("Line " + lineCt + ": Key with null/empty value found");
        }
        
        // if passes checks, add the type key value
        else {
          final TypeKeyValue typeKeyValue = new TypeKeyValueJpa(
              getAbbrType(terminology), fields[0], null);
          service.addTypeKeyValue(typeKeyValue);
          addedCt++;
        }

       

        
      } while ((line = pbr.readLine()) != null);

      // after all terms loaded and if review indicated
      // apply workflow status and commit
      // NOTE: Must commit first to allow lucene searching
      if (executeImport) {

        Logger.getLogger(getClass())
            .info("Commiting " + addedCt + " new terms...");

        service.commit();

        Logger.getLogger(getClass()).info("  Done.");

        if (reviewFlag) {
          // do nothing as yet
        }
      }

      // aggregate results for validation
      if (!executeImport) {
        if (toAddCt == 0) {
          result.getComments().add("No terms will be added");
        }
        if (toAddCt > 0) {
          result.getComments().add(toAddCt + " terms will be added");
        }
        if (ignoredCt != 0) {
          result.getComments().add(ignoredCt + " term "
              + (ignoredCt == 1 ? "" : "s") + " marked as ignored");
        }
        if (commentCt > 0) {
          result.getComments()
              .add(commentCt + " comment lines will be skipped");
        }
        if (errorCt > 0) {
          result.getErrors().add(errorCt + " lines in error");
        }
        if (dupPairCt > 0) {
          result.getWarnings().add(dupPairCt + " duplicates");
        }
        if (dupKeyCt > 0) {
          result.getWarnings()
              .add(dupKeyCt + " existing terms with new expansion");
        }
        if (dupValCt > 0) {
          result.getWarnings()
              .add(dupValCt + " existing expansions with new term");
        }
      }

      // otherwise, aggregate results for import
      else {
        if (addedCt == 0) {
          result.getWarnings().add("No terms added.");
        } else {
          result.getComments().add(addedCt + " term "
              + (addedCt == 1 ? "" : "s") + " added");
        }
        if (ignoredCt != 0) {
          result.getComments().add(ignoredCt + " term "
              + (ignoredCt == 1 ? "" : "s") + " marked as ignored");
        }
        if (dupPairCt != 0) {
          result.getWarnings().add(
              dupPairCt + " duplicate term/expansion pairs skipped");
        }
        if (errorCt != 0) {
          result.getErrors().add(errorCt + " lines with errors skipped");
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      Logger.getLogger(getClass()).info("Rolling back transaction");
      service.rollback();
      result.getErrors().add("Unexpected error: " + e.getMessage());
    } finally {
      if (pbr != null) {
        pbr.close();
      }
      service.setTransactionPerOperation(origTPO);
    }
    return result;
  }

  /* see superclass */
  @Override
  public InputStream exportAbbreviationFile(String terminology,
    boolean acceptNew, boolean readyOnly) throws Exception {

    if (acceptNew) {
      final TypeKeyValueList newAbbrs = service.findTypeKeyValuesForQuery(
          "type:\"" + getAbbrType(terminology) + "\" AND workflowStatus:NEW",
          null);
      if (newAbbrs.size() > 0) {
        origTPO = service.getTransactionPerOperation();
        service.setTransactionPerOperation(false);
        service.beginTransaction();
        for (TypeKeyValue abbr : newAbbrs.getObjects()) {
          abbr.setWorkflowStatus(WorkflowStatus.PUBLISHED);
          service.updateTypeKeyValue(abbr);
        }
        service.commit();
        service.setTransactionPerOperation(origTPO);
      }
    }

    // Write a header
    // Obtain members for refset,
    // Write RF2 simple refset pattern to a StringBuilder
    // wrap and return the string for that as an input stream
    StringBuilder sb = new StringBuilder();
    sb.append("lowWord").append("\t");
    sb.append("fullWord").append("\r\n");

    // sort by key
    PfsParameter pfs = new PfsParameterJpa();
    pfs.setSortField("key");

    TypeKeyValueList abbrs = service.findTypeKeyValuesForQuery(
        "type:\"" + getAbbrType(terminology) + "\"", pfs);
    for (TypeKeyValue abbr : abbrs.getObjects()) {
      if (!readyOnly
          || !WorkflowStatus.NEEDS_REVIEW.equals(abbr.getWorkflowStatus())) {
        sb.append(abbr.getKey()).append("\t");
        sb.append(abbr.getValue()).append("\r\n");
      }
    }
    return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
  }

  /* see superclass */
  @Override
  public void computeAbbreviationStatuses(String terminology) throws Exception {

    // NOTE: This is a debug helper function only and not intended for
    // actual use
    origTPO = service.getTransactionPerOperation();
    service.setTransactionPerOperation(false);
    service.beginTransaction();

    final TypeKeyValueList abbrs = service.findTypeKeyValuesForQuery(
        "type:\"" + getAbbrType(terminology) + "\"", null);
    for (final TypeKeyValue abbr : abbrs.getObjects()) {
      final TypeKeyValueList reviews = getReviewForAbbreviation(abbr);

      // NOTE getReviews always returns at least the term itself
      if (reviews.size() > 1) {
        abbr.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
        service.updateTypeKeyValue(abbr);
      } else if (!WorkflowStatus.NEW.equals(abbr.getWorkflowStatus())) {
        abbr.setWorkflowStatus(WorkflowStatus.PUBLISHED);
        service.updateTypeKeyValue(abbr);
      }
    }

    service.commit();
    service.setTransactionPerOperation(origTPO);

  }

  /* see superclass */
  @Override
  public void updateWorkflowStatus(TypeKeyValue abbr) throws Exception {
    // NOTE: Review always contains the term itself
    TypeKeyValueList temp = getReviewForAbbreviation(abbr);
    if (getReviewForAbbreviation(abbr).getTotalCount() > 1) {
      // set to NEEDS_REVIEW
      abbr.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
    } else {
      // otherwise set to
      abbr.setWorkflowStatus(WorkflowStatus.NEW);
    }
  }

  /* see superclass */
  @Override
  public TypeKeyValueList filterResults(TypeKeyValueList list, String filter,
    PfsParameter pfs) throws Exception {
    List<TypeKeyValue> filteredList = null;
    switch (filter) {
      case "duplicateKey":
        filteredList = filterDuplicateKey(list.getObjects());
        break;
      case "duplicateValue":
        filteredList = filterDuplicateValue(list.getObjects());
        break;
      case "blankValue":
        filteredList = filterBlankValue(list.getObjects());
        break;
      case "duplicate":
        filteredList = filterDuplicates(list.getObjects());
        break;
      case "duplicateValueAcrossTerminologies":
        filteredList =
            filterExpansionDuplicateAcrossTerminologies(list.getObjects());
        break;
      default:
        filteredList = list.getObjects();
    }
    TypeKeyValueList results = null;
    if (filteredList.size() == 0) {
      results = new TypeKeyValueListJpa();
    } else {
      String query = ConfigUtility.composeQuery("OR", filteredList.stream()
          .map(t -> "id:" + t.getId()).collect(Collectors.toList()));
      results = service.findTypeKeyValuesForQuery(query, pfs);

    }
    return results;

  }

  //
  /**
   * Filter duplicate key.
   *
   * @param list the list
   * @return the list
   */
  @SuppressWarnings("static-method")
  private List<TypeKeyValue> filterDuplicateKey(List<TypeKeyValue> list) {
    final Map<String, List<TypeKeyValue>> map = new HashMap<>();
    for (final TypeKeyValue abbr : list) {
      if (abbr.getKey() != null && !abbr.getKey().isEmpty()) {
        if (!map.containsKey(abbr.getKey())) {
          map.put(abbr.getKey(), new ArrayList<>(Arrays.asList(abbr)));
        } else {
          map.get(abbr.getKey()).add(abbr);
        }
      }
    }
    final List<TypeKeyValue> dupKeys = new ArrayList<>();
    for (final String key : map.keySet()) {
      if (map.get(key).size() > 1) {
        dupKeys.addAll(map.get(key));
      }
    }
    return dupKeys;
  }

  /**
   * Filter duplicate value.
   *
   * @param list the list
   * @return the list
   */
  @SuppressWarnings("static-method")
  private List<TypeKeyValue> filterDuplicateValue(List<TypeKeyValue> list) {
    final Map<String, List<TypeKeyValue>> map = new HashMap<>();
    for (final TypeKeyValue abbr : list) {
      // NOTE: Catch values consisting of whitespace
      if (abbr.getValue() != null && !abbr.getValue().trim().isEmpty()) {
        if (!map.containsKey(abbr.getValue())) {
          map.put(abbr.getValue(), new ArrayList<>(Arrays.asList(abbr)));
        } else {
          map.get(abbr.getValue()).add(abbr);
        }
      }
    }
    final List<TypeKeyValue> results = new ArrayList<>();
    for (final String key : map.keySet()) {
      if (map.get(key).size() > 1) {
        results.addAll(map.get(key));
      }
    }
    return results;
  }

  /**
   * Filter blank value.
   *
   * @param list the list
   * @return the list
   */
  @SuppressWarnings("static-method")
  private List<TypeKeyValue> filterBlankValue(List<TypeKeyValue> list) {
    final List<TypeKeyValue> results = new ArrayList<>();
    for (final TypeKeyValue abbr : list) {
      // NOTE: Catch values consisting of whitespace
      if (abbr.getValue() == null || abbr.getValue().trim().isEmpty()) {
        System.out.println("  BLANK");
        results.add(abbr);
      }
    }
    return results;
  }

  /**
   * Filter duplicates.
   *
   * @param list the list
   * @return the list
   */
  @SuppressWarnings("static-method")
  private List<TypeKeyValue> filterDuplicates(List<TypeKeyValue> list) {

    final Map<String, List<TypeKeyValue>> map = new HashMap<>();
    for (final TypeKeyValue abbr : list) {
      // NOTE: Catch values consisting of whitespace
      String keyValueStr = abbr.getKey() + "\t" + abbr.getValue();
      if (!map.containsKey(keyValueStr)) {
        map.put(keyValueStr, new ArrayList<>(Arrays.asList(abbr)));
      } else {
        map.get(keyValueStr).add(abbr);
      }
    }

    final List<TypeKeyValue> results = new ArrayList<>();
    for (final String key : map.keySet()) {
      if (map.get(key).size() > 1) {
        results.addAll(map.get(key));
      }
    }
    return results;
  }

  private List<TypeKeyValue> filterExpansionDuplicateAcrossTerminologies(
    List<TypeKeyValue> list) throws Exception {

    final List<TypeKeyValue> results = new ArrayList<>();

    for (TypeKeyValue abbr : list) {
      System.out.println("Checking value for : " + abbr.getId() + " / " + abbr.getValue());
      final TypeKeyValueList matches =
          service.findTypeKeyValuesForQuery("value:\"" + abbr.getValue()
              + "\" AND NOT type:\"" + abbr.getType() + "\"", null);
      if (matches.getTotalCount() > 0) {
        System.out.println("  Matched : " + abbr.getId() + " / " + matches.getTotalCount());
        results.add(abbr);
      }
    }

    return results;
  }

  @Override
  public String getAbbrType(String terminology) {
    return terminology + "-TERM";
  }

}
