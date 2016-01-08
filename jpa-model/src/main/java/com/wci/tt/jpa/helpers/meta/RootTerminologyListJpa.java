/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.RootTerminologyList;
import com.wci.tt.jpa.meta.RootTerminologyJpa;
import com.wci.tt.model.meta.RootTerminology;

/**
 * JAXB enabled implementation of {@link RootTerminologyList}.
 */
@XmlRootElement(name = "rootTerminologyList")
public class RootTerminologyListJpa extends AbstractResultList<RootTerminology>
    implements RootTerminologyList {

  /* see superclass */
  @Override
  @XmlElement(type = RootTerminologyJpa.class, name = "rootTerminology")
  public List<RootTerminology> getObjects() {
    return super.getObjectsTransient();
  }

}
