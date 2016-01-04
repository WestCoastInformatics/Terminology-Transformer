/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.DescriptionTypeList;
import com.wci.tt.rf2.DescriptionType;
import com.wci.tt.rf2.jpa.DescriptionTypeJpa;

/**
 * JAXB enabled implementation of {@link DescriptionTypeList}.
 */
@XmlRootElement(name = "descriptionTypeList")
public class DescriptionTypeListJpa extends
    AbstractResultList<DescriptionType> implements
    DescriptionTypeList {

  /* see superclass */
  @Override
  @XmlElement(type = DescriptionTypeJpa.class, name = "types")
  public List<DescriptionType> getObjects() {
    return super.getObjectsTransient();
  }

}
