/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.RefsetDescriptorRefsetMemberList;
import com.wci.tt.rf2.RefsetDescriptorRefsetMember;
import com.wci.tt.rf2.jpa.RefsetDescriptorRefsetMemberJpa;

/**
 * JAXB enabled implementation of {@link RefsetDescriptorRefsetMemberList}.
 */
@XmlRootElement(name = "refsetDescriptorRefsetMemberList")
public class RefsetDescriptorRefsetMemberListJpa extends
    AbstractResultList<RefsetDescriptorRefsetMember> implements
    RefsetDescriptorRefsetMemberList {

  /* see superclass */
  @Override
  @XmlElement(type = RefsetDescriptorRefsetMemberJpa.class, name = "refsets")
  public List<RefsetDescriptorRefsetMember> getObjects() {
    return super.getObjectsTransient();
  }

}
