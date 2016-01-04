/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.RelationshipList;
import com.wci.tt.rf2.Relationship;
import com.wci.tt.rf2.jpa.RelationshipJpa;

/**
 * JAXB enabled implementation of {@link RelationshipList}.
 */
@XmlRootElement(name = "relationshipList")
public class RelationshipListJpa extends AbstractResultList<Relationship>
    implements RelationshipList {

  /* see superclass */
  @Override
  @XmlElement(type = RelationshipJpa.class, name = "relationships")
  public List<Relationship> getObjects() {
    return super.getObjectsTransient();
  }

}
