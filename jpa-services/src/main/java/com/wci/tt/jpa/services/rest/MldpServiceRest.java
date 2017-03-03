/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import java.io.InputStream;
import java.util.List;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueJpa;
import com.wci.umls.server.model.workflow.WorkflowStatus;

/**
 * Lists a transform routines available via a REST service.
 */
public interface MldpServiceRest {

  /**
   * Import abbreviations.
   *
   * @param contentDispositionHeader the content disposition header
   * @param in the in
   * @param projectId the project id
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult importAbbreviations(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    Long projectId, String authToken) throws Exception;

  /**
   * Validate abbreviations file.
   *
   * @param contentDispositionHeader the content disposition header
   * @param in the in
   * @param projectId the project id
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateAbbreviationsFile(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    Long projectId, String authToken) throws Exception;

  /**
   * Export abbreviations file.
   *
   * @param projectId the project id
   * @param readyOnly the ready only flag
   * @param acceptNew the accept new
   * @param authToken the auth token
   * @return the input stream
   * @throws Exception the exception
   */
  public InputStream exportAbbreviationsFile(Long projectId, boolean readyOnly,
    boolean acceptNew, String authToken) throws Exception;

  /**
   * Adds the abbreviation.
   *
   * @param typeKeyValue the type key value
   * @param projectId the project id
   * @param authToken the auth token
   * @return the type key value
   * @throws Exception the exception
   */
  public TypeKeyValue addAbbreviation(TypeKeyValueJpa typeKeyValue,
    Long projectId, String authToken) throws Exception;

  /**
   * Returns the abbreviation.
   *
   * @param id the id
   * @param projectId the project id
   * @param authToken the auth token
   * @return the abbreviation
   * @throws Exception the exception
   */
  public TypeKeyValue getAbbreviation(Long id, Long projectId, String authToken)
    throws Exception;

  /**
   * Update abbreviation.
   *
   * @param typeKeyValue the type key value
   * @param projectId the project id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateAbbreviation(TypeKeyValueJpa typeKeyValue, Long projectId,
    String authToken) throws Exception;

  /**
   * Removes the abbreviation.
   *
   * @param id the id
   * @param projectId the project id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeAbbreviation(Long id, Long projectId, String authToken)
    throws Exception;

  /**
   * Find abbreviations.
   *
   * @param projectId the project id
   * @param query the query
   * @param filter the filter
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the type key value list
   * @throws Exception the exception
   */
  public TypeKeyValueList findAbbreviations(Long projectId, String query,
    String filter, PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Compute review statuses.
   *
   * @param projectId the project id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void computeReviewStatuses(Long projectId, String authToken)
    throws Exception;

  /**
   * Returns the review for abbreviation.
   *
   * @param id the id
   * @param projectId the project id
   * @param authToken the auth token
   * @return the review for abbreviation
   * @throws Exception the exception
   */
  public TypeKeyValueList getReviewForAbbreviation(Long id, Long projectId,
    String authToken) throws Exception;

  /**
   * Returns the review for abbreviations.
   *
   * @param ids the ids
   * @param projectId the project id
   * @param authToken the auth token
   * @return the review for abbreviations
   * @throws Exception the exception
   */
  public TypeKeyValueList getReviewForAbbreviations(List<Long> ids,
    Long projectId, String authToken) throws Exception;

  /**
   * Removes the abbreviations.
   *
   * @param ids the ids
   * @param projectId the project id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeAbbreviations(List<Long> ids, Long projectId,
    String authToken) throws Exception;

  /**
   * Import concepts file.
   *
   * @param contentDispositionHeader the content disposition header
   * @param in the in
   * @param projectId the project id
   * @param keepIds the keep ids
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult importConcepts(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    Long projectId, boolean keepIds, String authToken) throws Exception;

  /**
   * Export concepts file.
   *
   * @param projectId the project id
   * @param acceptNew the accept new
   * @param readyOnly the ready only
   * @param authToken the auth token
   * @return the input stream
   * @throws Exception the exception
   */
  public InputStream exportConcepts(Long projectId, boolean acceptNew,
    boolean readyOnly, String authToken) throws Exception;

  /**
   * Put concepts in workflow. NOTE: Temporary API prior to implementation of
   * proper workflow
   *
   * @param projectId the project id
   * @param conceptIds the concept ids
   * @param workflowStatus the workflow status
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void putConceptsInWorkflow(Long projectId, List<Long> conceptIds,
    WorkflowStatus workflowStatus, String authToken) throws Exception;

  /**
   * Clear workflow for project.
   *
   * @param projectId the project id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void clearReviewWorkflowForProject(Long projectId, String authToken)
    throws Exception;

  /**
   * Import terms.
   *
   * @param contentDispositionHeader the content disposition header
   * @param in the in
   * @param projectId the project id
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult importTerms(
    FormDataContentDisposition contentDispositionHeader, InputStream in,
    Long projectId, String authToken) throws Exception;

  /**
   * Find terms.
   *
   * @param projectId the project id
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the type key value list
   * @throws Exception the exception
   */
  public TypeKeyValueList findTerms(Long projectId, String query,
    PfsParameterJpa pfs, String authToken) throws Exception;

  /**
   * Returns the term.
   *
   * @param id the id
   * @param projectId the project id
   * @param authToken the auth token
   * @return the term
   * @throws Exception the exception
   */
  public TypeKeyValue getTerm(Long id, Long projectId, String authToken)
    throws Exception;

  /**
   * Adds the term.
   *
   * @param typeKeyValue the type key value
   * @param projectId the project id
   * @param authToken the auth token
   * @return the type key value
   * @throws Exception the exception
   */
  public TypeKeyValue addTerm(TypeKeyValueJpa typeKeyValue, Long projectId,
    String authToken) throws Exception;

  /**
   * Update term.
   *
   * @param typeKeyValue the type key value
   * @param projectId the project id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateTerm(TypeKeyValueJpa typeKeyValue, Long projectId,
    String authToken) throws Exception;

  /**
   * Removes the terms.
   *
   * @param ids the ids
   * @param projectId the project id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeTerms(List<Long> ids, Long projectId, String authToken)
    throws Exception;

  /**
   * Removes the term.
   *
   * @param id the id
   * @param projectId the project id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeTerm(Long id, Long projectId, String authToken)
    throws Exception;

}