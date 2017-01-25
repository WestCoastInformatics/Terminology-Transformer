/*
 *    Copyright 2016 West Coast Informatics, LLC
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
 * abbreviations
 */
public class DefaultAbbreviationHandler extends AbstractConfigurable
    implements AbbreviationHandler {

  private ProjectService service = null;

  @Override
  public String getName() {
    return "Default abbreviation handler";
  }

  @Override
  public void setService(ProjectService service) {
    this.service = service;
  }

  @Override
  public void close() throws Exception {
    // do nothing
  }

  /**
   * Recurse helper function to compute potential reviews in abbreviations
   * 
   * @param abbrs
   * @param reviews
   * @param service
   * @return
   * @throws Exception
   */

  private List<TypeKeyValue> getReviewForAbbreviationsHelper(
    List<TypeKeyValue> abbrsToCheck, List<TypeKeyValue> computedConflicts)
    throws Exception {

    // if computed reviews is null, instantiate set from the abbreviations to
    // check
    if (computedConflicts == null) {
      computedConflicts = new ArrayList<>(abbrsToCheck);
    }

    // if no further abbreviations to process, return the passed reviews list
    if (abbrsToCheck == null || abbrsToCheck.size() == 0) {
      return computedConflicts;
    }

    // type for convenience
    final String type = abbrsToCheck.get(0).getType();

    // construct query from all keys and values
    // NOTE: Could be optimized for values already searched, but don't expect
    // this to be very big
    final List<String> clauses = new ArrayList<>();
    for (TypeKeyValue abbr : abbrsToCheck) {
      // clause: key OR value matches for abbreviation not htis one
      clauses.add("(NOT id:" + abbr.getId() + " AND (keySort:\"" + abbr.getKey()
          + "\" OR value:\"" + abbr.getValue() + "\"))");
    }

    String query = "";
    for (int i = 0; i < clauses.size(); i++) {
      query += clauses.get(i) + (i == clauses.size() - 1 ? "" : " OR ");
    }

    PfsParameter pfs = new PfsParameterJpa();
    pfs.setQueryRestriction("type:\"" + type + "\"");

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
      for (TypeKeyValue conflict : computedConflicts) {
        if (result.getId().equals(conflict.getId())) {
          exists = true;
        }
      }
      if (!exists) {
        newResults.add(result);
        computedConflicts.add(result);
      }
    }

    // call with new results and updated reviews
    return getReviewForAbbreviationsHelper(newResults, computedConflicts);
  }

  @Override
  public TypeKeyValueList getReviewForAbbreviation(TypeKeyValue abbr)
    throws Exception {
    List<TypeKeyValue> reviews = getReviewForAbbreviationsHelper(
        new ArrayList<>(Arrays.asList(abbr)), null);
    TypeKeyValueList list = new TypeKeyValueListJpa();
    list.setTotalCount(reviews.size());
    list.setObjects(reviews);
    return list;
  }

  @Override
  public TypeKeyValueList getReviewForAbbreviations(List<TypeKeyValue> abbrList)
    throws Exception {
    List<TypeKeyValue> reviews =
        getReviewForAbbreviationsHelper(abbrList, null);
    TypeKeyValueList list = new TypeKeyValueListJpa();
    list.setTotalCount(reviews.size());
    list.setObjects(reviews);
    return list;
  }

  @Override
  public ValidationResult validateAbbreviationFile(String abbrType,
    InputStream inFile) throws Exception {
    return this.importHelper(abbrType, inFile, false);
  }

  @Override
  public ValidationResult importAbbreviationFile(String abbrType,
    InputStream inFile) throws Exception {
    return this.importHelper(abbrType, inFile, true);
  }

  @Override
  public boolean isHeaderLine(String line) {
    return line != null && line.toLowerCase().startsWith("abbreviation");
  }

  private ValidationResult importHelper(String type, InputStream in,
    boolean executeImport) throws Exception {
    ValidationResult result = new ValidationResultJpa();
    PushBackReader pbr = null;
    try {

      if (executeImport) {
        service.setTransactionPerOperation(false);
        service.beginTransaction();

        // clear NEW status from all abbreviations
        for (TypeKeyValue abbr : service
            .findTypeKeyValuesForQuery("type:\"" + type + "\"", null)
            .getObjects()) {
          if (WorkflowStatus.NEW.equals(abbr.getWorkflowStatus())) {
            abbr.setWorkflowStatus(WorkflowStatus.PUBLISHED);
            service.updateTypeKeyValue(abbr);
          }
        }
      }

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
        throw new Exception("Empty file");
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

        System.out
            .println("line " + lineCt + ":  " + fields.length + " fields");
        for (int i = 0; i < fields.length; i++) {
          System.out.println("  " + i + ": " + fields[i]);
        }
        // CHECK: exactly two fields
        if (fields.length == 2) {

          // CHECK: Both fields non-null and non-empty
          if (fields[0] != null && !fields[0].trim().isEmpty()
              && fields[1] != null && !fields[1].trim().isEmpty()) {

            // check for type/pair matches
            final TypeKeyValueList keyMatches =
                service.findTypeKeyValuesForQuery(
                    "type:\"" + type + "\"" + " AND key:\"" + fields[0] + "\"",
                    pfs);

            // check for exact match
            boolean pairMatchFound = false;

            if (keyMatches.getTotalCount() > 0) {
              for (TypeKeyValue match : keyMatches.getObjects()) {
                if (fields[1].equals(match.getValue())) {
                  pairMatchFound = true;
                  dupPairCt++;
                }
              }

              // increment duplicate key counter if pair match not found
              if (!pairMatchFound) {
                dupKeyCt++;
              }
            }

            // if no exact match found and validation, check for value match
            // without key match
            if (!pairMatchFound && !executeImport) {
              toAddCt++;

              // check for key match, pair not match
              final TypeKeyValueList valueMatches =
                  service.findTypeKeyValuesForQuery("type:\"" + type + "\""
                      + " AND value:\"" + fields[1] + "\"", null);
              if (valueMatches.getTotalCount() > 0) {
                dupValCt++;
              }
            }

            // if no exact match found and import, add the new abbreviation
            if (!pairMatchFound && executeImport) {
              // add different expansion for same
              TypeKeyValue typeKeyValue =
                  new TypeKeyValueJpa(type, fields[0], fields[1]);
              service.addTypeKeyValue(typeKeyValue);
              newAbbrs.add(typeKeyValue);
              addedCt++;
            }
          }

          else {
            errorCt++;
            if (!executeImport) {
              result.getErrors()
                  .add("Line " + lineCt + ": Incomplete line: " + line);
            }
          }

        } else {
          errorCt++;
          if (!executeImport) {
            String fieldsStr = "";
            for (int i = 0; i < fields.length; i++) {
              fieldsStr += "[" + fields[i] + "] ";
            }
            result.getErrors().add("Line " + lineCt + ": Expected two fields "
                + lineCt + " but found " + fields.length + ": " + fieldsStr);
          }
        }
      } while ((line = pbr.readLine()) != null);

      // after all abbreviations loaded, apply workflow status and commit
      // NOTE: Commit required for lucene index writing
      if (executeImport) {

        service.commit();
        service.beginTransaction();
        for (TypeKeyValue newAbbr : newAbbrs) {
          // if value empty, ignored value -- set demotion
          if (newAbbr.getValue() == null) {
            newAbbr.setWorkflowStatus(WorkflowStatus.DEMOTION);
          }
          // if review has greater than one element -- needs review
          else if (getReviewForAbbreviation(newAbbr).size() > 1) {
            newAbbr.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
          }
          // default: set new
          else {
            newAbbr.setWorkflowStatus(WorkflowStatus.NEW);
          }
          service.updateTypeKeyValue(newAbbr);
        }
        service.commit();
      }

      // aggregate results for validation
      if (!executeImport) {
        if (toAddCt > 0) {
          result.getComments().add(toAddCt + " abbreviations will be added");
        }
        if (ignoredCt != 0) {
          result.getComments().add(ignoredCt + " abbreviation "
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
              .add(dupKeyCt + " existing abbreviations with new expansion");
        }
        if (dupValCt > 0) {
          result.getWarnings()
              .add(dupValCt + " existing expansions with new abbreviation");
        }
      }

      // otherwise, aggregate results for import
      else {
        if (addedCt == 0) {
          result.getWarnings().add("No abbreviations added.");
        } else {
          result.getComments().add(addedCt + " abbreviation "
              + (addedCt == 1 ? "" : "s") + " added");
        }
        if (ignoredCt != 0) {
          result.getComments().add(ignoredCt + " abbreviation "
              + (ignoredCt == 1 ? "" : "s") + " marked as ignored");
        }
        if (dupPairCt != 0) {
          result.getWarnings().add(
              dupPairCt + " duplicate abbreviation/expansion pairs skipped");
        }
        if (errorCt != 0) {
          result.getErrors().add(errorCt + " lines with errors skipped");
        }
      }

    } catch (

    Exception e) {
      e.printStackTrace();

      result.getErrors().add("Unexpected error: " + e.getMessage());
    } finally {
      if (pbr != null) {
        pbr.close();
      }
    }
    return result;
  }

  @Override
  public InputStream exportAbbreviationFile(String abbrType, boolean acceptNew,
    boolean readyOnly) throws Exception {
    
    if (acceptNew) {
      final TypeKeyValueList newAbbrs = service.findTypeKeyValuesForQuery("type:\"" + abbrType + "\" AND workflowStatus:NEW", null);
      if (newAbbrs.size() > 0) {
        service.setTransactionPerOperation(false);
        service.beginTransaction();
        for (TypeKeyValue abbr : newAbbrs.getObjects()) {
          abbr.setWorkflowStatus(WorkflowStatus.PUBLISHED);
          service.updateTypeKeyValue(abbr);
        }
        service.commit();
      }
    }

    // Write a header
    // Obtain members for refset,
    // Write RF2 simple refset pattern to a StringBuilder
    // wrap and return the string for that as an input stream
    StringBuilder sb = new StringBuilder();
    sb.append("abbreviation").append("\t");
    sb.append("expansion").append("\r\n");

    // sort by key
    PfsParameter pfs = new PfsParameterJpa();
    pfs.setSortField("key");

    TypeKeyValueList abbrs =
        service.findTypeKeyValuesForQuery("type:\"" + abbrType + "\"", pfs);
    for (TypeKeyValue abbr : abbrs.getObjects()) {
      if (!readyOnly
          || !WorkflowStatus.NEEDS_REVIEW.equals(abbr.getWorkflowStatus())) {
        sb.append(abbr.getKey()).append("\t");
        sb.append(abbr.getValue()).append("\r\n");
      }
    }
    return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
  }

  @Override
  public void computeAbbreviationStatuses(String abbrType) throws Exception {

    // NOTE: This is a debug helper function only and not intended for
    // actual use
    service.setTransactionPerOperation(false);
    service.beginTransaction();

    final TypeKeyValueList abbrs =
        service.findTypeKeyValuesForQuery("type:\"" + abbrType + "\"", null);
    for (final TypeKeyValue abbr : abbrs.getObjects()) {
      final TypeKeyValueList reviews = getReviewForAbbreviation(abbr);

      // NOTE getReviews always returns at least the abbreviation itself
      if (reviews.size() > 1) {
        abbr.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
        service.updateTypeKeyValue(abbr);
      } else if (!WorkflowStatus.NEW.equals(abbr.getWorkflowStatus())) {
        abbr.setWorkflowStatus(WorkflowStatus.PUBLISHED);
        service.updateTypeKeyValue(abbr);
      }
    }

    service.commit();

  }

  @Override
  public void updateWorkflowStatus(TypeKeyValue abbr) throws Exception {
    // NOTE: Review always contains the abbreviation itself
    if (getReviewForAbbreviation(abbr).getTotalCount() > 1) {
      // set to NEEDS_REVIEW
      abbr.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
    } else {
      // otherwise set to
      abbr.setWorkflowStatus(WorkflowStatus.PUBLISHED);
    }
  }

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
      default:
        filteredList = list.getObjects();
    }
    String query = ConfigUtility.composeQuery("OR", filteredList.stream()
        .map(t -> "id:" + t.getId()).collect(Collectors.toList()));
    TypeKeyValueList results =  service.findTypeKeyValuesForQuery(query, pfs);
    System.out.println("results: " + results.getTotalCount());
    return results;

  }

  //
  // Filter functions separated for convenience/clarity
  //
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

  private List<TypeKeyValue> filterBlankValue(List<TypeKeyValue> list) {
    final List<TypeKeyValue> results = new ArrayList<>();
    for (final TypeKeyValue abbr : list) {
      // NOTE: Catch values consisting of whitespace
      if (abbr.getValue() == null || abbr.getValue().trim().isEmpty()) {
        results.add(abbr);
      }
    }
    return results;
  }

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

}
