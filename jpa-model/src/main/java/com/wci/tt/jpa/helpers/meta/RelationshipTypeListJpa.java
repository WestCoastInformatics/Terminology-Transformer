/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.RelationshipTypeList;
import com.wci.tt.jpa.meta.RelationshipTypeJpa;
import com.wci.tt.model.meta.RelationshipType;

/**
 * JAXB enabled implementation of {@link RelationshipTypeList}.
 */
@XmlRootElement(name = "relationshipTypeList")
public class RelationshipTypeListJpa extends
    AbstractResultList<RelationshipType> implements RelationshipTypeList {

  /* see superclass */
  @Override
  @XmlElement(type = RelationshipTypeJpa.class, name = "type")
  public List<RelationshipType> getObjects() {
    return super.getObjectsTransient();
  }

}
