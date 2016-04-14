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
 * Information model for representing NDC-RXNORM history.
 * 
 * <pre>
 *   { history : [{ ndc : "312656", startDate : "200706", endDate : "201101" }]
 *
 * </pre>
 */
@XmlRootElement(name = "rxcuihistory")
public class RxcuiHistoryModel implements InfoModel<RxcuiHistoryModel> {

  /** The NDC. */
  private String ndc;

  /** The start date. */
  private String startDate;

  /** The end date. */
  private String endDate;

  /**
   * Instantiates an empty {@link RxcuiHistoryModel}.
   */
  public RxcuiHistoryModel() {
    // n/a
  }

  /**
   * Instantiates a {@link RxcuiHistoryModel} from the specified parameters.
   *
   * @param model the model
   */
  public RxcuiHistoryModel(RxcuiHistoryModel model) {
    ndc = model.getNdc();
    startDate = model.getStartDate();
    endDate = model.getEndDate();
  }

  /**
   * Instantiates a {@link RxcuiHistoryModel} from the specified parameters.
   *
   * @param ndc the ndc
   * @param startDate the start date
   * @param endDate the end date
   */
  public RxcuiHistoryModel(String ndc, String startDate, String endDate) {
    this.ndc = ndc;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  /**
   * Returns the name.
   *
   * @return the name
   */
  /* see superclass */
  @XmlTransient
  @Override
  public String getName() {
    return "Rxcui History Model";
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
   * Returns the ndc.
   *
   * @return the ndc
   */
  public String getNdc() {
    return ndc;
  }

  /**
   * Sets the ndc.
   *
   * @param ndc the ndc
   */
  public void setNdc(String ndc) {
    this.ndc = ndc;
  }

  /**
   * Returns the startDate.
   *
   * @return the startDate
   */
  public String getStartDate() {
    return startDate;
  }

  /**
   * Sets the startDate.
   *
   * @param startDate the startDate
   */
  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  /**
   * Returns the endDate.
   *
   * @return the endDate
   */
  public String getEndDate() {
    return endDate;
  }

  /**
   * Sets the endDate.
   *
   * @param endDate the endDate
   */
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, RxcuiHistoryModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  public RxcuiHistoryModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, RxcuiHistoryModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model);
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
  public RxcuiHistoryModel getModelInCommon(RxcuiHistoryModel model,
    boolean analysisMode) throws Exception {
    if (model == null) {
      return null;
    }
    boolean found = false;
    RxcuiHistoryModel common = new RxcuiHistoryModel();

    if (model.getNdc() != null && ndc != null) {
      if (analysisMode && !model.getNdc().equals(ndc)) {
        common.setNdc(InfoModel.MULTIPLE_VALUES);
      } else if (model.getNdc().equals(ndc)) {
        common.setNdc(ndc);
        found = true;
      }
    }

    if (model.getStartDate() != null && startDate != null) {
      if (analysisMode && !model.getStartDate().equals(startDate)) {
        common.setStartDate(InfoModel.MULTIPLE_VALUES);
      } else if (model.getStartDate().equals(startDate)) {
        common.setStartDate(startDate);
        found = true;
      }
    }

    if (model.getEndDate() != null && endDate != null) {
      if (analysisMode && !model.getEndDate().equals(endDate)) {
        common.setEndDate(InfoModel.MULTIPLE_VALUES);
      } else if (model.getEndDate().equals(endDate)) {
        common.setEndDate(endDate);
        found = true;
      }
    }

    if (!found) {
      return null;
    }
    return common;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
    result = prime * result + ((ndc == null) ? 0 : ndc.hashCode());
    result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
    RxcuiHistoryModel other = (RxcuiHistoryModel) obj;
    if (endDate == null) {
      if (other.endDate != null)
        return false;
    } else if (!endDate.equals(other.endDate))
      return false;
    if (ndc == null) {
      if (other.ndc != null)
        return false;
    } else if (!ndc.equals(other.ndc))
      return false;
    if (startDate == null) {
      if (other.startDate != null)
        return false;
    } else if (!startDate.equals(other.startDate))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "NdcHistoryModel [ndc=" + ndc + ", startDate=" + startDate
        + ", endDate=" + endDate + "]";
  }

}
