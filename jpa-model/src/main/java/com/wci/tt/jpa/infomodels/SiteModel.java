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
 * Information model for a site in the body.
 * 
 * <pre>
 *    { bodySite : "arm", laterality: "left", position : "upper" }
 * </pre>
 */
@XmlRootElement(name = "site")
public class SiteModel implements InfoModel<SiteModel> {

  /** The body site. */
  private String bodySite;

  /** The laterality. */
  private String laterality;

  /** The position. */
  private String position;

  /**
   * Instantiates an empty {@link SiteModel}.
   */
  public SiteModel() {
    // n/a
  }

  /**
   * Instantiates a {@link SiteModel} from the specified parameters.
   *
   * @param model the model
   */
  public SiteModel(SiteModel model) {
    bodySite = model.getBodySite();
    laterality = model.getLaterality();
    position = model.getPosition();
  }

  /* see superclass */
  @XmlTransient
  @Override
  public String getName() {
    return "Site Model";
  }

  /**
   * Sets the properties.
   *
   * @param p the properties
   * @throws Exception the exception
   */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a - no configuration
  }

  /**
   * Returns the body site.
   *
   * @return the body site
   */
  public String getBodySite() {
    return bodySite;
  }

  /**
   * Sets the body site.
   *
   * @param bodySite the body site
   */
  public void setBodySite(String bodySite) {
    this.bodySite = bodySite;
  }

  /**
   * Returns the laterality.
   *
   * @return the laterality
   */
  public String getLaterality() {
    return laterality;
  }

  /**
   * Sets the laterality.
   *
   * @param laterality the laterality
   */
  public void setLaterality(String laterality) {
    this.laterality = laterality;
  }

  /**
   * Returns the position.
   *
   * @return the position
   */
  public String getPosition() {
    return position;
  }

  /**
   * Sets the position.
   *
   * @param position the position
   */
  public void setPosition(String position) {
    this.position = position;
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
      ConfigUtility.getGraphForJson(model, SiteModel.class);
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
  public SiteModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, SiteModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model, e);
    }
  }

  /**
   * Returns the version.
   *
   * @return the version
   */
  /* see superclass */
  @XmlTransient
  @Override
  public String getVersion() {
    return "1.0";
  }

  /**
   * Returns the model value.
   *
   * @return the model value
   * @throws Exception the exception
   */
  /* see superclass */
  @XmlTransient
  @Override
  public String getModelValue() throws Exception {
    return ConfigUtility.getJsonForGraph(this);
  }

  /**
   * Returns the model in common.
   *
   * @param model the model
   * @param analysisMode the analysis mode
   * @return the model in common
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public SiteModel getModelInCommon(SiteModel model, boolean analysisMode)
    throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bodySite == null) ? 0 : bodySite.hashCode());
    result =
        prime * result + ((laterality == null) ? 0 : laterality.hashCode());
    result = prime * result + ((position == null) ? 0 : position.hashCode());
    return result;
  }

  /* see superclass */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SiteModel other = (SiteModel) obj;
    if (bodySite == null) {
      if (other.bodySite != null)
        return false;
    } else if (!bodySite.equals(other.bodySite))
      return false;
    if (laterality == null) {
      if (other.laterality != null)
        return false;
    } else if (!laterality.equals(other.laterality))
      return false;
    if (position == null) {
      if (other.position != null)
        return false;
    } else if (!position.equals(other.position))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "SiteModel [bodySite=" + bodySite + ", laterality=" + laterality
        + ", position=" + position + "]";
  }

}
