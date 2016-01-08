/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.LexicalClassList;
import com.wci.tt.jpa.content.LexicalClassJpa;
import com.wci.tt.model.content.LexicalClass;

/**
 * JAXB enabled implementation of {@link LexicalClassList}.
 */
@XmlRootElement(name = "lexicalClassList")
public class LexicalClassListJpa extends AbstractResultList<LexicalClass>
    implements LexicalClassList {


  /* see superclass */
  @Override
  @XmlElement(type = LexicalClassJpa.class, name = "lexicalClass")
  public List<LexicalClass> getObjects() {
    return super.getObjectsTransient();
  }

}
