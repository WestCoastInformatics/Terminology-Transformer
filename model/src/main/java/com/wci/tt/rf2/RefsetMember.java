/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.rf2;


/**
 * Represents a refset member.
 */
public interface RefsetMember extends Component {

  /**
   * Returns the refset id.
   *
   * @return the refset id
   */
  public String getRefsetId();

  /**
   * Sets the refset id.
   *
   * @param refsetId the refset id
   */
  public void setRefsetId(String refsetId);

}