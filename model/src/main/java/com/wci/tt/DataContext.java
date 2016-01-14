/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt;

import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.HasId;
import com.wci.tt.helpers.HasTerminology;

/**
 * Interface representing the context known about the data being processed.
 */
public interface DataContext extends HasTerminology, HasId {

  /**
   * Returns the data type.
   *
   * @return the data type
   */
  public DataContextType getDataType();

  /**
   * Sets the data type.
   *
   * @param dataType the data type
   */
  public void setDataType(DataContextType dataType);

  /**
   * Returns the customer.
   *
   * @return the customer
   */
  public String getCustomer();

  /**
   * Sets the customer.
   *
   * @param customer the customer
   */
  public void setCustomer(String customer);

  /**
   * Returns the semantic type.
   *
   * @return the semantic type
   */
  public String getSemanticType();

  /**
   * Sets the semantic type.
   *
   * @param semanticType the semantic type
   */
  public void setSemanticType(String semanticType);

  /**
   * Returns the specialty.
   *
   * @return the specialty
   */
  public String getSpecialty();

  /**
   * Sets the specialty.
   *
   * @param specialty the specialty
   */
  public void setSpecialty(String specialty);
}
