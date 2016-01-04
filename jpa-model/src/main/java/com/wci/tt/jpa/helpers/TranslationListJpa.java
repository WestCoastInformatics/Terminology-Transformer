/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.Translation;
import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.TranslationList;
import com.wci.tt.jpa.TranslationJpa;

/**
 * JAXB enabled implementation of {@link TranslationList}.
 */
@XmlRootElement(name = "projectList")
public class TranslationListJpa extends AbstractResultList<Translation> implements
    TranslationList {

  /* see superclass */
  @Override
  @XmlElement(type = TranslationJpa.class, name = "translations")
  public List<Translation> getObjects() {
    return super.getObjectsTransient();
  }

}
