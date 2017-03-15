/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.infomodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.wci.tt.infomodels.InfoModel;
import com.wci.umls.server.helpers.ConfigUtility;

/**
 * The Class MedicationOutputModel.
 */

@XmlRootElement(name = "outputModel")
public class MedicationOutputModel implements InfoModel<MedicationOutputModel> {

  /**  The model. */
  private MedicationModel model;

  /**  The input string. */
  private String inputString;

  /**  The normalized string. */
  private String normalizedString;

  /**  The remaining string. */
  private String remainingString;

  /**  The normalized remaining S tring. */
  private String normalizedRemainingString;

  /**  The removed terms. */
  private List<String> removedTerms;

  /**  The type. */
  private String type;

  /**
   * Returns the model.
   *
   * @return the model
   */
  public MedicationModel getModel() {
    return model;
  }

  /**
   * Instantiates an empty {@link MedicationOutputModel}.
   */
  public MedicationOutputModel() {

  }

  /**
   * Instantiates a {@link MedicationOutputModel} from the specified parameters.
   *
   * @param model the model
   */
  public MedicationOutputModel(MedicationOutputModel outputModel) {
    inputString = outputModel.getInputString();
    model = outputModel.getModel();
    normalizedRemainingString = outputModel.getNormalizedRemainingString();
    normalizedString = outputModel.getNormalizedString();
    remainingString = outputModel.getRemainingString();
    removedTerms = new ArrayList<>(outputModel.getRemovedTerms());
    type = outputModel.getType();
  }
  

  @Override
  public String toString() {
    return "MedicationOutputModel [model=" + model + ", inputString="
        + inputString + ", normalizedString=" + normalizedString
        + ", remainingString=" + remainingString
        + ", normalizedRemainingString=" + normalizedRemainingString
        + ", removedTerms=" + removedTerms + ", type=" + type + "]";
  }

  /**
   * Sets the model.
   *
   * @param model the model
   */
  public void setModel(MedicationModel model) {
    this.model = model;
  }

  /**
   * Returns the input string.
   *
   * @return the input string
   */
  public String getInputString() {
    return inputString;
  }

  /**
   * Sets the input string.
   *
   * @param inputString the input string
   */
  public void setInputString(String inputString) {
    this.inputString = inputString;
  }

  /**
   * Returns the normalized string.
   *
   * @return the normalized string
   */
  public String getNormalizedString() {
    return normalizedString;
  }

  /**
   * Sets the normalized string.
   *
   * @param normalizedString the normalized string
   */
  public void setNormalizedString(String normalizedString) {
    this.normalizedString = normalizedString;
  }

  /**
   * Returns the removed terms.
   *
   * @return the removed terms
   */
  public List<String> getRemovedTerms() {
    if (removedTerms == null) {
      removedTerms = new ArrayList<>();
    }
    return removedTerms;
  }

  /**
   * Sets the removed terms.
   *
   * @param removedTerms the removed terms
   */
  public void setRemovedTerms(List<String> removedTerms) {
    this.removedTerms = removedTerms;
  }

  /**
   * Returns the remaining string.
   *
   * @return the remaining string
   */
  public String getRemainingString() {
    return remainingString;
  }

  /**
   * Sets the remaining string.
   *
   * @param remainingString the remaining string
   */
  public void setRemainingString(String remainingString) {
    this.remainingString = remainingString;
  }

  /**
   * Returns the normalized remaining S tring.
   *
   * @return the normalized remaining S tring
   */
  public String getNormalizedRemainingString() {
    return normalizedRemainingString;
  }

  /**
   * Sets the normalized remaining S tring.
   *
   * @param normalizedRemainingString the normalized remaining S tring
   */
  public void setNormalizedRemainingString(String normalizedRemainingString) {
    this.normalizedRemainingString = normalizedRemainingString;
    getName();
  }

  /**
   * Returns the type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the type
   */
  public void setType(String type) {
    this.type = type;
  }

  /* see superclass */
  @Override
  public void checkProperties(Properties arg0) throws Exception {

  }

  /* see superclass */
  @Override
  @XmlTransient
  public String getName() {
    return "MLDP Medication Output ModeL";
  }

  /* see superclass */
  @Override
  public void setProperties(Properties arg0) throws Exception {

  }

  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    return true;
  }

  /* see superclass */
  @Override
  public MedicationOutputModel getModel(String model) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @XmlTransient
  @Override
  public String getModelValue() throws Exception {
    return ConfigUtility.getJsonForGraph(this);
  }

  /* see superclass */
  @Override
  public MedicationOutputModel getModelInCommon(MedicationOutputModel model,
    boolean analysisMode) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  @XmlTransient
  public String getVersion() {
    return "1.0";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((inputString == null) ? 0 : inputString.hashCode());
    result = prime * result + ((normalizedRemainingString == null) ? 0
        : normalizedRemainingString.hashCode());
    result = prime * result
        + ((normalizedString == null) ? 0 : normalizedString.hashCode());
    result = prime * result
        + ((remainingString == null) ? 0 : remainingString.hashCode());
    result =
        prime * result + ((removedTerms == null) ? 0 : removedTerms.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MedicationOutputModel other = (MedicationOutputModel) obj;
    if (inputString == null) {
      if (other.inputString != null)
        return false;
    } else if (!inputString.equals(other.inputString))
      return false;
    if (normalizedRemainingString == null) {
      if (other.normalizedRemainingString != null)
        return false;
    } else if (!normalizedRemainingString
        .equals(other.normalizedRemainingString))
      return false;
    if (normalizedString == null) {
      if (other.normalizedString != null)
        return false;
    } else if (!normalizedString.equals(other.normalizedString))
      return false;
    if (remainingString == null) {
      if (other.remainingString != null)
        return false;
    } else if (!remainingString.equals(other.remainingString))
      return false;
    if (removedTerms == null) {
      if (other.removedTerms != null)
        return false;
    } else if (!removedTerms.equals(other.removedTerms))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }
  
  


}
