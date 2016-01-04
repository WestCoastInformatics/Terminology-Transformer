/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.Refset;
import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.RefsetList;
import com.wci.tt.jpa.RefsetJpa;

/**
 * JAXB enabled implementation of {@link RefsetList}.
 */
@XmlRootElement(name = "refsetList")
public class RefsetListJpa extends AbstractResultList<Refset> implements
    RefsetList {

  /* see superclass */
  @Override
  @XmlElement(type = RefsetJpa.class, name = "refsets")
  public List<Refset> getObjects() {
    return super.getObjectsTransient();
  }

}
