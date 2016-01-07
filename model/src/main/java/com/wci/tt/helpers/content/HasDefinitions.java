/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import java.util.List;

import com.wci.tt.model.content.Definition;

/**
 * Represents a thing that has definitions.
 */
public interface HasDefinitions {

  /**
   * Returns the definitions.
   *
   * @return the definitions
   */
  public List<Definition> getDefinitions();

  /**
   * Sets the definitions.
   *
   * @param definitions the definitions
   */
  public void setDefinitions(List<Definition> definitions);

  /**
   * Adds the definition.
   *
   * @param definition the definition
   */
  public void addDefinition(Definition definition);

  /**
   * Removes the definition.
   *
   * @param definition the definition
   */
  public void removeDefinition(Definition definition);

}
