/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.RelationshipList;
import com.wci.tt.jpa.content.AbstractRelationship;
import com.wci.tt.model.content.ComponentHasAttributes;
import com.wci.tt.model.content.Relationship;

/**
 * JAXB enabled implementation of {@link RelationshipList}.
 */
@XmlRootElement(name = "relationshipList")
public class RelationshipListJpa
    extends
    AbstractResultList<Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes>>
    implements RelationshipList {

  /* see superclass */
  @Override
  @XmlElement(type = AbstractRelationship.class, name = "relationship")
  public List<Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes>> getObjects() {
    return super.getObjectsTransient();
  }

}
