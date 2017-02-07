/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.validation;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.services.validation.AbstractValidationCheck;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;

/**
 * Default checks that apply to all terminologies.
 */
public class ConceptHasPreferredTerm
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
    return result;
  }
  
  @Override
  public Set<Long> validateConcepts(Set<Long> conceptIds, String terminology, String version, ContentService service) throws Exception {
    final Set<Long> failedConceptIds = new HashSet<>();
    for (final Long id : conceptIds) {
      final Concept concept = service.getConcept(id);
      if (!validate(concept).isValid()) {
        failedConceptIds.add(id);
      }
    }
    return failedConceptIds;
  }

  /* see superclass */
  @Override
  public String getName() {
    return "Concept Has Preferred Term";
  }

}
