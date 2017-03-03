/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.infomodels;

import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.wci.tt.infomodels.InfoModel;
import com.wci.umls.server.helpers.ConfigUtility;

/**
 * Represents a model for ingredient, strength, and units.
 * 
 * <pre>
 *     { ingredientName : "<Ingredient Name>" : 
 *       value : "<Ingredient strength>", 
 *       unit: "<Strength unit>" }
 * </pre>
 */
@XmlRootElement
public class IngredientModel
    implements InfoModel<IngredientModel>, Comparable<IngredientModel> {

  /** The ingredient name. */
  private String ingredient;

  /** The value. */
  private String strength;

  /**
   * Instantiates an empty {@link IngredientModel}.
   */
  public IngredientModel() {
    // n/a
  }

  /**
   * Instantiates a {@link IngredientModel} from the specified parameters.
   *
   * @param ingredientName the ingredient name
   * @param strength the strength
   */
  public IngredientModel(String ingredientName, String strength) {
    this.ingredient = ingredientName;
    this.strength = strength;
  }

  /**
   * Instantiates a {@link IngredientModel} from the specified parameters.
   *
   * @param ingredientModel the ingredient model
   */
  public IngredientModel(IngredientModel ingredientModel) {
    this.ingredient = ingredientModel.getIngredient();
    this.strength = ingredientModel.getStrength();
  }

  /**
   * Returns the ingredient.
   *
   * @return the ingredient
   */
  public String getIngredient() {
    return ingredient;
  }

  /**
   * Sets the ingredient.
   *
   * @param ingredient the ingredient
   */
  public void setIngredient(String ingredient) {
    this.ingredient = ingredient;
  }

  /**
   * Returns the strength.
   *
   * @return the strength
   */
  public String getStrength() {
    return strength;
  }

  /**
   * Sets the strength.
   *
   * @param strength the strength
   */
  public void setStrength(String strength) {
    this.strength = strength;
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {

  }

  @Override
  public String getName() {
    return "MLDP Ingredient Model";
  }

  @Override
  public void setProperties(Properties arg0) throws Exception {

  }

  @Override
  public int compareTo(IngredientModel o) {
    if (o.getIngredient().compareTo(ingredient) != 0) {
      return o.getIngredient().compareTo(ingredient);
    }
    return o.getStrength().compareTo(strength);
  }

  @Override
  public boolean verify(String model) throws Exception {
    return true;
  }

  @Override
  public IngredientModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return (IngredientModel) ConfigUtility.getGraphForJson(model,
          IngredientModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model);
    }
  }

  @Override
  public String getModelValue() throws Exception {
    return ConfigUtility.getJsonForGraph(this);
  }

  @Override
  public IngredientModel getModelInCommon(IngredientModel model,
    boolean analysisMode) throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getVersion() {
   return "1.0";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((ingredient == null) ? 0 : ingredient.hashCode());
    result = prime * result + ((strength == null) ? 0 : strength.hashCode());
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
    IngredientModel other = (IngredientModel) obj;
    if (ingredient == null) {
      if (other.ingredient != null)
        return false;
    } else if (!ingredient.equals(other.ingredient))
      return false;
    if (strength == null) {
      if (other.strength != null)
        return false;
    } else if (!strength.equals(other.strength))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "IngredientModel [ingredient=" + ingredient + ", strength="
        + strength + "]";
  }
  
  

}
