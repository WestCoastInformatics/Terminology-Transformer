/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.mojo;

import org.apache.maven.plugin.AbstractMojo;

/**
 * Superclass for mojos related to source data.
 */
public abstract class SourceDataMojo extends AbstractMojo {

  /**
   * Returns the name.
   *
   * @param terminology the terminology
   * @param version the version
   * @return the name
   */
  protected String getName(String terminology, String version) {
    return terminology + " " + version + " source data";
  }
}
