/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.DescriptionList;
import com.wci.tt.rf2.Description;
import com.wci.tt.rf2.jpa.DescriptionJpa;

/**
 * JAXB enabled implementation of {@link DescriptionList}.
 */
@XmlRootElement(name = "descriptionList")
public class DescriptionListJpa extends AbstractResultList<Description>
    implements DescriptionList {

  /* see superclass */
  @Override
  @XmlElement(type = DescriptionJpa.class, name = "descriptions")
  public List<Description> getObjects() {
    return super.getObjectsTransient();
  }

}
