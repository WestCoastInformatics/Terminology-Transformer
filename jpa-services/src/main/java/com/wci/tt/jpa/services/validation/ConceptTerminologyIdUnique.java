/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.validation;

import java.util.Properties;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.content.ConceptList;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.validation.AbstractValidationCheck;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.SemanticTypeComponent;
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

      // check name -- exact match across features
      concepts = contentService.findConcepts(c.getTerminology(), c.getVersion(),
          c.getBranch(),
          "NOT id:" + c.getId() + " AND name:\"" + c.getName() + "\"", null);

      for (Concept concept : concepts.getObjects()) {
        boolean featureMatch = false;
        for (SemanticTypeComponent sty1 : c.getSemanticTypes()) {
          for (SemanticTypeComponent sty2 : concept.getSemanticTypes()) {
            if (sty1.getSemanticType().equals(sty2.getSemanticType())) {
              featureMatch = true;
            }
          }
        }
        if (!featureMatch) {
        result.addError(
            "Concept name not unique across features, duplicated on concept "
                + concept.getTerminologyId());
        }

      }

      // check name -- exact match within feature
      concepts = contentService.findConcepts(null, null, c.getBranch(),
          "NOT id:" + c.getId() + " AND name:\"" + c.getName() + "\""
              + " AND NOT (terminology:\"" + c.getTerminology() + "\""
              + " AND version:\"" + c.getVersion() + "\")",
          null);
      for (Concept concept : concepts.getObjects()) {
        for (SemanticTypeComponent sty1 : c.getSemanticTypes()) {
          for (SemanticTypeComponent sty2 : concept.getSemanticTypes()) {
            if (sty1.getSemanticType().equals(sty2.getSemanticType())) {
              result.addError(
                  "Concept name not unique within feature " + sty2 + ", duplicated on concept "
                      + concept.getTerminologyId());
            }
          }
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

  /* see superclass */
  @Override
  public String getName() {
    return "Concept Terminology Id Unique";
  }

}