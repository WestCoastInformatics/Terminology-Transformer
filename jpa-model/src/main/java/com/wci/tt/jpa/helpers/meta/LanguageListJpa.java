/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.LanguageList;
import com.wci.tt.jpa.meta.LanguageJpa;
import com.wci.tt.model.meta.Language;

/**
 * JAXB enabled implementation of {@link LanguageList}.
 */
@XmlRootElement(name = "languageList")
public class LanguageListJpa extends AbstractResultList<Language> implements
    LanguageList {

  /* see superclass */
  @Override
  @XmlElement(type = LanguageJpa.class, name = "type")
  public List<Language> getObjects() {
    return super.getObjectsTransient();
  }

}
