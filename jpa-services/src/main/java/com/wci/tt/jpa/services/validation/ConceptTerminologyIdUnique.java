/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.validation;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.content.ConceptList;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.validation.AbstractValidationCheck;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;

/**
 * Default checks that apply to all terminologies.
 */
public class ConceptTerminologyIdUnique extends AbstractValidationCheck {

  /* see superclass */
  @Override
  public void setProperties(Properties p) {

  }

  /* see superclass */
  @Override
  public ValidationResult validate(Concept c) {
    ValidationResult result = new ValidationResultJpa();
    ContentService contentService = null;
    
    if (c.getTerminologyId() == null || c.getTerminologyId().isEmpty()) {
      result.getErrors().add("Terminology id null or empty");
      return result;
    }
    try {
      contentService = new ContentServiceJpa();

      ConceptList concepts = null;
      
      // check terminology id
      concepts = contentService.findConcepts(c.getTerminology(), c.getVersion(),
          c.getBranch(),
          "NOT id:" + c.getId() + " AND terminologyId:" + c.getTerminologyId(),
          null);

      if (concepts.getTotalCount() > 0) {
        for (Concept concept : concepts.getObjects()) {
          result.addError("Terminology id " + c.getTerminologyId()
              + " not unique, duplicated on internal id " + concept.getId()
              + ", " + concept.getName());
        }
      }

    } catch (Exception e) {
      // do nothing
    } finally {
      if (contentService != null) {
        try {
          contentService.close();
        } catch (Exception e) {
          // do nothing
        }
      }
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
    return "Concept Terminology Id Unique";
  }

}
