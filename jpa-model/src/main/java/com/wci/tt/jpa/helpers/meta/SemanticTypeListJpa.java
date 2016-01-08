/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.SemanticTypeList;
import com.wci.tt.jpa.meta.SemanticTypeJpa;
import com.wci.tt.model.meta.SemanticType;

/**
 * JAXB enabled implementation of {@link SemanticTypeList}.
 */
@XmlRootElement(name = "semanticTypeList")
public class SemanticTypeListJpa extends AbstractResultList<SemanticType>
    implements SemanticTypeList {

  /* see superclass */
  @Override
  @XmlElement(type = SemanticTypeJpa.class, name = "type")
  public List<SemanticType> getObjects() {
    return super.getObjectsTransient();
  }

}
