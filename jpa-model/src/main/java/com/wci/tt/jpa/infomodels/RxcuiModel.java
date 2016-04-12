/*
 *    Copyright 2016 West Coast Informatics, LLC
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
 * Information model for representing NDC-RXNORM history.
 * 
 * <pre>
 *    { active : "false", rxcui : "312656",
 *      history : [{ rxcui : "312656", active : "true", startDate : "200706", endDate : "201101" }]
 *    }
 * </pre>
 */
@XmlRootElement(name = "rxcui")
public class RxcuiModel implements InfoModel<RxcuiModel> {

  /** The active flag. */
  private boolean active;

  /** The rxcui. */
  private String rxcui;

  /** The history. */
  private List<NdcHistoryModel> history;

  /**
   * Instantiates an empty {@link RxcuiModel}.
   */
  public RxcuiModel() {
    // n/a
  }

  /**
   * Instantiates a {@link RxcuiModel} from the specified parameters.
   *
   * @param model the model
   */
  public RxcuiModel(RxcuiModel model) {
    active = model.isActive();
    rxcui = model.getRxcui();
    history = new ArrayList<>(model.getHistory());
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
    return "NDC Information Model";
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
   * Indicates whether or not active is the case.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets the active.
   *
   * @param active the active
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Returns the history.
   *
   * @return the history
   */
  @XmlElement(name = "history")
  public List<NdcHistoryModel> getHistory() {
    if (history == null) {
      history = new ArrayList<>();
    }
    return history;
  }

  /**
   * Sets the history.
   *
   * @param history the history
   */
  public void setHistory(List<NdcHistoryModel> history) {
    this.history = history;
  }

  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, RxcuiModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  public RxcuiModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, RxcuiModel.class);
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
  public RxcuiModel getModelInCommon(RxcuiModel model, boolean analysisMode)
    throws Exception {
    if (model == null) {
      return null;
    }
    boolean found = false;
    RxcuiModel common = new RxcuiModel();

    if (model.getRxcui() != null && rxcui != null) {
      if (analysisMode && !model.getRxcui().equals(rxcui)) {
        common.setRxcui(InfoModel.MULTIPLE_VALUES);
      } else if (model.getRxcui().equals(rxcui)) {
        common.setRxcui(rxcui);
        found = true;
      }
    }

    if (model.getHistory() != null && history != null) {
      // Find common ingredient strength values
      for (final NdcHistoryModel in : model.getHistory()) {
        for (final NdcHistoryModel in2 : history) {
          NdcHistoryModel commonIngredient =
              in.getModelInCommon(in2, analysisMode);
          if (commonIngredient != null
              && !common.getHistory().contains(commonIngredient)) {
            common.getHistory().add(commonIngredient);
          }
          found = true;
        }
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
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + ((history == null) ? 0 : history.hashCode());
    result = prime * result + ((rxcui == null) ? 0 : rxcui.hashCode());
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
    RxcuiModel other = (RxcuiModel) obj;
    if (active != other.active)
      return false;
    if (history == null) {
      if (other.history != null)
        return false;
    } else if (!history.equals(other.history))
      return false;
    if (rxcui == null) {
      if (other.rxcui != null)
        return false;
    } else if (!rxcui.equals(other.rxcui))
      return false;

    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "NdcModel [active=" + active + ", rxcui=" + rxcui
        + ", history=" + history + "]";
  }

}
