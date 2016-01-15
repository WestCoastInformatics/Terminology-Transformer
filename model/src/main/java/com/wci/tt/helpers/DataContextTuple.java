/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.DataContext;

/**
 * Interface representing the data queried along with its associated
 * DataContext.
 */
public interface DataContextTuple extends HasId {

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
   * Returns the dataContext.
   *
   * @return the dataContext
   */
  public DataContext getDataContext();

  /**
   * Sets the dataContext.
   *
   * @param context the context to set
   */
  public void setDataContext(DataContext context);
}
