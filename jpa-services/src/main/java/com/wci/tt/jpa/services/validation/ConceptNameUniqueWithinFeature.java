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
public class ConceptNameUniqueWithinFeature extends AbstractValidationCheck {

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

      final ConceptList concepts = contentService.findConcepts(null, null,
          c.getBranch(),
          "NOT id:" + c.getId() + " AND name:\"" + c.getName() + "\"", null);
      for (Concept concept : concepts.getObjects()) {
        for (SemanticTypeComponent sty1 : c.getSemanticTypes()) {
          for (SemanticTypeComponent sty2 : concept.getSemanticTypes()) {
            if (sty1.getSemanticType().equals(sty2.getSemanticType())) {
              result.addError("Concept name not unique within feature " + sty2.getSemanticType()
                  + ", duplicated on concept " + concept.getTerminologyId());
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
    return "Concept Name Unique Within Feature";
  }

}
