/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.validation;

import java.util.Properties;

import com.wci.tt.Refset;
import com.wci.tt.Translation;
import com.wci.tt.ValidationResult;
import com.wci.tt.jpa.ValidationResultJpa;
import com.wci.tt.rf2.Concept;
import com.wci.tt.rf2.ConceptRefsetMember;
import com.wci.tt.services.RefsetService;
import com.wci.tt.services.TranslationService;
import com.wci.tt.services.handlers.ValidationCheck;

/**
 * Abstract validation check to make implementation easier.
 */
public abstract class AbstractValidationCheck implements ValidationCheck {

  /* see superclass */
  @Override
  public void setProperties(Properties p) {
    // n/a
  }

  /* see superclass */
  @Override
  public abstract String getName();

  /* see superclass */
  @Override
  public ValidationResult validate(Concept concept, TranslationService service)
    throws Exception {
    ValidationResult result = new ValidationResultJpa();
    // no checks
    return result;
  }

  /* see superclass */
  @Override
  public ValidationResult validate(ConceptRefsetMember members,
    RefsetService service) throws Exception {
    ValidationResult result = new ValidationResultJpa();
    // no checks
    return result;
  }

  /* see superclass */
  @Override
  public ValidationResult validate(Translation translation,
    TranslationService service) throws Exception {
    ValidationResult result = new ValidationResultJpa();
    // no checks
    return result;
  }

  /* see superclass */
  @Override
  public ValidationResult validate(Refset refset, RefsetService service)
    throws Exception {
    ValidationResult result = new ValidationResultJpa();
    // no checks
    return result;
  }
}
