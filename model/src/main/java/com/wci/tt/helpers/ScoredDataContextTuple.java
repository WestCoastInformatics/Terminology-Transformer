/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.DataContext;

/**
 * Interface representing the data queried along with its associated DataContext
 * and probability.
 */
public interface ScoredDataContextTuple extends HasId, HasScore {

  /**
   * Returns the data.
   *
   * @return the data
   */
  public String getData();

  /**
   * Sets the data.
   *
   * @param data the data to set
   */
  public void setData(String data);

  /**
   * Returns the data context.
   *
   * @return the data context
   */
  public DataContext getDataContext();

  /**
   * Sets the data context.
   *
   * @param context the data context
   */
  public void setDataContext(DataContext context);
}
