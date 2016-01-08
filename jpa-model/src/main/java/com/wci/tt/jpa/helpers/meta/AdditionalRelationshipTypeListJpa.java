/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.AdditionalRelationshipTypeList;
import com.wci.tt.jpa.meta.AdditionalRelationshipTypeJpa;
import com.wci.tt.model.meta.AdditionalRelationshipType;

/**
 * JAXB enabled implementation of {@link AdditionalRelationshipTypeList}.
 */
@XmlRootElement(name = "additionalRelationshipTypeList")
public class AdditionalRelationshipTypeListJpa extends
    AbstractResultList<AdditionalRelationshipType> implements
    AdditionalRelationshipTypeList {

  /* see superclass */
  @Override
  @XmlElement(type = AdditionalRelationshipTypeJpa.class, name = "type")
  public List<AdditionalRelationshipType> getObjects() {
    return super.getObjectsTransient();
  }

}
