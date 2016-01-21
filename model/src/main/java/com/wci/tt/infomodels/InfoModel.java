/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.infomodels;

import com.wci.umls.server.helpers.Configurable;

/**
 * Generically represents an information model.
 *
 * @param <T> the
 */
public interface InfoModel<T extends InfoModel<?>> extends Configurable {

  /**
   * Verifies that the model expressed in the parameter is an example of this
   * type of model.
   *
   * @param model the model
   * @return true, if successful
   * @throws Exception the exception
   */
  public boolean verify(String model) throws Exception;

  /**
   * Returns an object representing the model.
   *
   * @param model the model
   * @return the model
   * @throws Exception the exception
   */
  public T getModel(String model) throws Exception;
}
