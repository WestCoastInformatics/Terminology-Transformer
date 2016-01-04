/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.ConceptValidationResult;
import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.ConceptValidationResultList;
import com.wci.tt.jpa.ConceptValidationResultJpa;

/**
 * JAXB enabled implementation of {@link ConceptValidationResultListJpa}.
 */
@XmlRootElement(name = "conceptValidationList")
public class ConceptValidationResultListJpa extends
    AbstractResultList<ConceptValidationResult> implements
    ConceptValidationResultList {

  /* see superclass */
  @Override
  @XmlElement(type = ConceptValidationResultJpa.class, name = "results")
  public List<ConceptValidationResult> getObjects() {
    return super.getObjectsTransient();
  }

}
