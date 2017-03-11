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

// TODO Consider genericizing this
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
  public MedicationOutputModel(MedicationOutputModel model) {
    this.inputString = model.getInputString();
    this.model = model.getModel();
    this.normalizedRemainingString = model.getNormalizedRemainingString();
    this.normalizedString = model.getNormalizedString();
    this.remainingString = model.getRemainingString();
    this.removedTerms = model.getRemovedTerms();
    this.type = model.getType();
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
    this.getName();
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
  public String getVersion() {
    return "1.0";
  }


}
