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
public class ConceptHasExactlyOneFeature
    extends AbstractValidationCheck {

  /* see superclass */
  @Override
  public void setProperties(Properties p) {

  }

  /* see superclass */
  @Override
  public ValidationResult validate(Concept c) {
    ValidationResult result = new ValidationResultJpa();
  
    if (c.getSemanticTypes() == null || c.getSemanticTypes().size() != 1) {
      result.getErrors().add("Concept does not have exactly one feature");
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
    return "Concept Minimum Requirements";
  }

}
