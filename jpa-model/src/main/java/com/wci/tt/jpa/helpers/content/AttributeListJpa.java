/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.AttributeList;
import com.wci.tt.jpa.content.AttributeJpa;
import com.wci.tt.model.content.Attribute;

/**
 * JAXB enabled implementation of {@link AttributeList}.
 */
@XmlRootElement(name = "attributeList")
public class AttributeListJpa extends AbstractResultList<Attribute> implements
    AttributeList {

  /* see superclass */
  @Override
  @XmlElement(type = AttributeJpa.class, name = "attribute")
  public List<Attribute> getObjects() {
    return super.getObjectsTransient();
  }

}
