/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.TreePositionList;
import com.wci.tt.jpa.content.AbstractTreePosition;
import com.wci.tt.model.content.AtomClass;
import com.wci.tt.model.content.TreePosition;

/**
 * JAXB enabled implementation of {@link TreePositionList}.
 */
@XmlRootElement(name = "treePositionList")
public class TreePositionListJpa extends
    AbstractResultList<TreePosition<? extends AtomClass>> implements
    TreePositionList {

  /* see superclass */
  @Override
  @XmlElement(type = AbstractTreePosition.class, name = "treepos")
  public List<TreePosition<? extends AtomClass>> getObjects() {
    return super.getObjectsTransient();
  }

}
