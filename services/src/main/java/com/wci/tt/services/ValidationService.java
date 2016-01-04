/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services;

import com.wci.tt.Project;
import com.wci.tt.Refset;
import com.wci.tt.Translation;
import com.wci.tt.ValidationResult;
import com.wci.tt.helpers.KeyValuePairList;
import com.wci.tt.rf2.Concept;
import com.wci.tt.rf2.ConceptRefsetMember;

/**
 * Generically represents a service for validating content.
 */
public interface ValidationService extends RootService {

  /**
   * Validate concept.
   *
   * @param concept the concept
   * @param project the project
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateConcept(Concept concept, Project project,
    TranslationService service) throws Exception;

  /**
   * Validate translation.
   *
   * @param translation the translation
   * @param project the project
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateTranslation(Translation translation,
    Project project, TranslationService service) throws Exception;

  /**
   * Validate member.
   *
   * @param member the member
   * @param project the project
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateMember(ConceptRefsetMember member,
    Project project, RefsetService service) throws Exception;

  /**
   * Validate refset.
   *
   * @param refset the refset
   * @param project TODO
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateRefset(Refset refset, Project project,
    RefsetService service) throws Exception;

  /**
   * Returns the validation check names.
   *
   * @return the validation check names
   * @throws Exception the exception
   */
  public KeyValuePairList getValidationCheckNames() throws Exception;

}