/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;

import com.wci.tt.model.content.Atom;
import com.wci.tt.model.content.AtomRelationship;
import com.wci.tt.model.content.AtomSubsetMember;
import com.wci.tt.model.content.Concept;
import com.wci.tt.model.content.ConceptRelationship;
import com.wci.tt.model.content.ConceptSubsetMember;
import com.wci.tt.model.content.Definition;
import com.wci.tt.model.content.SemanticTypeComponent;
import com.wci.tt.services.handlers.GraphResolutionHandler;

/**
 * Default implementation of {@link GraphResolutionHandler}. This connects
 * graphs at the level at which CascadeType.ALL is used in the data model.
 */
public class UmlsGraphResolutionHandler extends DefaultGraphResolutionHandler {

  @Override
  public void resolve(Concept concept) throws Exception {
    if (concept != null) {
      boolean nullId = concept.getId() == null;
      concept.setMembers(new ArrayList<ConceptSubsetMember>());

      concept.getLabels();

      // Attributes
      resolveAttributes(concept, nullId);

      // Definitions
      for (Definition def : concept.getDefinitions()) {
        resolveDefinition(def, nullId);
      }

      // Semantic type components
      for (SemanticTypeComponent sty : concept.getSemanticTypes()) {
        if (nullId) {
          sty.setId(null);
        }
        sty.getSemanticType();
        resolve(sty);
      }

      // Atoms
      for (Atom atom : concept.getAtoms()) {
        // if the concept is "new", then the atom must be too
        if (nullId) {
          atom.setId(null);
        }
        resolve(atom);
      }

      // Relationships
      // Default behavior -- do not return relationships, require paging calls
      concept.setRelationships(new ArrayList<ConceptRelationship>());

    } else if (concept == null) {
      throw new Exception("Cannot resolve a null concept.");
    }
  }

  @Override
  public void resolve(Atom atom) throws Exception {
    if (atom != null) {
      boolean nullId = atom.getId() == null;
      atom.setMembers(new ArrayList<AtomSubsetMember>());

      atom.getName();
      atom.getConceptTerminologyIds().keySet();
      atom.getAlternateTerminologyIds().keySet();

      // Attributes
      resolveAttributes(atom, nullId);

      // Definitions
      for (Definition def : atom.getDefinitions()) {
        resolveDefinition(def, nullId);
      }

      // for UMLS view don't read relationship sas these are teminology-specific
      // rels
      // they can show when browsing that terminology
      atom.setRelationships(new ArrayList<AtomRelationship>());

    } else if (atom == null) {
      throw new Exception("Cannot resolve a null atom.");
    }

  }

}
