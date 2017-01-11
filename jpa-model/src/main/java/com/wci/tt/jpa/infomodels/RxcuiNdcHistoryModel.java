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
 *   { history : [{ ndc : "312656", start : "200706", end : "201101" }]
 *
 * </pre>
 */
@XmlRootElement(name = "rxcuihistory")
public class RxcuiNdcHistoryModel implements InfoModel<RxcuiNdcHistoryModel> {

  /** The NDC. */
  private String ndc;

  /** The start. */
  private String start;

  /** The end. */
  private String end;

  /**
   * Instantiates an empty {@link RxcuiNdcHistoryModel}.
   */
  public RxcuiNdcHistoryModel() {
    // n/a
  }

  /**
   * Instantiates a {@link RxcuiNdcHistoryModel} from the specified parameters.
   *
   * @param model the model
   */
  public RxcuiNdcHistoryModel(RxcuiNdcHistoryModel model) {
    ndc = model.getNdc();
    start = model.getStart();
    end = model.getEnd();
  }

  /**
   * Instantiates a {@link RxcuiNdcHistoryModel} from the specified parameters.
   *
   * @param ndc the ndc
   * @param start the start
   * @param end the end date
   */
  public RxcuiNdcHistoryModel(String ndc, String start, String end) {
    this.ndc = ndc;
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
      ConfigUtility.getGraphForJson(model, RxcuiNdcHistoryModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  public RxcuiNdcHistoryModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, RxcuiNdcHistoryModel.class);
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
  public RxcuiNdcHistoryModel getModelInCommon(RxcuiNdcHistoryModel model,
    boolean analysisMode) throws Exception {
    if (model == null) {
      return null;
    }
    boolean found = false;
    RxcuiNdcHistoryModel common = new RxcuiNdcHistoryModel();

    if (model.getNdc() != null && ndc != null) {
      if (analysisMode && !model.getNdc().equals(ndc)) {
        common.setNdc(InfoModel.MULTIPLE_VALUES);
      } else if (model.getNdc().equals(ndc)) {
        common.setNdc(ndc);
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
    result = prime * result + ((ndc == null) ? 0 : ndc.hashCode());
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
    RxcuiNdcHistoryModel other = (RxcuiNdcHistoryModel) obj;
    if (end == null) {
      if (other.end != null)
        return false;
    } else if (!end.equals(other.end))
      return false;
    if (ndc == null) {
      if (other.ndc != null)
        return false;
    } else if (!ndc.equals(other.ndc))
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
    return "NdcHistoryModel [ndc=" + ndc + ", start=" + start + ", end=" + end
        + "]";
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {
    // do nothing
    
  }

}
