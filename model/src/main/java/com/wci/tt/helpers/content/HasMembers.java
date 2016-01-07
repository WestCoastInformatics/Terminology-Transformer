/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import java.util.List;

import com.wci.tt.model.content.ComponentHasAttributesAndName;
import com.wci.tt.model.content.Subset;
import com.wci.tt.model.content.SubsetMember;

/**
 * Represents a thing that has definitions.
 * @param <T>
 */
public interface HasMembers<T extends SubsetMember<? extends ComponentHasAttributesAndName, ? extends Subset>> {

  /**
   * Returns the members.
   * 
   * @return the members
   */
  public List<T> getMembers();

  /**
   * Sets the members.
   * 
   * @param members the members
   */
  public void setMembers(List<T> members);

  /**
   * Adds a member.
   * 
   * @param member the member
   */
  public void addMember(T member);

  /**
   * Removes the member.
   *
   * @param member the member
   */
  public void removeMember(T member);
}