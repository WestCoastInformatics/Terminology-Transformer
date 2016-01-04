/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.User;
import com.wci.tt.helpers.AbstractResultList;
import com.wci.tt.helpers.UserList;
import com.wci.tt.jpa.UserJpa;

/**
 * JAXB enabled implementation of {@link UserList}.
 */
@XmlRootElement(name = "userList")
public class UserListJpa extends AbstractResultList<User> implements UserList {

  /* see superclass */
  @Override
  @XmlElement(type = UserJpa.class, name = "users")
  public List<User> getObjects() {
    return super.getObjectsTransient();
  }

}
