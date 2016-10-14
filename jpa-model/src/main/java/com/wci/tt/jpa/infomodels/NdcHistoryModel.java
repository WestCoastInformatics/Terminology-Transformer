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
 *   { history : [{ rxcui : "312656", start : "200706", end : "201101" }]
 *
 * </pre>
 */
@XmlRootElement(name = "history")
public class NdcHistoryModel implements InfoModel<NdcHistoryModel> {

  /** The RXCUI. */
  private String rxcui;

  /** The start date. */
  private String start;

  /** The end date. */
  private String end;

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
    start = model.getStart();
    end = model.getEnd();
  }

  /**
   * Instantiates a {@link NdcHistoryModel} from the specified parameters.
   *
   * @param rxcui the rxcui
   * @param start the start date
   * @param end the end date
   */
  public NdcHistoryModel(String rxcui, String start, String end) {
    this.rxcui = rxcui;
    this.start = start;
    this.end = end;
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
   * Returns the start.
   *
   * @return the start
   */
  public String getStart() {
    return start;
  }

  /**
   * Sets the start.
   *
   * @param start the start
   */
  public void setStart(String start) {
    this.start = start;
  }

  /**
   * Returns the end.
   *
   * @return the end
   */
  public String getEnd() {
    return end;
  }

  /**
   * Sets the end.
   *
   * @param end the end
   */
  public void setEnd(String end) {
    this.end = end;
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
  @Override
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

    if (model.getStart() != null && start != null) {
      if (analysisMode && !model.getStart().equals(start)) {
        common.setStart(InfoModel.MULTIPLE_VALUES);
      } else if (model.getStart().equals(start)) {
        common.setStart(start);
        found = true;
      }
    }

    if (model.getEnd() != null && end != null) {
      if (analysisMode && !model.getEnd().equals(end)) {
        common.setEnd(InfoModel.MULTIPLE_VALUES);
      } else if (model.getEnd().equals(end)) {
        common.setEnd(end);
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
    result = prime * result + ((end == null) ? 0 : end.hashCode());
    result = prime * result + ((rxcui == null) ? 0 : rxcui.hashCode());
    result = prime * result + ((start == null) ? 0 : start.hashCode());
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
    if (end == null) {
      if (other.end != null)
        return false;
    } else if (!end.equals(other.end))
      return false;
    if (rxcui == null) {
      if (other.rxcui != null)
        return false;
    } else if (!rxcui.equals(other.rxcui))
      return false;
    if (start == null) {
      if (other.start != null)
        return false;
    } else if (!start.equals(other.start))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "NdcHistoryModel [rxcui=" + rxcui + ", start=" + start + ", end="
        + end + "]";
  }

}
