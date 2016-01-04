/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.Terminology;
import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.TerminologyList;
import com.wci.tt.jpa.TerminologyJpa;

/**
 * JAXB enabled implementation of {@link TerminologyList}.
 */
@XmlRootElement(name = "projectList")
public class TerminologyListJpa extends AbstractResultList<Terminology>
    implements TerminologyList {

  /* see superclass */
  @Override
  @XmlElement(type = TerminologyJpa.class, name = "translations")
  public List<Terminology> getObjects() {
    return super.getObjectsTransient();
  }

}
