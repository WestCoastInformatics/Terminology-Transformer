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
 * Represents this JSON object
 * 
 * Features: BrandName DoseForm DoseFormQualifier Ingredient ReleasePeriod Route
 * Strength
 * 
 * 
 * <pre>
 * { 
 *   ingredients: [{ ingredient: "aspirin",
 *                strength : "81 mg"
 *   brandName: "aspirin lo-dose",
 *   doseForm: "tablet"
 *   doseFormQualifier: "chewable", 
 *   route: "oral",
 *   releasePeriod: "12hr"
 * }
 * </pre>
 * 
 * .
 */

// TODO Consider genericizing this
@XmlRootElement(name = "outputModel")
public class MedicationOutputModel implements InfoModel<MedicationOutputModel> {

  private MedicationModel model;

  private String inputString;

  private String normalizedString;

  private String remainingString;

  private List<String> removedTerms;

  private String type;

  public MedicationModel getModel() {
    return model;
  }

  public void setModel(MedicationModel model) {
    this.model = model;
  }

  public String getInputString() {
    return inputString;
  }

  public void setInputString(String inputString) {
    this.inputString = inputString;
  }

  public String getNormalizedString() {
    return normalizedString;
  }

  public void setNormalizedString(String normalizedString) {
    this.normalizedString = normalizedString;
  }

  public List<String> getRemovedTerms() {
    if (removedTerms == null) {
      removedTerms = new ArrayList<>();
    }
    return removedTerms;
  }

  public void setRemovedTerms(List<String> removedTerms) {
    this.removedTerms = removedTerms;
  }

  public String getRemainingString() {
    return remainingString;
  }

  public void setRemainingString(String remainingString) {
    this.remainingString = remainingString;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {

  }

  @Override
  public String getName() {
    return "MLDP Medication Output ModeL";
  }

  @Override
  public void setProperties(Properties arg0) throws Exception {

  }

  @Override
  public boolean verify(String model) throws Exception {
    return true;
  }

  @Override
  public MedicationOutputModel getModel(String model) throws Exception {
    throw new UnsupportedOperationException();
  }

  @XmlTransient
  @Override
  public String getModelValue() throws Exception {
    return ConfigUtility.getJsonForGraph(this);
  }

  @Override
  public MedicationOutputModel getModelInCommon(MedicationOutputModel model,
    boolean analysisMode) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

}
