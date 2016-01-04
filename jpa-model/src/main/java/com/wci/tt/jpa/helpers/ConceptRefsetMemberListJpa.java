/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.ConceptRefsetMemberList;
import com.wci.tt.rf2.ConceptRefsetMember;
import com.wci.tt.rf2.jpa.ConceptRefsetMemberJpa;

/**
 * JAXB enabled implementation of {@link ConceptRefsetMemberList}.
 */
@XmlRootElement(name = "conceptRefsetMemberList")
public class ConceptRefsetMemberListJpa extends
    AbstractResultList<ConceptRefsetMember> implements ConceptRefsetMemberList {

  /* see superclass */
  @Override
  @XmlElement(type = ConceptRefsetMemberJpa.class, name = "members")
  public List<ConceptRefsetMember> getObjects() {
    return super.getObjectsTransient();
  }
}
