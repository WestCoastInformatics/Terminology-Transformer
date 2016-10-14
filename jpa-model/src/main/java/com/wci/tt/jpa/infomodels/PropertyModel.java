/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.infomodels;

import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.wci.tt.infomodels.InfoModel;
import com.wci.umls.server.helpers.ConfigUtility;

/**
 * Represents a model for a property.
 * 
 * <pre>
 *     { prop : "prop",
 *       value : "value" }
 * </pre>
 */
@XmlRootElement(name = "property")
public class PropertyModel implements InfoModel<PropertyModel> {

  /** The prop. */
  private String prop;

  /** The value. */
  private String value;

  /**
   * Instantiates an empty {@link PropertyModel}.
   */
  public PropertyModel() {
  }

  /**
   * Instantiates a {@link PropertyModel} from the specified parameters.
   *
   * @param prop the prop
   * @param value the value
   */
  public PropertyModel(String prop, String value) {
    this.prop = prop;
    this.value = value;
  }

  /**
   * Instantiates a {@link PropertyModel} from the specified parameters.
   *
   * @param model the model
   */
  public PropertyModel(PropertyModel model) {
    this.prop = model.getProp();
    this.value = model.getValue();
  }

  /**
   * Returns the prop.
   *
   * @return the prop
   */
  public String getProp() {
    return prop;
  }

  /**
   * Sets the prop.
   *
   * @param prop the prop
   */
  public void setProp(String prop) {
    this.prop = prop;
  }

  /**
   * Returns the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the value
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Returns the name.
   *
   * @return the name
   */
  @XmlTransient
  @Override
  public String getName() {
    return "Property Model";
  }

  /**
   * Sets the properties.
   *
   * @param p the properties
   * @throws Exception the exception
   */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /**
   * Verify.
   *
   * @param model the model
   * @return true, if successful
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, this.getClass());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the model.
   *
   * @param model the model
   * @return the model
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public PropertyModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, PropertyModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model);
    }
  }

  /* see superclass */
  @Override
  @XmlTransient
  public String getModelValue() throws Exception {
    return ConfigUtility.getJsonForGraph(this);
  }

  /**
   * Returns the version.
   *
   * @return the version
   */
  @XmlTransient
  @Override
  public String getVersion() {
    return "1.0";
  }

  /**
   * To string.
   *
   * @return the string
   */
  @Override
  public String toString() {
    return "PropertyModel [prop=" + prop + ", value=" + value + "]";
  }

  /**
   * Hash code.
   *
   * @return the int
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((prop == null) ? 0 : prop.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  /**
   * Equals.
   *
   * @param obj the obj
   * @return true, if successful
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PropertyModel other = (PropertyModel) obj;
    if (prop == null) {
      if (other.prop != null)
        return false;
    } else if (!prop.equals(other.prop))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public PropertyModel getModelInCommon(PropertyModel model,
    boolean analysisMode) throws Exception {
    if (model == null) {
      return null;
    }
    PropertyModel common = new PropertyModel();
    boolean found = false;
    if (model.getProp() != null && model.getProp().equals(prop)) {
      common.setProp(prop);
      found = true;
    }
    if (model.getValue() != null && model.getValue().equals(value)) {
      common.setValue(value);
      found = true;
    }
    if (found) {
      if (analysisMode && common.getValue() == null) {
        common.setValue(InfoModel.MULTIPLE_VALUES);
      }
      if (analysisMode && common.getProp() == null) {
        common.setProp(InfoModel.MULTIPLE_VALUES);
      }
      return common;
    } else {
      return null;
    }
  }
}
