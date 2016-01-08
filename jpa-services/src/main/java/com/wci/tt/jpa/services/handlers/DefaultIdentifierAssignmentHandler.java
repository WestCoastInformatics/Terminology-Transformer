/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.Properties;

import com.wci.tt.model.content.Atom;
import com.wci.tt.model.content.Attribute;
import com.wci.tt.model.content.Code;
import com.wci.tt.model.content.ComponentHasAttributes;
import com.wci.tt.model.content.ComponentHasAttributesAndName;
import com.wci.tt.model.content.ComponentHasDefinitions;
import com.wci.tt.model.content.Concept;
import com.wci.tt.model.content.Definition;
import com.wci.tt.model.content.Descriptor;
import com.wci.tt.model.content.LexicalClass;
import com.wci.tt.model.content.Relationship;
import com.wci.tt.model.content.SemanticTypeComponent;
import com.wci.tt.model.content.StringClass;
import com.wci.tt.model.content.Subset;
import com.wci.tt.model.content.SubsetMember;
import com.wci.tt.model.content.TransitiveRelationship;
import com.wci.tt.model.content.TreePosition;
import com.wci.tt.services.handlers.IdentifierAssignmentHandler;

/**
 * Default implementation of {@link IdentifierAssignmentHandler}. This supports
 * "application-managed" identifier assignment.
 */
public class DefaultIdentifierAssignmentHandler implements
    IdentifierAssignmentHandler {

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Concept concept) throws Exception {
    return concept.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Descriptor descriptor) throws Exception {
    return descriptor.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Code code) throws Exception {
    return code.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(StringClass stringClass) throws Exception {
    return stringClass.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(LexicalClass lexicalClass) throws Exception {
    return lexicalClass.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Atom atom) throws Exception {
    return atom.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Attribute attribute,
    ComponentHasAttributes component) throws Exception {
    return attribute.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Definition definition,
    ComponentHasDefinitions component) throws Exception {
    return definition.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(
    Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes> relationship)
    throws Exception {
    return relationship.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(
    TransitiveRelationship<? extends ComponentHasAttributes> relationship)
    throws Exception {
    return relationship.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(
    TreePosition<? extends ComponentHasAttributesAndName> treepos)
    throws Exception {
    return treepos.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(Subset subset) throws Exception {
    return subset.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(
    SubsetMember<? extends ComponentHasAttributes, ? extends Subset> member)
    throws Exception {
    return member.getTerminologyId();
  }

  /* see superclass */
  @Override
  public String getTerminologyId(SemanticTypeComponent semanticTypeComponent,
    Concept concept) throws Exception {
    return semanticTypeComponent.getTerminologyId();
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

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

}
