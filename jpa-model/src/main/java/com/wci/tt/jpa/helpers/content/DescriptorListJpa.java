/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.DescriptorList;
import com.wci.tt.jpa.content.DescriptorJpa;
import com.wci.tt.model.content.Descriptor;

/**
 * JAXB enabled implementation of {@link DescriptorList}.
 */
@XmlRootElement(name = "descriptorList")
public class DescriptorListJpa extends AbstractResultList<Descriptor> implements
    DescriptorList {


  /* see superclass */
  @Override
  @XmlElement(type = DescriptorJpa.class, name = "descriptor")
  public List<Descriptor> getObjects() {
    return super.getObjectsTransient();
  }

}
