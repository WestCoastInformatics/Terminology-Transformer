/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.SubsetList;
import com.wci.tt.jpa.content.AbstractSubset;
import com.wci.tt.model.content.Subset;

/**
 * JAXB enabled implementation of {@link SubsetList}.
 */
@XmlRootElement(name = "subsetList")
public class SubsetListJpa extends AbstractResultList<Subset> implements
    SubsetList {

  /* see superclass */
  @Override
  @XmlElement(type = AbstractSubset.class, name = "subset")
  public List<Subset> getObjects() {
    return super.getObjectsTransient();
  }

}
