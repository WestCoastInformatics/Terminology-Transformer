/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import com.wci.tt.ValidationResult;
import com.wci.tt.helpers.ConceptValidationResultList;
import com.wci.tt.helpers.KeyValuePairList;
import com.wci.tt.helpers.MemberValidationResultList;
import com.wci.tt.jpa.RefsetJpa;
import com.wci.tt.jpa.TranslationJpa;
import com.wci.tt.rf2.jpa.ConceptJpa;
import com.wci.tt.rf2.jpa.ConceptRefsetMemberJpa;

/**
 * Represents a service for validating content.
 */
public interface ValidationServiceRest {

  /**
   * Validates an individual translation concept.
   *
   * @param concept the concept
   * @param projectId the project id
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateConcept(ConceptJpa concept, Long projectId, String authToken)
    throws Exception;

  /**
   * Validate all concepts.
   *
   * @param translationId the translation id
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ConceptValidationResultList validateAllConcepts(Long translationId,
    String authToken) throws Exception;

  /**
   * Validate a top level refset info or its metadata.
   *
   * @param refset the refset
   * @param projectId the project id
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateRefset(RefsetJpa refset, Long projectId, String authToken)
    throws Exception;

  /**
   * Validate a top-level translation or its metadata.
   *
   * @param translation the translation
   * @param projectId the project id
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateTranslation(TranslationJpa translation,
    Long projectId, String authToken) throws Exception;

  /**
   * Validate an individual simple ref set member.
   *
   * @param member the member
   * @param projectId the project id
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateMember(ConceptRefsetMemberJpa member,
    Long projectId, String authToken) throws Exception;

  /**
   * Validate all refset members.
   *
   * @param refsetId the refset id
   * @param authToken the auth token
   * @return the validation result
   * @throws Exception the exception
   */
  public MemberValidationResultList validateAllMembers(Long refsetId,
    String authToken) throws Exception;

  /**
   * Returns the validation checks.
   *
   * @param authToken the auth token
   * @return the validation checks
   * @throws Exception the exception
   */
  public KeyValuePairList getValidationChecks(String authToken) throws Exception;
}
