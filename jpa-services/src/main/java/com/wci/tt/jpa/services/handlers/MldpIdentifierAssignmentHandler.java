/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.Properties;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;

import com.wci.umls.server.helpers.ComponentInfo;
import com.wci.umls.server.jpa.AbstractConfigurable;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Attribute;
import com.wci.umls.server.model.content.Code;
import com.wci.umls.server.model.content.ComponentHasAttributes;
import com.wci.umls.server.model.content.ComponentHasAttributesAndName;
import com.wci.umls.server.model.content.ComponentHasDefinitions;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.Definition;
import com.wci.umls.server.model.content.Descriptor;
import com.wci.umls.server.model.content.LexicalClass;
import com.wci.umls.server.model.content.MapSet;
import com.wci.umls.server.model.content.Mapping;
import com.wci.umls.server.model.content.Relationship;
import com.wci.umls.server.model.content.SemanticTypeComponent;
import com.wci.umls.server.model.content.StringClass;
import com.wci.umls.server.model.content.Subset;
import com.wci.umls.server.model.content.SubsetMember;
import com.wci.umls.server.model.content.TransitiveRelationship;
import com.wci.umls.server.model.content.TreePosition;
import com.wci.umls.server.services.handlers.IdentifierAssignmentHandler;

/**
 * Default implementation of {@link IdentifierAssignmentHandler}. This supports
 * "application-managed" identifier assignment.
 */
public class MldpIdentifierAssignmentHandler extends AbstractConfigurable
    implements IdentifierAssignmentHandler {

  /** The max concept id. */
  private long maxConceptId = -1;

  private long maxAtomId = -1;

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // do nothing
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Concept concept) throws Exception {
    // Unpublishable concepts don't get assigned ids
    if (!concept.isPublishable()) {
      return concept.getTerminologyId();
    }

    long conceptId = 1000L;
    // If this is the first time this is called, lookup max ID from the database
    if (maxConceptId == -1) {
      final ContentServiceJpa service = new ContentServiceJpa();
      try {
        final javax.persistence.Query query = service.getEntityManager()
            .createQuery("select max(cast(terminologyId as long)) from ConceptJpa");
       
        Long result = (Long) query.getSingleResult();
        if (result != null) {
          conceptId = result;
        }
      } catch (NoResultException e) {
        // do nothing, use conceptId above
      } finally {
        service.close();
      }
      // Set the maxConceptId
      maxConceptId = conceptId;
      Logger.getLogger(getClass())
          .info("Initializing max concept id = " + maxConceptId);
    }
    final long result = ++maxConceptId;
    return Long.toString(result);
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Descriptor descriptor) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Code code) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(StringClass stringClass) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(LexicalClass lexicalClass) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Atom atom) throws Exception {
    // Unpublishable atoms don't get assigned ids
    if (!atom.isPublishable()) {
      return atom.getTerminologyId();
    }

    long atomId = 1000;
    // If this is the first time this is called, lookup max ID from the database
    if (maxAtomId == -1) {
      final ContentServiceJpa service = new ContentServiceJpa();
      try {
        final javax.persistence.Query query = service.getEntityManager()
            .createQuery("select max(cast(terminologyId as long)) from AtomJpa");
      
        Long result = (Long) query.getSingleResult();
        if (result != null) {
          atomId = result;
        }
      } catch (NoResultException e) {
        // do nothing, use atomId above
      } finally {
        service.close();
      }
      // Set the maxConceptId
      maxAtomId = atomId;
      Logger.getLogger(getClass())
          .info("Initializing max atom id = " + maxAtomId);
    }
    final long result = ++maxAtomId;
    return Long.toString(result);
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Attribute attribute, ComponentInfo component)
    throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Definition definition,
    ComponentHasDefinitions component) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(
    Relationship<? extends ComponentInfo, ? extends ComponentInfo> relationship,
    String inverseRelType, String inverseAdditionalRelType) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getInverseTerminologyId(
    Relationship<? extends ComponentInfo, ? extends ComponentInfo> relationship,
    String inverseRelType, String inverseAdditionalRelType) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(
    TransitiveRelationship<? extends ComponentHasAttributes> relationship)
    throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(
    TreePosition<? extends ComponentHasAttributesAndName> treepos)
    throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Subset subset) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(
    SubsetMember<? extends ComponentHasAttributes, ? extends Subset> member)
    throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(SemanticTypeComponent semanticTypeComponent,
    Concept concept) throws Exception {
    return semanticTypeComponent == null ? null
        : semanticTypeComponent.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Mapping mapping) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(MapSet mapSet) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public boolean allowIdChangeOnUpdate() {
    return false;
  }

  /* see superclass */
  @Override
  public boolean allowConceptIdChangeOnUpdate() {
    return false;
  }

  /* see superclass */
  @Override
  public String getName() {
    return "MLDP Id Assignment Handler";
  }

  /* see superclass */
  @Override
  public void commit() throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public void rollback() throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public void beginTransaction() throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public void clear() throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public void commitClearBegin() throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public void logAndCommit(int objectCt, int logCt, int commitCt)
    throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public void silentIntervalCommit(int objectCt, int logCt, int commitCt)
    throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean getTransactionPerOperation() throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setTransactionPerOperation(boolean arg0) throws Exception {
    throw new UnsupportedOperationException();
  }
}
