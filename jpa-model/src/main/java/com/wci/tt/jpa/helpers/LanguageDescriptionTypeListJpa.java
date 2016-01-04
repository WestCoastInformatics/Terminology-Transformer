/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.LanguageDescriptionTypeList;
import com.wci.tt.rf2.LanguageDescriptionType;
import com.wci.tt.rf2.jpa.LanguageDescriptionTypeJpa;

/**
 * JAXB enabled implementation of {@link LanguageDescriptionTypeList}.
 */
@XmlRootElement(name = "languageDescriptionTypeList")
public class LanguageDescriptionTypeListJpa extends
    AbstractResultList<LanguageDescriptionType> implements
    LanguageDescriptionTypeList {

  /* see superclass */
  @Override
  @XmlElement(type = LanguageDescriptionTypeJpa.class, name = "types")
  public List<LanguageDescriptionType> getObjects() {
    return super.getObjectsTransient();
  }

}
