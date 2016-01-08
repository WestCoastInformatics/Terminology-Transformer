/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.meta.ReleasePropertyList;
import com.wci.tt.jpa.content.ReleasePropertyJpa;
import com.wci.tt.model.meta.ReleaseProperty;

/**
 * JAXB-enabled implementation of {@link ReleasePropertyList}.
 */
@XmlRootElement(name = "releasePropertyList")
public class ReleasePropertyListJpa extends AbstractResultList<ReleaseProperty>
    implements ReleasePropertyList {

  /* see superclass */
  @Override
  @XmlElement(type = ReleasePropertyJpa.class, name = "releaseProperty")
  public List<ReleaseProperty> getObjects() {
    return super.getObjectsTransient();
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ReleasePropertyListJpa [releaseProperties =" + getObjects()
        + ", getCount()=" + getCount() + "]";
  }

}
