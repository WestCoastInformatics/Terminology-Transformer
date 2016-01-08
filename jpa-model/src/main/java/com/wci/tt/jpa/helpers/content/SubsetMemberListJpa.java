/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers.content;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.content.SubsetMemberList;
import com.wci.tt.jpa.content.AbstractSubsetMember;
import com.wci.tt.model.content.ComponentHasAttributesAndName;
import com.wci.tt.model.content.Subset;
import com.wci.tt.model.content.SubsetMember;

/**
 * JAXB enabled implementation of {@link SubsetMemberList}.
 */
@XmlRootElement(name = "subsetMemberList")
public class SubsetMemberListJpa
    extends
    AbstractResultList<SubsetMember<? extends ComponentHasAttributesAndName, ? extends Subset>>
    implements SubsetMemberList {


  /* see superclass */
  @Override
  @XmlElement(type = AbstractSubsetMember.class, name = "member")
  public List<SubsetMember<? extends ComponentHasAttributesAndName, ? extends Subset>> getObjects() {
    return super.getObjectsTransient();
  }

}
