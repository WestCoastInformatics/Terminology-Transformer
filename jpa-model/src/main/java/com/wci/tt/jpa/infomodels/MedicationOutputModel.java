/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.infomodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
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
@XmlRootElement(name = "medication")
public class MedicationOutputModel implements InfoModel<MedicationOutputModel> {

  /** The Constant FLOOR_MODEL_SCORE. */
  @SuppressWarnings("unused")
  private static final float FLOOR_MODEL_SCORE = 0.11f;

  /** The Constant MINIMUM_COMMON_SCORE. */
  private static final float MINIMUM_COMMON_SCORE = .015f;

  /** The ingredients. */
  private List<IngredientModel> ingredients;

  /** The brand name. */
  private String brandName;

  /** The dose form. */
  private String doseForm;

  /** The form qualifier. */
  private String doseFormQualifier;

  /** The route. */
  private String route;

  /** The release period.. */
  private String releasePeriod;

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

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.tt.infomodels.InfoModel#verify(java.lang.String)
   */
  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, MedicationOutputModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.tt.infomodels.InfoModel#getModel(java.lang.String)
   */
  /* see superclass */
  public MedicationOutputModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return (MedicationOutputModel) ConfigUtility.getGraphForJson(model,
          MedicationOutputModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.tt.infomodels.InfoModel#getModelValue()
   */
  /* see superclass */
  @XmlTransient
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
  public String getBrandName() {
    return brandName;
  }

  /**
   * Sets the brand name.
   *
   * @param brandName the brand name
   */
  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  /**
   * Returns the dose form.
   *
   * @return the dose form
   */
  public String getDoseForm() {
    return doseForm;
  }

  /**
   * Sets the dose form.
   *
   * @param doseForm the dose form
   */
  public void setDoseForm(String doseForm) {
    this.doseForm = doseForm;
  }

  /**
   * Returns the route.
   *
   * @return the route
   */
  public String getRoute() {
    return route;
  }

  /**
   * Sets the route.
   *
   * @param route the route
   */
  public void setRoute(String route) {
    this.route = route;
  }

  /**
   * Returns the dose form qualifier.
   *
   * @return the dose form qualifier
   */
  public String getDoseFormQualifier() {
    return doseFormQualifier;
  }

  /**
   * Sets the dose form qualifier.
   *
   * @param doseFormQualifier the dose form qualifier
   */
  public void setDoseFormQualifier(String doseFormQualifier) {
    this.doseFormQualifier = doseFormQualifier;
  }

  /**
   * Returns the release period.
   *
   * @return the release period
   */
  public String getReleasePeriod() {
    return releasePeriod;
  }

  /**
   * Sets the release period.
   *
   * @param releasePeriod the release period
   */
  public void setReleasePeriod(String releasePeriod) {
    this.releasePeriod = releasePeriod;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.tt.infomodels.InfoModel#getVersion()
   */
  /* see superclass */
  @XmlTransient
  public String getVersion() {
    return "1.0";
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {
    // do nothing
  }

  @Override
  public MedicationOutputModel getModelInCommon(MedicationOutputModel model,
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
    MedicationOutputModel other = (MedicationOutputModel) obj;
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
