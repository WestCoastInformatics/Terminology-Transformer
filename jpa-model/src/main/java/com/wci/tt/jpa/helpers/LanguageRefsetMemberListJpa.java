/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.LanguageRefsetMemberList;
import com.wci.tt.rf2.LanguageRefsetMember;
import com.wci.tt.rf2.jpa.LanguageRefsetMemberJpa;

/**
 * JAXB enabled implementation of {@link LanguageRefsetMemberList}.
 */
@XmlRootElement(name = "languageRefsetMemberList")
public class LanguageRefsetMemberListJpa extends
    AbstractResultList<LanguageRefsetMember> implements
    LanguageRefsetMemberList {

  /* see superclass */
  @Override
  @XmlElement(type = LanguageRefsetMemberJpa.class, name = "members")
  public List<LanguageRefsetMember> getObjects() {
    return super.getObjectsTransient();
  }

}
