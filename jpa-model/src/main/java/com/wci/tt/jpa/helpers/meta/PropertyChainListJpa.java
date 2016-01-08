/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.PropertyChainList;
import com.wci.tt.jpa.meta.PropertyChainJpa;
import com.wci.tt.model.meta.PropertyChain;

/**
 * JAXB enabled implementation of {@link PropertyChainList}.
 */
@XmlRootElement(name = "propertyChainList")
public class PropertyChainListJpa extends AbstractResultList<PropertyChain>
    implements PropertyChainList {

  /* see superclass */
  @Override
  @XmlElement(type = PropertyChainJpa.class, name = "type")
  public List<PropertyChain> getObjects() {
    return super.getObjectsTransient();
  }

}
