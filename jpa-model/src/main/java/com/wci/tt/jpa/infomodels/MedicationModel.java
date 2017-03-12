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
import com.wci.tt.jpa.helpers.ValueRawModel;
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
@XmlRootElement(name = "medication")
public class MedicationModel implements InfoModel<MedicationModel> {

  /** The ingredients. */
  private List<IngredientModel> ingredients;

  /** The brand name. */
  private ValueRawModel brandName;

  /** The dose form. */
  private ValueRawModel doseForm;

  /** The form qualifier. */
  private ValueRawModel doseFormQualifier;

  /** The route. */
  private ValueRawModel route;

  /** The release period.. */
  private ValueRawModel releasePeriod;

  /**
   * Instantiates an empty {@link MedicationModel}.
   */
  public MedicationModel() {
  }

  /**
   * Instantiates a {@link MedicationModel} from the specified parameters.
   *
   * @param model the model
   */
  public MedicationModel(MedicationModel model) {
    this.ingredients = model.getIngredients();
    this.brandName = model.getBrandName();
    this.doseForm = model.getDoseForm();
    this.doseFormQualifier = model.getDoseFormQualifier();
    this.route = model.getRoute();
    this.releasePeriod = model.getReleasePeriod();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.Configurable#getName()
   */
  /* see superclass */
  @XmlTransient
  @Override
  public String getName() {
    return "MLDP Medication Model";
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.Configurable#setProperties(java.util.
   * Properties)
   */
  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, MedicationModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  @Override
  public MedicationModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return (MedicationModel) ConfigUtility.getGraphForJson(model,
          MedicationModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model);
    }
  }

  /* see superclass */
  @XmlTransient
  @Override
  public String getModelValue() throws Exception {
    return ConfigUtility.getJsonForGraph(this);
  }

  /**
   * Returns the ingredients.
   *
   * @return the ingredients
   */
  public List<IngredientModel> getIngredients() {
    if (ingredients == null) {
      ingredients = new ArrayList<>();
    }
    return ingredients;
  }

  /**
   * Sets the ingredients.
   *
   * @param ingredients the ingredients
   */
  public void setIngredients(List<IngredientModel> ingredients) {
    this.ingredients = ingredients;
  }

  /**
   * Returns the brand name.
   *
   * @return the brand name
   */
  public ValueRawModel getBrandName() {
    return brandName;
  }

  /**
   * Sets the brand name.
   *
   * @param brandName the brand name
   */
  public void setBrandName(ValueRawModel brandName) {
    this.brandName = brandName;
  }

  /**
   * Returns the dose form.
   *
   * @return the dose form
   */
  public ValueRawModel getDoseForm() {
    return doseForm;
  }

  /**
   * Sets the dose form.
   *
   * @param doseForm the dose form
   */
  public void setDoseForm(ValueRawModel doseForm) {
    this.doseForm = doseForm;
  }

  /**
   * Returns the route.
   *
   * @return the route
   */
  public ValueRawModel getRoute() {
    return route;
  }

  /**
   * Sets the route.
   *
   * @param route the route
   */
  public void setRoute(ValueRawModel route) {
    this.route = route;
  }

  /**
   * Returns the dose form qualifier.
   *
   * @return the dose form qualifier
   */
  public ValueRawModel getDoseFormQualifier() {
    return doseFormQualifier;
  }

  /**
   * Sets the dose form qualifier.
   *
   * @param doseFormQualifier the dose form qualifier
   */
  public void setDoseFormQualifier(ValueRawModel doseFormQualifier) {
    this.doseFormQualifier = doseFormQualifier;
  }

  /**
   * Returns the release period.
   *
   * @return the release period
   */
  public ValueRawModel getReleasePeriod() {
    return releasePeriod;
  }

  /**
   * Sets the release period.
   *
   * @param releasePeriod the release period
   */
  public void setReleasePeriod(ValueRawModel releasePeriod) {
    this.releasePeriod = releasePeriod;
  }

  /* see superclass */
  @XmlTransient
  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {
    // do nothing
  }

  @Override
  public MedicationModel getModelInCommon(MedicationModel model,
    boolean analysisMode) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((brandName == null) ? 0 : brandName.hashCode());
    result = prime * result + ((doseForm == null) ? 0 : doseForm.hashCode());
    result = prime * result
        + ((doseFormQualifier == null) ? 0 : doseFormQualifier.hashCode());
    result =
        prime * result + ((ingredients == null) ? 0 : ingredients.hashCode());
    result = prime * result
        + ((releasePeriod == null) ? 0 : releasePeriod.hashCode());
    result = prime * result + ((route == null) ? 0 : route.hashCode());
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
    MedicationModel other = (MedicationModel) obj;
    if (brandName == null) {
      if (other.brandName != null)
        return false;
    } else if (!brandName.equals(other.brandName))
      return false;
    if (doseForm == null) {
      if (other.doseForm != null)
        return false;
    } else if (!doseForm.equals(other.doseForm))
      return false;
    if (doseFormQualifier == null) {
      if (other.doseFormQualifier != null)
        return false;
    } else if (!doseFormQualifier.equals(other.doseFormQualifier))
      return false;
    if (ingredients == null) {
      if (other.ingredients != null)
        return false;
    } else if (!ingredients.equals(other.ingredients))
      return false;
    if (releasePeriod == null) {
      if (other.releasePeriod != null)
        return false;
    } else if (!releasePeriod.equals(other.releasePeriod))
      return false;
    if (route == null) {
      if (other.route != null)
        return false;
    } else if (!route.equals(other.route))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "MedicationModel [ingredients=" + ingredients + ", brandName="
        + brandName + ", doseForm=" + doseForm + ", doseFormQualifier="
        + doseFormQualifier + ", route=" + route + ", releasePeriod="
        + releasePeriod + "]";
  }

}