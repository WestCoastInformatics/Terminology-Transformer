package com.wci.tt.jpa;

import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.TransformInput;
import com.wci.tt.helpers.DataType;

// TODO: Auto-generated Javadoc
/**
 * The Class TransformInputJpa.
 */
@XmlRootElement
public class TransformInputJpa implements TransformInput {

  /** The terminology. */
  private String terminology;
  
  /** The version. */
  private String version;
  
  private DataType type;
  
  /**
   * Instantiates a new transform input jpa.
   */
  public TransformInputJpa() {
    // do nothing
  }
  
  /**
   * Gets the terminology.
   *
   * @return the terminology
   */
  @Override
  public String getTerminology() {
    return this.terminology;
  }

  /**
   * Sets the terminology.
   *
   * @param terminology the new terminology
   */
  @Override
  public void setTerminology(String terminology) {
    this.terminology = terminology;
    
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  @Override
  public String getVersion() {
   return this.version;
  }

  /**
   * Sets the version.
   *
   * @param version the new version
   */
  @Override
  public void setVersion(String version) {
    this.version = version;
  }

}
