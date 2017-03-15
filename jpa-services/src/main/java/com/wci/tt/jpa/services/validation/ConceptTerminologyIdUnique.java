/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.handlers.DefaultSearchHandler;
import com.wci.umls.server.jpa.services.validation.AbstractValidationCheck;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;
import com.wci.umls.server.services.handlers.SearchHandler;

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
    ContentServiceJpa contentService = null;
    int totalCt[] = new int[1];

    if (c.getTerminologyId() == null || c.getTerminologyId().isEmpty()) {
      result.getErrors().add("Terminology id null or empty");
      return result;
    }
    try {
      contentService = new ContentServiceJpa();
      SearchHandler handler = new DefaultSearchHandler();
      // getQueryResults(String terminology,
      // String version, String branch, String query, String literalField,
      // Class<T> clazz, PfsParameter pfs, int[] totalCt, EntityManager manager)

      // Cycle over atoms

      List<ConceptJpa> matches = handler
          .getQueryResults(c.getTerminology(), c.getVersion(), c.getBranch(),
              "NOT id:" + c.getId() + " AND terminologyId:"
                  + c.getTerminologyId(),
              "terminologyId", ConceptJpa.class, null, totalCt,
              contentService.getEntityManager());

      if (matches.size() > 0) {
        for (Concept concept : matches) {
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
  public Set<Long> validateConcepts(Set<Long> conceptIds, String terminology,
    String version, ContentService service) throws Exception {
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
