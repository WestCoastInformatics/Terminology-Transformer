/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.IoHandlerInfo;
import com.wci.tt.helpers.IoHandlerInfoList;
import com.wci.tt.jpa.IoHandlerInfoJpa;

/**
 * JAXB enabled implementation of {@link IoHandlerInfoList}.
 */
@XmlRootElement(name = "handlerList")
public class IoHandlerInfoListJpa extends AbstractResultList<IoHandlerInfo>
    implements IoHandlerInfoList {

  /* see superclass */
  @Override
  @XmlElement(type = IoHandlerInfoJpa.class, name = "handlers")
  public List<IoHandlerInfo> getObjects() {
    return super.getObjectsTransient();
  }

}
