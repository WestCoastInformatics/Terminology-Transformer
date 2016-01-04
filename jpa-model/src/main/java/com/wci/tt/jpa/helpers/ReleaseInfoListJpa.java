/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.ReleaseInfo;
import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.ReleaseInfoList;
import com.wci.tt.jpa.ReleaseInfoJpa;

/**
 * JAXB-enabled implementation of {@link ReleaseInfoList}.
 */
@XmlRootElement(name = "releaseInfoList")
public class ReleaseInfoListJpa extends AbstractResultList<ReleaseInfo>
    implements ReleaseInfoList {

  /* see superclass */
  @Override
  @XmlElement(type = ReleaseInfoJpa.class, name = "releaseInfos")
  public List<ReleaseInfo> getObjects() {
    return super.getObjectsTransient();
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ReleaseInfoListJpa [releaseInfos=" + getObjects() + ", getCount()="
        + getCount() + "]";
  }

}
