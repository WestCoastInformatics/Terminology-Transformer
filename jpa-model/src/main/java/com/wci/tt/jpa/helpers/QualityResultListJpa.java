/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.QualityResult;
import com.wci.tt.helpers.QualityResultList;

/**
 * JAXB-enabled implementation of {@link QualityResultList}.
 */
@XmlRootElement(name = "qualityResultList")
public class QualityResultListJpa extends AbstractResultList<QualityResult>
    implements QualityResultList {

  /* see superclass */
  @Override
  @XmlElement(type = QualityResultJpa.class, name = "results")
  public List<QualityResult> getObjects() {
    return super.getObjectsTransient();
  }

  /* see superclass */
  @Override
  public String toString() {
    return "QualityResultListJpa [qualityResults=" + getObjects()
        + ", getCount()=" + getCount() + "]";
  }

}
