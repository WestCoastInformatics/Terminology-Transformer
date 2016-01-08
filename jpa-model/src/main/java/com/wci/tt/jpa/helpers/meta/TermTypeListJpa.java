/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.TermTypeList;
import com.wci.tt.jpa.meta.TermTypeJpa;
import com.wci.tt.model.meta.TermType;

/**
 * JAXB enabled implementation of {@link TermTypeList}.
 */
@XmlRootElement(name = "termTypeList")
public class TermTypeListJpa extends AbstractResultList<TermType> implements
    TermTypeList {

  /* see superclass */
  @Override
  @XmlElement(type = TermTypeJpa.class, name = "type")
  public List<TermType> getObjects() {
    return super.getObjectsTransient();
  }

}
