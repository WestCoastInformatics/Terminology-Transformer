/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.validation;

import java.util.Properties;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.services.validation.AbstractValidationCheck;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Concept;

/**
 * Default checks that apply to all terminologies.
 */
public class ConceptMinimumRequirements
    extends AbstractValidationCheck {

  /* see superclass */
  @Override
  public void setProperties(Properties p) {

  }

  /* see superclass */
  @Override
  public ValidationResult validate(Concept c) {
    ValidationResult result = new ValidationResultJpa();
    boolean hasPreferredTerm = false;
    for (Atom atom : c.getAtoms()) {
       if (atom.getTermType().equals("PT")) {
         hasPreferredTerm = true;
       }
    }
    if (!hasPreferredTerm) {
      result.getErrors().add("Concept does not have preferred term");
    }
    if (c.getSemanticTypes() == null || c.getSemanticTypes().size() == 0) {
      result.getErrors().add("Concept has no specified features / semantic types");
    }
    return result;
  }

  /* see superclass */
  @Override
  public String getName() {
    return "Concept Minimum Requirements";
  }

}
