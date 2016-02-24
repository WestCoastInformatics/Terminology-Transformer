/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.filters;

import com.wci.tt.infomodels.InfoModel;

/**
 * Generically represents a pre-processing filter for an information model.
 */
public interface PreProcessingFilter {

  /**
   * Indicates whether to keep or skip the specified model.
   *
   * @param infoModel the info model
   * @return true, if term should be processed
   * @throws Exception the exception
   */
  public boolean accept(InfoModel<?> infoModel) throws Exception;
}
