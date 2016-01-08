/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.GeneralConceptAxiomList;
import com.wci.tt.jpa.content.GeneralConceptAxiomJpa;
import com.wci.tt.model.content.GeneralConceptAxiom;

/**
 * JAXB enabled implementation of {@link GeneralConceptAxiomList}.
 */
@XmlRootElement(name = "generalConceptAxiomList")
public class GeneralConceptAxiomListJpa extends
    AbstractResultList<GeneralConceptAxiom> implements GeneralConceptAxiomList {

  /* see superclass */
  @Override
  @XmlElement(type = GeneralConceptAxiomJpa.class, name = "GeneralConceptAxiom")
  public List<GeneralConceptAxiom> getObjects() {
    return super.getObjectsTransient();
  }

}
