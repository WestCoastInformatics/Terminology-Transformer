/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import java.util.Properties;

/**
 * Represents something configurable.
 */
public interface Configurable {

  /**
   * Sets the properties.
   *
   * @param p the properties
   * @throws Exception
   */
  public void setProperties(Properties p) throws Exception;

  /**
   * Returns the name for a user to pick among a number of choices.
   *
   * @return the name
   */
  public String getName();
}
