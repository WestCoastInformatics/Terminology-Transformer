/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt;

/**
 * Represents a user.
 */
public interface UserPreferences {

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

  /**
   * Returns the user.
   *
   * @return the user
   */
  public User getUser();

  /**
   * Sets the user.
   *
   * @param user the user
   */
  public void setUser(User user);

  /**
   * Returns the last tab accessed.
   *
   * @return the lastTab
   */
  public String getLastTab();

  /**
   * Sets the last tab accessed.
   *
   * @param lastTab the last tab accessed
   */
  public void setLastTab(String lastTab);
}
