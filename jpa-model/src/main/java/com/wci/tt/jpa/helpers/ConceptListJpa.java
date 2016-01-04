/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.ConceptList;
import com.wci.tt.rf2.Concept;
import com.wci.tt.rf2.jpa.ConceptJpa;

/**
 * JAXB enabled implementation of {@link ConceptList}.
 */
@XmlRootElement(name = "conceptList")
public class ConceptListJpa extends AbstractResultList<Concept> implements
    ConceptList {

  /* see superclass */
  @Override
  @XmlElement(type = ConceptJpa.class, name = "concepts")
  public List<Concept> getObjects() {
    return super.getObjectsTransient();
  }

}
