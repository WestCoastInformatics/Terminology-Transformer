/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt;

import java.util.List;

import com.wci.tt.helpers.HasId;

/**
 * Represents a memory of reusable translations for certain phrases. This
 * assumes it is always from the language of the international edition to a
 * single other language.
 */
public interface PhraseMemory extends HasId {

  /**
   * Returns the entries.
   *
   * @return the entries
   */
  public List<MemoryEntry> getEntries();

  /**
   * Sets the entries.
   *
   * @param entries the entries
   */
  public void setEntries(List<MemoryEntry> entries);

  /**
   * Returns the translation.
   *
   * @return the translation
   */
  public Translation getTranslation();

  /**
   * Sets the translation.
   *
   * @param translation the translation
   */
  public void setTranslation(Translation translation);
}
