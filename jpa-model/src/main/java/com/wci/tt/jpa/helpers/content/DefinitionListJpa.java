/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.DefinitionList;
import com.wci.tt.jpa.content.DefinitionJpa;
import com.wci.tt.model.content.Definition;

/**
 * JAXB enabled implementation of {@link DefinitionList}.
 */
@XmlRootElement(name = "definitionList")
public class DefinitionListJpa extends AbstractResultList<Definition> implements
    DefinitionList {

  /* see superclass */
  @Override
  @XmlElement(type = DefinitionJpa.class, name = "definition")
  public List<Definition> getObjects() {
    return super.getObjectsTransient();
  }

}
