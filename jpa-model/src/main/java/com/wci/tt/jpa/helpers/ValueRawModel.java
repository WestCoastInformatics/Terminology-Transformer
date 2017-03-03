/*
 *    Copyright 2016 Gliimpse, Inc.
 */
package com.wci.tt.jpa.helpers;

import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.wci.tt.infomodels.InfoModel;
import com.wci.umls.server.helpers.ConfigUtility;

/**
 * Represents a model for ingredient, strength, and units.
 * 
 * <pre>
 *     { value : "<value>",
 *       raw : "<raw value>" }
 * </pre>
 */
@XmlRootElement
public class ValueRawModel implements InfoModel<ValueRawModel> {

  /** The value. */
  private String value;

  /** The raw value. */
  private String raw;

  /**
   * Instantiates an empty {@link ValueRawModel}.
   */
  public ValueRawModel() {
  }

  /**
   * Instantiates a {@link ValueRawModel} from the specified parameters.
   *
   * @param value the value
   * @param raw the raw
   */
  public ValueRawModel(String value, String raw) {
    this.value = value;
    this.raw = raw;
  }

  /**
   * Instantiates a {@link ValueRawModel} from the specified parameters.
   *
   * @param model the model
   */
  public ValueRawModel(ValueRawModel model) {
    this.value = model.getValue();
    this.raw = model.getRaw();
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
   * Returns the raw.
   *
   * @return the raw
   */
  public String getRaw() {
    return raw;
  }

  /**
   * Sets the raw.
   *
   * @param raw the raw
   */
  public void setRaw(String raw) {
    this.raw = raw;
  }

  /**
   * Returns the name.
   *
   * @return the name
   */
  @XmlTransient
  @Override
  public String getName() {
    return "Ingredient Model";
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
  public ValueRawModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return (ValueRawModel) ConfigUtility.getGraphForJson(model,
          ValueRawModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model);
    }
  }

  /* see superclass */
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
    return "ValueRawModel [value=" + value + ", raw=" + raw + "]";
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
    result = prime * result + ((raw == null) ? 0 : raw.hashCode());
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
    ValueRawModel other = (ValueRawModel) obj;
    if (raw == null) {
      if (other.raw != null)
        return false;
    } else if (!raw.equals(other.raw))
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
  public ValueRawModel getModelInCommon(ValueRawModel model,
    boolean analysisMode) throws Exception {
    if (model == null) {
      return null;
    }
    ValueRawModel common = new ValueRawModel();
    boolean found = false;
    if (model.getValue() != null && model.getValue().equals(value)) {
      common.setValue(value);
      found = true;
    }
    if (model.getRaw() != null && model.getRaw().equals(raw)) {
      common.setRaw(raw);
      found = true;
    }
    if (found) {
      if (analysisMode && common.getValue() == null) {
        common.setValue(InfoModel.MULTIPLE_VALUES);
      }
      if (analysisMode && common.getRaw() == null) {
        common.setRaw(InfoModel.MULTIPLE_VALUES);
      }
      return common;
    } else {
      return null;
    }
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {
    // TODO Auto-generated method stub
    
  }
}
