/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.validation;

import java.util.List;
import java.util.Properties;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.validation.AbstractValidationCheck;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.SemanticTypeComponent;
import com.wci.umls.server.model.meta.SemanticType;
import com.wci.umls.server.services.ContentService;

/**
 * Default checks that apply to all terminologies.
 */
public class ConceptSemanticTypesValid extends AbstractValidationCheck {

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
      
      List<SemanticType> stys = contentService.getSemanticTypes(c.getTerminology(), c.getVersion()).getObjects();
      
      for (SemanticTypeComponent styc : c.getSemanticTypes()) {
        boolean matchFound = false;
        for (final SemanticType sty : stys) {
          if (sty.getExpandedForm().equals(styc.getSemanticType())) {
            matchFound = true;
            break;
          }
        }
        if (!matchFound) {
          result.getErrors().add("Semantic type " + styc.getSemanticType() + " not valid");
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
    return "Concept Semantic Types Valid";
  }

}
