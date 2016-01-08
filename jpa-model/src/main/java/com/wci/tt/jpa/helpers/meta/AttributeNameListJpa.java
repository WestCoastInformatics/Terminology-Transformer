/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.AttributeNameList;
import com.wci.tt.jpa.meta.AttributeNameJpa;
import com.wci.tt.model.meta.AttributeName;

/**
 * JAXB enabled implementation of {@link AttributeNameList}.
 */
@XmlRootElement(name = "attributeNameList")
public class AttributeNameListJpa extends AbstractResultList<AttributeName>
    implements AttributeNameList {

  /* see superclass */
  @Override
  @XmlElement(type = AttributeNameJpa.class, name = "name")
  public List<AttributeName> getObjects() {
    return super.getObjectsTransient();
  }

}
