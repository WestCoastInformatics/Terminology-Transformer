/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

/**
 * The Enum DataTypes.
 */
public enum DataContextType {

  /** Unknown value. */
  UNKNOWN,

  /** The code. */
  CODE,
  
  /** The info model. */
  INFO_MODEL,
  
  /** The name. */
  NAME,
  
  /** The text. */
  TEXT,
  
  /**  The acronym. */
  // TODO: formalize this as a mechanism for normalizer feedback
  ACRONYM,
  
  /**  The pattern. */
  PATTERN;
}
