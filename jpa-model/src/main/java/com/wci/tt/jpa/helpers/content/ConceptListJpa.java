/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.ConceptList;
import com.wci.tt.jpa.content.ConceptJpa;
import com.wci.tt.model.content.Concept;

/**
 * JAXB enabled implementation of {@link ConceptList}.
 */
@XmlRootElement(name = "conceptList")
public class ConceptListJpa extends AbstractResultList<Concept> implements
    ConceptList {

  /* see superclass */
  @Override
  @XmlElement(type = ConceptJpa.class, name = "concept")
  public List<Concept> getObjects() {
    return super.getObjectsTransient();
  }

}
