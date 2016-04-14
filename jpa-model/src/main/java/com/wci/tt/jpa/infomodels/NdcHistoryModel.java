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
 *   { history : [{ rxcui : "312656", startDate : "200706", endDate : "201101" }]
 *
 * </pre>
 */
@XmlRootElement(name = "history")
public class NdcHistoryModel implements InfoModel<NdcHistoryModel> {

  /** The RXCUI. */
  private String rxcui;

  /** The start date. */
  private String startDate;

  /** The end date. */
  private String endDate;

  /**
   * Instantiates an empty {@link NdcHistoryModel}.
   */
  public NdcHistoryModel() {
    // n/a
  }

  /**
   * Instantiates a {@link NdcHistoryModel} from the specified parameters.
   *
   * @param model the model
   */
  public NdcHistoryModel(NdcHistoryModel model) {
    rxcui = model.getRxcui();
    startDate = model.getStartDate();
    endDate = model.getEndDate();
  }

  /**
   * Instantiates a {@link NdcHistoryModel} from the specified parameters.
   *
   * @param rxcui the rxcui
   * @param startDate the start date
   * @param endDate the end date
   */
  public NdcHistoryModel(String rxcui, String startDate, String endDate) {
    this.rxcui = rxcui;
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
    return "NDC History Model";
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
   * Returns the rxcui.
   *
   * @return the rxcui
   */
  public String getRxcui() {
    return rxcui;
  }

  /**
   * Sets the rxcui.
   *
   * @param rxcui the rxcui
   */
  public void setRxcui(String rxcui) {
    this.rxcui = rxcui;
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
      ConfigUtility.getGraphForJson(model, NdcHistoryModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  public NdcHistoryModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, NdcHistoryModel.class);
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
  public NdcHistoryModel getModelInCommon(NdcHistoryModel model,
    boolean analysisMode) throws Exception {
    if (model == null) {
      return null;
    }
    boolean found = false;
    NdcHistoryModel common = new NdcHistoryModel();

    if (model.getRxcui() != null && rxcui != null) {
      if (analysisMode && !model.getRxcui().equals(rxcui)) {
        common.setRxcui(InfoModel.MULTIPLE_VALUES);
      } else if (model.getRxcui().equals(rxcui)) {
        common.setRxcui(rxcui);
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
    result = prime * result + ((rxcui == null) ? 0 : rxcui.hashCode());
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
    NdcHistoryModel other = (NdcHistoryModel) obj;
    if (endDate == null) {
      if (other.endDate != null)
        return false;
    } else if (!endDate.equals(other.endDate))
      return false;
    if (rxcui == null) {
      if (other.rxcui != null)
        return false;
    } else if (!rxcui.equals(other.rxcui))
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
    return "NdcHistoryModel [rxcui=" + rxcui + ", startDate=" + startDate
        + ", endDate=" + endDate + "]";
  }

}
