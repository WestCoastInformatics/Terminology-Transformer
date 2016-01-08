/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.LabelSetList;
import com.wci.tt.jpa.meta.LabelSetJpa;
import com.wci.tt.model.meta.LabelSet;

/**
 * JAXB enabled implementation of {@link LabelSetList}.
 */
@XmlRootElement(name = "labelSetList")
public class LabelSetListJpa extends AbstractResultList<LabelSet> implements
    LabelSetList {

  /* see superclass */
  @Override
  @XmlElement(type = LabelSetJpa.class, name = "name")
  public List<LabelSet> getObjects() {
    return super.getObjectsTransient();
  }

}
