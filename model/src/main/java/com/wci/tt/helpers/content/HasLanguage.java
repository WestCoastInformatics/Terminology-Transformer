/**
 * Copyright 2015 West Coast Informatics, LLC
 */
/*************************************************************
 * HasLanguage: HasLanguage.java
 * Last Updated: Feb 27, 2009
 *************************************************************/
package com.wci.tt.helpers.content;

import com.wci.tt.model.meta.Language;

/**
 * Represents a thing that is associated with a {@link Language}
 */
public interface HasLanguage {

  /**
   * Returns the language.
   * 
   * @return the language
   */
  public Language getLanguage();

  /**
   * Sets the language
   * 
   * @param language the language
   */
  public void setLanguage(Language language);

}
