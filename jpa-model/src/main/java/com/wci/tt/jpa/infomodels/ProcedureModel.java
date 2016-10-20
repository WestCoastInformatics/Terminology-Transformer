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
 * Information model for representing an MLDP Procedure.
 * 
 * <pre>
 *    { condition : "Fracture",
 *      subcondition : "Open",
 *      site : {bodySite : "arm", laterality: "left"},
 *      cause : "virus",
 *      occurrence : "childhood",
 *      course : "recurrent",
 *      category : "type 2",
 *      severity : "mild"
 *    }
 * </pre>
 */
@XmlRootElement(name = "condition")
public class ProcedureModel implements InfoModel<ProcedureModel> {

  /** The procedure. */
  private String procedure;

  /** The condition. */
  private String condition;

  /** The site. */
  private SiteModel site;

  /** The related site. */
  private SiteModel relatedSite;

  /** The approach. */
  private String approach;

  /** The device. */
  private String device;

  /** The substance. */
  private String substance;

  /** The qual. */
  private String qual;

  /**
   * Instantiates an empty {@link ProcedureModel}.
   */
  public ProcedureModel() {
    // n/a
  }

  /**
   * Instantiates a {@link ProcedureModel} from the specified parameters.
   *
   * @param model the model
   */
  public ProcedureModel(ProcedureModel model) {
    procedure = model.getProcedure();
    condition = model.getCondition();
    site = model.getSite();
    relatedSite = model.getRelatedSite();
    approach = model.getApproach();
    device = model.getDevice();
    substance = model.getSubstance();
    qual = model.getQual();
  }

  /* see superclass */
  @XmlTransient
  @Override
  public String getName() {
    return "Procedure Model";
  }

  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, ProcedureModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  @Override
  public ProcedureModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, ProcedureModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model, e);
    }
  }

  /* see superclass */
  @XmlTransient
  @Override
  public String getVersion() {
    return "1.0";
  }

  /* see superclass */
  @XmlTransient
  @Override
  public String getModelValue() throws Exception {
    return ConfigUtility.getJsonForGraph(this);
  }

  /* see superclass */
  @Override
  public ProcedureModel getModelInCommon(ProcedureModel model,
    boolean analysisMode) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public void setProperties(Properties arg0) throws Exception {
    // n/a
  }

  /**
   * Returns the procedure.
   *
   * @return the procedure
   */
  public String getProcedure() {
    return procedure;
  }

  /**
   * Sets the procedure.
   *
   * @param procedure the procedure
   */
  public void setProcedure(String procedure) {
    this.procedure = procedure;
  }

  /**
   * Returns the condition.
   *
   * @return the condition
   */
  public String getCondition() {
    return condition;
  }

  /**
   * Sets the condition.
   *
   * @param condition the condition
   */
  public void setCondition(String condition) {
    this.condition = condition;
  }

  /**
   * Returns the site.
   *
   * @return the site
   */
  public SiteModel getSite() {
    return site;
  }

  /**
   * Sets the site.
   *
   * @param site the site
   */
  public void setSite(SiteModel site) {
    this.site = site;
  }

  /**
   * Returns the related site.
   *
   * @return the related site
   */
  public SiteModel getRelatedSite() {
    return relatedSite;
  }

  /**
   * Sets the related site.
   *
   * @param relatedSite the related site
   */
  public void setRelatedSite(SiteModel relatedSite) {
    this.relatedSite = relatedSite;
  }

  /**
   * Returns the approach.
   *
   * @return the approach
   */
  public String getApproach() {
    return approach;
  }

  /**
   * Sets the approach.
   *
   * @param approach the approach
   */
  public void setApproach(String approach) {
    this.approach = approach;
  }

  /**
   * Returns the device.
   *
   * @return the device
   */
  public String getDevice() {
    return device;
  }

  /**
   * Sets the device.
   *
   * @param device the device
   */
  public void setDevice(String device) {
    this.device = device;
  }

  /**
   * Returns the substance.
   *
   * @return the substance
   */
  public String getSubstance() {
    return substance;
  }

  /**
   * Sets the substance.
   *
   * @param substance the substance
   */
  public void setSubstance(String substance) {
    this.substance = substance;
  }

  /**
   * Returns the qual.
   *
   * @return the qual
   */
  public String getQual() {
    return qual;
  }

  /**
   * Sets the qual.
   *
   * @param qual the qual
   */
  public void setQual(String qual) {
    this.qual = qual;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((approach == null) ? 0 : approach.hashCode());
    result = prime * result + ((condition == null) ? 0 : condition.hashCode());
    result = prime * result + ((device == null) ? 0 : device.hashCode());
    result = prime * result + ((procedure == null) ? 0 : procedure.hashCode());
    result = prime * result + ((qual == null) ? 0 : qual.hashCode());
    result =
        prime * result + ((relatedSite == null) ? 0 : relatedSite.hashCode());
    result = prime * result + ((site == null) ? 0 : site.hashCode());
    result = prime * result + ((substance == null) ? 0 : substance.hashCode());
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
    ProcedureModel other = (ProcedureModel) obj;
    if (approach == null) {
      if (other.approach != null)
        return false;
    } else if (!approach.equals(other.approach))
      return false;
    if (condition == null) {
      if (other.condition != null)
        return false;
    } else if (!condition.equals(other.condition))
      return false;
    if (device == null) {
      if (other.device != null)
        return false;
    } else if (!device.equals(other.device))
      return false;
    if (procedure == null) {
      if (other.procedure != null)
        return false;
    } else if (!procedure.equals(other.procedure))
      return false;
    if (qual == null) {
      if (other.qual != null)
        return false;
    } else if (!qual.equals(other.qual))
      return false;
    if (relatedSite == null) {
      if (other.relatedSite != null)
        return false;
    } else if (!relatedSite.equals(other.relatedSite))
      return false;
    if (site == null) {
      if (other.site != null)
        return false;
    } else if (!site.equals(other.site))
      return false;
    if (substance == null) {
      if (other.substance != null)
        return false;
    } else if (!substance.equals(other.substance))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ProcedureModel [procedure=" + procedure + ", condition=" + condition
        + ", site=" + site + ", relatedSite=" + relatedSite + ", approach="
        + approach + ", device=" + device + ", substance=" + substance
        + ", qual=" + qual + "]";
  }

}
