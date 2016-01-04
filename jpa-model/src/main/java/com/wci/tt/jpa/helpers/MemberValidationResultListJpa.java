/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.MemberValidationResult;
import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.MemberValidationResultList;
import com.wci.tt.jpa.MemberValidationResultJpa;

/**
 * JAXB enabled implementation of
 * {@link MemberValidationResultListJpa}.
 */
@XmlRootElement(name = "memberValidationList")
public class MemberValidationResultListJpa extends
    AbstractResultList<MemberValidationResult> implements
    MemberValidationResultList {

  /* see superclass */
  @Override
  @XmlElement(type = MemberValidationResultJpa.class, name = "results")
  public List<MemberValidationResult> getObjects() {
    return super.getObjectsTransient();
  }

}
