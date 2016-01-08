/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.CodeList;
import com.wci.tt.jpa.content.CodeJpa;
import com.wci.tt.model.content.Code;

/**
 * JAXB enabled implementation of {@link CodeList}.
 */
@XmlRootElement(name = "codeList")
public class CodeListJpa extends AbstractResultList<Code> implements CodeList {

  /* see superclass */
  @Override
  @XmlElement(type = CodeJpa.class, name = "code")
  public List<Code> getObjects() {
    return super.getObjectsTransient();
  }

}
