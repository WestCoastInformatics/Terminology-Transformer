/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.SemanticTypeComponentList;
import com.wci.tt.jpa.content.SemanticTypeComponentJpa;
import com.wci.tt.model.content.SemanticTypeComponent;

/**
 * JAXB enabled implementation of {@link SemanticTypeComponentList}.
 */
@XmlRootElement(name = "semanticTypeComponentList")
public class SemanticTypeComponentListJpa extends
    AbstractResultList<SemanticTypeComponent> implements
    SemanticTypeComponentList {


  /* see superclass */
  @Override
  @XmlElement(type = SemanticTypeComponentJpa.class, name = "semanticTypeComponent")
  public List<SemanticTypeComponent> getObjects() {
    return super.getObjectsTransient();
  }

}
