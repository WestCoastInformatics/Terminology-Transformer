/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.ModuleDependencyRefsetMemberList;
import com.wci.tt.rf2.ModuleDependencyRefsetMember;
import com.wci.tt.rf2.jpa.ModuleDependencyRefsetMemberJpa;

/**
 * JAXB enabled implementation of {@link ModuleDependencyRefsetMemberList}.
 */
@XmlRootElement(name = "moduleDependencyRefsetMemberList")
public class ModuleDependencyRefsetMemberListJpa extends
    AbstractResultList<ModuleDependencyRefsetMember> implements
    ModuleDependencyRefsetMemberList {

  /* see superclass */
  @Override
  @XmlElement(type = ModuleDependencyRefsetMemberJpa.class, name = "members")
  public List<ModuleDependencyRefsetMember> getObjects() {
    return super.getObjectsTransient();
  }

}
