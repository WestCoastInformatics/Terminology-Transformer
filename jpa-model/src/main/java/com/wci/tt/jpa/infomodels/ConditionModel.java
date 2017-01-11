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
 * Information model for representing an MLDP Condition.
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
public class ConditionModel implements InfoModel<ConditionModel> {

  /** The condition. */
  private String condition;

  /** The subconditoin. */
  private String subcondition;

  /** The site. */
  private SiteModel site;

  /** The cause. */
  private String cause;

  /** The occurrence. */
  private String occurrence;

  /** The course. */
  private String course;

  /** The category. */
  private String category;

  /** The severity. */
  private String severity;

  /**
   * Instantiates an empty {@link ConditionModel}.
   */
  public ConditionModel() {
    // n/a
  }

  /**
   * Instantiates a {@link ConditionModel} from the specified parameters.
   *
   * @param model the model
   */
  public ConditionModel(ConditionModel model) {
    condition = model.getCondition();
    subcondition = model.getSubcondition();
    site = model.getSite();
    cause = model.getCause();
    occurrence = model.getOccurrence();
    course = model.getCourse();
    category = model.getCategory();
    severity = model.getSeverity();

  }

  /* see superclass */
  @XmlTransient
  @Override
  public String getName() {
    return "Condition Model";
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
   * Returns the subcondition.
   *
   * @return the subcondition
   */
  public String getSubcondition() {
    return subcondition;
  }

  /**
   * Sets the subcondition.
   *
   * @param subcondition the subcondition
   */
  public void setSubcondition(String subcondition) {
    this.subcondition = subcondition;
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
   * Returns the cause.
   *
   * @return the cause
   */
  public String getCause() {
    return cause;
  }

  /**
   * Sets the cause.
   *
   * @param cause the cause
   */
  public void setCause(String cause) {
    this.cause = cause;
  }

  /**
   * Returns the occurrence.
   *
   * @return the occurrence
   */
  public String getOccurrence() {
    return occurrence;
  }

  /**
   * Sets the occurrence.
   *
   * @param occurrence the occurrence
   */
  public void setOccurrence(String occurrence) {
    this.occurrence = occurrence;
  }

  /**
   * Returns the course.
   *
   * @return the course
   */
  public String getCourse() {
    return course;
  }

  /**
   * Sets the course.
   *
   * @param course the course
   */
  public void setCourse(String course) {
    this.course = course;
  }

  /**
   * Returns the category.
   *
   * @return the category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Sets the category.
   *
   * @param category the category
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Returns the severity.
   *
   * @return the severity
   */
  public String getSeverity() {
    return severity;
  }

  /**
   * Sets the severity.
   *
   * @param severity the severity
   */
  public void setSeverity(String severity) {
    this.severity = severity;
  }

  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, ConditionModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  @Override
  public ConditionModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, ConditionModel.class);
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
  public ConditionModel getModelInCommon(ConditionModel model,
    boolean analysisMode) throws Exception {
    throw new UnsupportedOperationException();
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((category == null) ? 0 : category.hashCode());
    result = prime * result + ((cause == null) ? 0 : cause.hashCode());
    result = prime * result + ((condition == null) ? 0 : condition.hashCode());
    result = prime * result + ((course == null) ? 0 : course.hashCode());
    result =
        prime * result + ((occurrence == null) ? 0 : occurrence.hashCode());
    result = prime * result + ((severity == null) ? 0 : severity.hashCode());
    result = prime * result + ((site == null) ? 0 : site.hashCode());
    result =
        prime * result + ((subcondition == null) ? 0 : subcondition.hashCode());
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
    ConditionModel other = (ConditionModel) obj;
    if (category == null) {
      if (other.category != null)
        return false;
    } else if (!category.equals(other.category))
      return false;
    if (cause == null) {
      if (other.cause != null)
        return false;
    } else if (!cause.equals(other.cause))
      return false;
    if (condition == null) {
      if (other.condition != null)
        return false;
    } else if (!condition.equals(other.condition))
      return false;
    if (course == null) {
      if (other.course != null)
        return false;
    } else if (!course.equals(other.course))
      return false;
    if (occurrence == null) {
      if (other.occurrence != null)
        return false;
    } else if (!occurrence.equals(other.occurrence))
      return false;
    if (severity == null) {
      if (other.severity != null)
        return false;
    } else if (!severity.equals(other.severity))
      return false;
    if (site == null) {
      if (other.site != null)
        return false;
    } else if (!site.equals(other.site))
      return false;
    if (subcondition == null) {
      if (other.subcondition != null)
        return false;
    } else if (!subcondition.equals(other.subcondition))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ConditionModel [condition=" + condition + ", subcondition="
        + subcondition + ", site=" + site + ", cause=" + cause + ", occurrence="
        + occurrence + ", course=" + course + ", category=" + category
        + ", severity=" + severity + "]";
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {
   // n/a    
  }

}
