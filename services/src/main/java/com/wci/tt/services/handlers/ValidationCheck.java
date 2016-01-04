/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import com.wci.tt.Refset;
import com.wci.tt.Translation;
import com.wci.tt.ValidationResult;
import com.wci.tt.helpers.Configurable;
import com.wci.tt.rf2.Concept;
import com.wci.tt.rf2.ConceptRefsetMember;
import com.wci.tt.services.RefsetService;
import com.wci.tt.services.TranslationService;

/**
 * Generically represents a validation check on a {@link Refset},
 * {@link Translation}, {@link Concept}, or {@link ConceptRefsetMember}.
 * Implementations will be static state checks on the objects themselves,
 * determining whether a given state of the object is valid or not.
 * 
 */
public interface ValidationCheck extends Configurable {

  /**
   * Validates the concept.
   *
   * @param concept the concept
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validate(Concept concept, TranslationService service)
    throws Exception;

  /**
   * Validates the member.
   *
   * @param member the member
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validate(ConceptRefsetMember member,
    RefsetService service) throws Exception;

  /**
   * Validates the translation (not its members).
   *
   * @param translation the translation
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validate(Translation translation,
    TranslationService service) throws Exception;

  /**
   * Validates the refset (not its members).
   *
   * @param refset the refset
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validate(Refset refset, RefsetService service)
    throws Exception;

}
