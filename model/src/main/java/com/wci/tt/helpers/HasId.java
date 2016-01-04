/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

/**
 * Represents a thing that has an id.
 */
public interface HasId {

  /**
   * Returns the id.
   *
   * @return the id
   */
  public Long getId();

  /**
   * Sets the id.
   *
   * @param id the id
   */
  public void setId(Long id);

}
