/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.Project;
import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.ProjectList;
import com.wci.tt.jpa.ProjectJpa;

/**
 * JAXB enabled implementation of {@link ProjectList}.
 */
@XmlRootElement(name = "projectList")
public class ProjectListJpa extends AbstractResultList<Project> implements
    ProjectList {

  /* see superclass */
  @Override
  @XmlElement(type = ProjectJpa.class, name = "projects")
  public List<Project> getObjects() {
    return super.getObjectsTransient();
  }

}
