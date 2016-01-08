/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.AtomList;
import com.wci.tt.jpa.content.AtomJpa;
import com.wci.tt.model.content.Atom;

/**
 * JAXB enabled implementation of {@link AtomList}.
 */
@XmlRootElement(name = "atomList")
public class AtomListJpa extends AbstractResultList<Atom> implements AtomList {

  /* see superclass */
  @Override
  @XmlElement(type = AtomJpa.class, name = "atom")
  public List<Atom> getObjects() {
    return super.getObjectsTransient();
  }

}
