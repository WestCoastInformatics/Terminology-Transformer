/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.workflow;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.workflow.TrackingRecord;
import com.wci.tt.workflow.TrackingRecordList;

/**
 * JAXB enabled implementation of {@link TrackingRecordList}.
 */
@XmlRootElement(name = "projectList")
public class TrackingRecordListJpa extends AbstractResultList<TrackingRecord>
    implements TrackingRecordList {

  /* see superclass */
  @Override
  @XmlElement(type = TrackingRecordJpa.class, name = "records")
  public List<TrackingRecord> getObjects() {
    return super.getObjectsTransient();
  }

}
