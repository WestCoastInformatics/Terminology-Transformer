/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.filters;

import com.wci.tt.infomodels.InfoModel;

/**
 * The Interface PreProcessingFilter.
 */
public interface PreProcessingFilter {

  /**
   * Goes through all pre-processing filters to determine if term should be
   * processed.
   *
   * @param term the term
   * @return true, if term should be processed
   * @throws Exception the exception
   */
  public boolean isTermSupported(InfoModel<?> infoModel) throws Exception;
}
