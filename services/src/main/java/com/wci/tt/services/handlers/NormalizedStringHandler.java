/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import com.wci.tt.helpers.Configurable;

/**
 * Generically represents a handler that can lexically normalize a string.
 */
public interface NormalizedStringHandler extends Configurable {

  /**
   * Returns the normalized string.
   *
   * @param string the string
   * @return the normalized string
   * @throws Exception
   */
  public String getNormalizedString(String string) throws Exception;

}
