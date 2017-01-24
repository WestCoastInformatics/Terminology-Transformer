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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wci.tt.services.handlers.AbbreviationHandler;
import com.wci.umls.server.ValidationResult;
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

  private Set<TypeKeyValue> getReviewForAbbreviationsHelper(
    List<TypeKeyValue> abbrsToCheck, Set<TypeKeyValue> computedConflicts)
    throws Exception {

    // if computed reviews is null, instantiate set from the abbreviations to
    // check
    if (computedConflicts == null) {
      computedConflicts = new HashSet<>(abbrsToCheck);
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

    System.out.println("Matching results: " + results);

    // compute new results from results and reviews
    final List<TypeKeyValue> newResults = new ArrayList<>(results);
    newResults.removeAll(computedConflicts);

    // update reviews from results
    computedConflicts.addAll(results);

    // call with new results and updated reviews
    return getReviewForAbbreviationsHelper(newResults, computedConflicts);
  }

  @Override
  public TypeKeyValueList getReviewForAbbreviation(TypeKeyValue abbr)
    throws Exception {
    Set<TypeKeyValue> reviews = getReviewForAbbreviationsHelper(
        new ArrayList<>(Arrays.asList(abbr)), null);
    TypeKeyValueList list = new TypeKeyValueListJpa();
    list.setTotalCount(reviews.size());
    list.setObjects(new ArrayList<>(reviews));
    return list;
  }

  @Override
  public TypeKeyValueList getReviewForAbbreviations(List<TypeKeyValue> abbrList)
    throws Exception {
    Set<TypeKeyValue> reviews = getReviewForAbbreviationsHelper(abbrList, null);
    TypeKeyValueList list = new TypeKeyValueListJpa();
    list.setTotalCount(reviews.size());
    list.setObjects(new ArrayList<>(reviews));
    System.out.println("Review results size: " + reviews.size());
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

      // Read from input stream
      Reader reader = new InputStreamReader(in, "UTF-8");
      pbr = new PushBackReader(reader);
      int lineCt = 0;
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

        // CHECK: exactly two fields
        if (fields.length == 2) {

          // CHECK: Both fields non-null and non-empty
          if (fields[0] != null && !fields[0].isEmpty() && fields[1] != null
              && !fields[1].isEmpty()) {

            // check for type/pair matches
            final TypeKeyValueList keyMatches =
                service.findTypeKeyValuesForQuery(
                    "type:\"" + type + "\"" + " AND key:\"" + fields[0] + "\"",
                    pfs);

            // check for exact match
            boolean pairMatchFound = false;
            boolean keyOrValueMatchFound = false;
            if (keyMatches.getTotalCount() > 0) {
              for (TypeKeyValue match : keyMatches.getObjects()) {
                if (fields[1].equals(match.getValue())) {
                  pairMatchFound = true;
                  dupPairCt++;
                  if (!executeImport) {
                    result.getWarnings().add("Line " + lineCt + ": Duplicate: "
                        + fields[0] + " / " + match.getValue());
                  }
                } else {
                  if (!executeImport) {
                    result.getWarnings()
                        .add("Line " + lineCt + ": Abbreviation " + fields[0]
                            + " already exists with expansion "
                            + match.getValue());
                  }
                }
              }

              // increment duplicate key counter if pair match not found
              if (!pairMatchFound) {
                keyOrValueMatchFound = true;
                dupKeyCt++;
              }
            }

            // if validation, check for value match without key match
            if (!executeImport) {

              if (!pairMatchFound) {
                toAddCt++;
              }

              // check for key match, pair not match
              final TypeKeyValueList valueMatches =
                  service.findTypeKeyValuesForQuery("type:\"" + type + "\""
                      + " AND value:\"" + fields[1] + "\"", null);
              if (valueMatches.getTotalCount() > 0) {
                for (TypeKeyValue match : keyMatches.getObjects()) {
                  if (!fields[0].equals(match.getKey())) {
                    result.getWarnings().add("Line " + lineCt + ": Expansion "
                        + fields[1] + " exists for key " + fields[0]);
                  }

                }
                // increment duplicate value count if pair match not found
                if (!pairMatchFound) {
                  keyOrValueMatchFound = true;
                  dupValCt++;
                }
              }
            }

            // if import mode and no pair match found, add the new abbreviation
            if (!pairMatchFound && executeImport) {
              // add different expansion for same
              System.out.println("Adding " + fields[0] + " / " + fields[1]);
              TypeKeyValue typeKeyValue =
                  new TypeKeyValueJpa(type, fields[0], fields[1]);
              typeKeyValue.setWorkflowStatus(keyOrValueMatchFound
                  ? WorkflowStatus.NEEDS_REVIEW : WorkflowStatus.NEW);
              service.addTypeKeyValue(typeKeyValue);
              addedCt++;
            }
          } else {
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

      if (executeImport) {
        service.commit();
      }

      // aggregate results for validation
      if (!executeImport) {
        if (toAddCt > 0) {
          result.getComments().add(toAddCt + " abbreviations will be added");
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
          result.getComments().add(addedCt + " abbreviations added");
        }
        if (dupPairCt != 0) {
          result.getWarnings().add(
              dupPairCt + " duplicate abbreviation/expansion pairs skipped");
        }
        if (errorCt != 0) {
          result.getErrors().add(errorCt + " lines with errors skipped");
        }
      }

    } catch (Exception e) {
      service.rollback();
      result.getErrors().add("Unexpected error: " + e.getMessage());
    } finally {
      if (pbr != null) {
        pbr.close();
      }
    }
    return result;
  }

  @Override
  public InputStream exportAbbreviationFile(String abbrType, boolean readyOnly)
    throws Exception {
    // Write a header
    // Obtain members for refset,
    // Write RF2 simple refset pattern to a StringBuilder
    // wrap and return the string for that as an input stream
    StringBuilder sb = new StringBuilder();
    sb.append("abbreviation").append("\t");
    sb.append("expansion").append("\t");
    sb.append("\r\n");

    // sort by key
    PfsParameter pfs = new PfsParameterJpa();
    pfs.setSortField("key");

    TypeKeyValueList abbrs =
        service.findTypeKeyValuesForQuery("type:\"" + abbrType + "\"", pfs);
    for (TypeKeyValue abbr : abbrs.getObjects()) {
      if (!readyOnly
          || !WorkflowStatus.NEEDS_REVIEW.equals(abbr.getWorkflowStatus())) {
        sb.append(abbr.getKey()).append("\t");
        sb.append(abbr.getValue()).append("\t");
        sb.append("\r\n");
      }
    }
    return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
  }

  @Override
  public void computeAbbreviationStatuses(String abbrType) throws Exception {

    // TODO NOTE: This is a debug helper function only and not intended for
    // actual use
    service.setTransactionPerOperation(false);
    service.beginTransaction();

    // TODO Consider optimization (paged retrievals) if necessary
    final TypeKeyValueList abbrs =
        service.findTypeKeyValuesForQuery("type:\"" + abbrType + "\"", null);
    for (final TypeKeyValue abbr : abbrs.getObjects()) {
      final TypeKeyValueList reviews = getReviewForAbbreviation(abbr);

      // NOTE getReviews always returns at least the abbreviation itself
      if (reviews.size() > 1) {
        System.out.println("  --> NEEDS REVIEW MARKED for " + abbr.getKey());
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

  @SuppressWarnings("unchecked")
  @Override
  public TypeKeyValueList filterResults(TypeKeyValueList list, String filter,
    PfsParameter pfs) throws Exception {
    List filteredList = null;
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
      default:
        filteredList = list.getObjects();
    }
    
    // create paging-only PFS
    PfsParameter lpfs = new PfsParameterJpa();
    lpfs.setStartIndex(pfs.getStartIndex());
    lpfs.setMaxResults(pfs.getMaxResults());
    final int totalCt[] = new int[1];
    filteredList = service.applyPfsToList(filteredList, TypeKeyValueJpa.class, totalCt, lpfs);
    TypeKeyValueList results = new TypeKeyValueListJpa();
    results.setTotalCount(totalCt[0]);
    results.setObjects(filteredList);
    return results;
    
  }

  //
  // Filter functions separated for convenience/clarity
  //
  private List<TypeKeyValue> filterDuplicateKey(List<TypeKeyValue> list) {
    final Map<String, List<TypeKeyValue>> map = new HashMap<>();
    for (final TypeKeyValue abbr : list) {
      if (!map.containsKey(abbr.getKey())) {
        map.put(abbr.getKey(), new ArrayList<>(Arrays.asList(abbr)));
      } else {
        map.get(abbr.getKey()).add(abbr);
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
      if (!map.containsKey(abbr.getKey())) {
        map.put(abbr.getValue(), new ArrayList<>(Arrays.asList(abbr)));
      } else {
        map.get(abbr.getValue()).add(abbr);
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
      if (abbr.getValue() == null || abbr.getValue().isEmpty()) {
        results.add(abbr);
      }
    }
    return results;
  }

}
