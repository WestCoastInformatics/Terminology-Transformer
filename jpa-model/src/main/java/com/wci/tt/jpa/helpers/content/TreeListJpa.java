/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.Tree;
import com.wci.tt.helpers.content.TreeList;

/**
 * JAXB enabled implementation of {@link TreeList}.
 */
@XmlRootElement(name = "treeList")
public class TreeListJpa extends AbstractResultList<Tree> implements TreeList {

  /* see superclass */
  @Override
  @XmlElement(type = TreeJpa.class, name = "tree")
  public List<Tree> getObjects() {
    return super.getObjectsTransient();
  }

}
