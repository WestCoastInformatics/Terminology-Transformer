/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.jpa.services.handlers.DefaultSearchHandler;
import com.wci.umls.server.jpa.services.validation.AbstractValidationCheck;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;
import com.wci.umls.server.services.handlers.SearchHandler;

/**
 * Default checks that apply to all terminologies.
 */
public class ConceptTermsUniqueAcrossFeatures extends AbstractValidationCheck {

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

    try {
      contentService = new ContentServiceJpa();

      final Set<Concept> matchingConcepts = new HashSet<>();

      SearchHandler handler = new DefaultSearchHandler();
      // getQueryResults(String terminology,
      // String version, String branch, String query, String literalField,
      // Class<T> clazz, PfsParameter pfs, int[] totalCt, EntityManager manager)

      // Cycle over atoms
      for (Atom atom : c.getAtoms()) {
        final String query = "NOT id:" + c.getId() + " AND atoms.nameNorm:\""
            + ConfigUtility.normalize(atom.getName()) + "\""
            + (c.getSemanticTypes() == null || c.getSemanticTypes().size() == 0
                ? "" : " AND NOT semanticTypes.semanticType:"
                    + c.getSemanticTypes().get(0).getSemanticType());
        List<ConceptJpa> matches = handler.getQueryResults(c.getTerminology(),
            c.getVersion(), c.getBranch(), query, "atoms.nameNorm",
            ConceptJpa.class, null, totalCt, contentService.getEntityManager());

        for (Concept match : matches) {
          matchingConcepts.add(match);
        }
      }

      for (Concept concept : matchingConcepts) {
        // find matching atoms
        for (Atom atom1 : c.getAtoms()) {
          if (atom1.getName() == null || atom1.getName().isEmpty()) {
            continue;
          }
          for (Atom atom2 : concept.getAtoms()) {
            if (atom1.getName().equals(atom2.getName())) {
              result.addError(
                  "Exact term match (" + atom1.getName() + ") in feature '"
                      + concept.getSemanticTypes().get(0).getSemanticType()
                      + "' to " + concept.getTerminologyId() + " "
                      + concept.getName());
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
    return "Concept Name Unique Across Features";
  }

}
