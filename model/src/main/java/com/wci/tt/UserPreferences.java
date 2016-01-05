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

  /**
   * Returns the last refset accordion accessed.
   *
   * @return the lastRefsetAccordion
   */
  public String getLastRefsetAccordion();

  /**
   * Sets the last refset accordion accessed.
   *
   * @param lastRefsetAccordion the last refset accordion accessed
   */
  public void setLastRefsetAccordion(String lastRefsetAccordion);

  /**
   * Indicates whether or not spelling enabled is the case.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isSpellingEnabled();

  /**
   * Sets the spelling enabled.
   *
   * @param spellingEnabled the spelling enabled
   */
  public void setSpellingEnabled(boolean spellingEnabled);

  /**
   * Indicates whether or not memory enabled is the case.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isMemoryEnabled();

  /**
   * Sets the memory enabled.
   *
   * @param memoryEnabled the memory enabled
   */
  public void setMemoryEnabled(boolean memoryEnabled);

  /**
   * Returns the last translation accordion accessed.
   *
   * @return the lastTranslationAccordion
   */
  public String getLastTranslationAccordion();

  /**
   * Sets the last translation accordion accessed.
   *
   * @param lastTranslationAccordion the last translation accordion accessed
   */
  public void setLastTranslationAccordion(String lastTranslationAccordion);

  /**
   * Returns the last directory accordion accessed.
   *
   * @return the lastDirectoryAccordion
   */
  public String getLastDirectoryAccordion();

  /**
   * Sets the last directory accordion accessed.
   *
   * @param lastDirectoryAccordion the last directory accordion accessed
   */
  public void setLastDirectoryAccordion(String lastDirectoryAccordion);

  /**
   * Returns the last project role accessed.
   *
   * @return the lastProjectRole
   */
  public UserRole getLastProjectRole();

  /**
   * Sets the last project role accessed.
   *
   * @param lastProjectRole the last project role accessed
   */
  public void setLastProjectRole(UserRole lastProjectRole);

  /**
   * Returns the last project ID accessed.
   *
   * @return the lastProjectId
   */
  public Long getLastProjectId();

  /**
   * Sets the last project accessed.
   *
   * @param lastProjectId the last project id
   */
  public void setLastProjectId(Long lastProjectId);

}
