package com.wci.tt.jpa;

import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;

// TODO: Auto-generated Javadoc
/**
 * The Class TransformInputJpa.
 */
@XmlRootElement
public class DataContextJpa implements DataContext {

  /** The terminology. */
  private String terminology;
  
  /** The version. */
  private String version;
  
  private DataContextType dataType;
  
  private String customer;
  
  private String semanticType;  
  
  private String specialty;
  
  /**
   * Instantiates a new transform input jpa.
   */
  public DataContextJpa() {
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
  @Override
  public DataContextType getDataType() {
    return dataType;
  }
  @Override
  public void setDataType(DataContextType dataType) {
    this.dataType = dataType;
  }
  @Override
  public String getCustomer() {
    return customer;
  }
  @Override
  public void setCustomer(String customer) {
    this.customer = customer;
  }
  @Override
  public String getSemanticType() {
    return semanticType;
  }
  @Override
  public void setSemanticType(String semanticType) {
    this.semanticType = semanticType;
  }
  @Override
  public String getSpecialty() {
    return specialty;
  }

  @Override
  public void setSpecialty(String specialty) {
    this.specialty = specialty;
  }
  
  

}
