/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import java.util.Map;

/**
 * Represents a thing that has alternate ids.
 */
public interface HasAlternateTerminologyIds {
  /**
   * Returns the alternate terminology ids. The return value is a map of
   * "terminology" to "terminologyId" for terminologies other than the
   * components native terminology. For example, the UMLS AUI applied to an
   * atom.
   *
   * @return the alternate terminology ids
   */
  public Map<String, String> getAlternateTerminologyIds();

  /**
   * Sets the alternate terminology ids.
   *
   * @param alternateTerminologyIds the alternate terminology ids
   */
  public void setAlternateTerminologyIds(
    Map<String, String> alternateTerminologyIds);

  /**
   * Put alternate terminology id.
   *
   * @param terminology the terminology
   * @param terminologyId the terminology id
   */
  public void putAlternateTerminologyId(String terminology, String terminologyId);

  /**
   * Removes the alternate terminology id.
   *
   * @param terminology the terminology
   */
  public void removeAlternateTerminologyId(String terminology);
}
