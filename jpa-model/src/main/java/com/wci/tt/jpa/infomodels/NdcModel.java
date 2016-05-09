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
 *    { active : "false", ndc: "19428372921", rxcui : "312656",
 *      rxcuiName: "Promazine 50 MG/ML Injectable Solution",
 *      history : [{ rxcui : "312656", active : "true", start : "200706", end : "201101" }]
 *    }
 * </pre>
 */
@XmlRootElement(name = "ndc")
public class NdcModel implements InfoModel<NdcModel> {

  /** The active flag. */
  private boolean active;

  /** The ndc. */
  private String ndc;

  /** The rxcui. */
  private String rxcui;

  /** The rxnorm concept name. */
  private String rxcuiName;

  /** The history. */
  private List<NdcHistoryModel> history;

  /**
   * Instantiates an empty {@link NdcModel}.
   */
  public NdcModel() {
    // n/a
  }

  /**
   * Instantiates a {@link NdcModel} from the specified parameters.
   *
   * @param model the model
   */
  public NdcModel(NdcModel model) {
    active = model.isActive();
    rxcui = model.getRxcui();
    rxcuiName = model.getRxcuiName();
    ndc = model.getNdc();
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
    return "NDC Model";
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
   * Returns the rxcui name.
   *
   * @return the rxcui name
   */
  public String getRxcuiName() {
    return rxcuiName;
  }

  /**
   * Sets the rxcui name.
   *
   * @param rxcuiName the rxcui name
   */
  public void setRxcuiName(String rxcuiName) {
    this.rxcuiName = rxcuiName;
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
      ConfigUtility.getGraphForJson(model, NdcModel.class);
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
  public NdcModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, NdcModel.class);
    } catch (Exception e) {
      throw new Exception("Malformed model - " + model);
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
  public NdcModel getModelInCommon(NdcModel model, boolean analysisMode)
    throws Exception {
    if (model == null) {
      return null;
    }
    boolean found = false;
    NdcModel common = new NdcModel();

    if (model.getRxcui() != null && rxcui != null) {
      if (analysisMode && !model.getRxcui().equals(rxcui)) {
        common.setRxcui(InfoModel.MULTIPLE_VALUES);
      } else if (model.getRxcui().equals(rxcui)) {
        common.setRxcui(rxcui);
        found = true;
      }
    }

    if (model.getRxcuiName() != null && rxcuiName != null) {
      if (analysisMode && !model.getRxcuiName().equals(rxcuiName)) {
        common.setRxcuiName(InfoModel.MULTIPLE_VALUES);
      } else if (model.getRxcuiName().equals(rxcuiName)) {
        common.setRxcuiName(rxcuiName);
        found = true;
      }
    }

    if (model.getNdc() != null && ndc != null) {
      if (analysisMode && !model.getNdc().equals(ndc)) {
        common.setNdc(InfoModel.MULTIPLE_VALUES);
      } else if (model.getNdc().equals(ndc)) {
        common.setNdc(ndc);
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

  /**
   * Hash code.
   *
   * @return the int
   */
  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + ((history == null) ? 0 : history.hashCode());
    result = prime * result + ((rxcui == null) ? 0 : rxcui.hashCode());
    result = prime * result + ((rxcuiName == null) ? 0 : rxcuiName.hashCode());
    result = prime * result + ((ndc == null) ? 0 : ndc.hashCode());
    return result;
  }

  /**
   * Equals.
   *
   * @param obj the obj
   * @return true, if successful
   */
  /* see superclass */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NdcModel other = (NdcModel) obj;
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
    if (rxcuiName == null) {
      if (other.rxcuiName != null)
        return false;
    } else if (!rxcuiName.equals(other.rxcuiName))
      return false;
    if (ndc == null) {
      if (other.ndc != null)
        return false;
    } else if (!ndc.equals(other.ndc))
      return false;
    return true;
  }

  /**
   * To string.
   *
   * @return the string
   */
  /* see superclass */
  @Override
  public String toString() {
    return "NdcModel [active=" + active + ", rxcui=" + rxcui + ", rxcuiName="
        + rxcuiName + ", ndc=" + ndc + ", history=" + history + "]";
  }

}
