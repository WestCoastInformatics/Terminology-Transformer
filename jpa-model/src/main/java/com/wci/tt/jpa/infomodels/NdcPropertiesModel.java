/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.infomodels;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.wci.tt.infomodels.InfoModel;
import com.wci.umls.server.helpers.ConfigUtility;

/**
 * Information model for representing NDC properties.
 * 
 * <pre>
 *  { rxcui: "1668240", 
 *    ndc11 : "00069315083", 
 *    ndc10 : "0069-3150-83"
 *    ndc9 : "0069-3150",
 *    splSetId="3b631aa1-2d46-40bc-a614-d698301ea4f9", 
 *    propertyList: [
 *      {prop : "DM_SPL_ID", value : "172467"},
 *      {prop : "LABELER", value : "Pfizer Laboratories Div Pfizer Inc"},
 *      {prop : "LABEL_TYPE", value : "HUMAN PRESCRIPTION DRUG LABEL"},
 *      {prop : "MARKETING_CATEGORY", value : "NDA"},
 *      {prop : "MARKETING_EFFECTIVE_TIME_LOW", value : "19970130"},
 *      {prop : "MARKETING_STATUS", value : "active"},
 *      {prop  : "NDA", value : "NDA050733"} 
 *    ]
 *   }
 * </pre>
 */
@XmlRootElement(name = "ndcProperties")
public class NdcPropertiesModel implements InfoModel<NdcPropertiesModel> {

  /** The rxcui. */
  private String rxcui;

  /** The ndc. */
  private String ndc11;

  /** The ndc. */
  private String ndc10;

  /** The ndc. */
  private String ndc9;

  /** The spl set id. */
  private String splSetId;

  /** The properties. */
  private List<PropertyModel> propertyList;

  /**
   * Instantiates an empty {@link NdcPropertiesModel}.
   */
  public NdcPropertiesModel() {
    // n/a
  }

  /**
   * Instantiates a {@link NdcPropertiesModel} from the specified parameters.
   *
   * @param model the model
   */
  public NdcPropertiesModel(NdcPropertiesModel model) {
    rxcui = model.getRxcui();
    ndc11 = model.getNdc11();
    ndc10 = model.getNdc10();
    ndc9 = model.getNdc9();
    splSetId = model.getSplSetId();
    propertyList = new ArrayList<>(model.getPropertyList());
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
    return "NDC Properties Model";
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
   * Returns the 11-digit ndc code.
   *
   * @return the 11-digit ndc code.
   */
  public String getNdc11() {
    return ndc11;
  }

  /**
   * Sets the 11-digit ndc code.
   *
   * @param ndc11 the ndc11
   */
  public void setNdc11(String ndc11) {
    this.ndc11 = ndc11;
  }

  /**
   * Returns the 10-digit ndc code.
   *
   * @return the 10-digit ndc code.
   */
  public String getNdc10() {
    return ndc10;
  }

  /**
   * Sets the 10-digit ndc code.
   *
   * @param ndc10 the ndc10
   */
  public void setNdc10(String ndc10) {
    this.ndc10 = ndc10;
  }

  /**
   * Returns the 9-digit ndc code.
   *
   * @return the 9-digit ndc code.
   */
  public String getNdc9() {
    return ndc9;
  }

  /**
   * Sets the 9-digit ndc code.
   *
   * @param ndc9 the ndc9
   */
  public void setNdc9(String ndc9) {
    this.ndc9 = ndc9;
  }

  /**
   * Returns the spl set id.
   *
   * @return the spl set id
   */
  public String getSplSetId() {
    return splSetId;
  }

  /**
   * Sets the spl set id.
   *
   * @param splSetId the spl set id
   */
  public void setSplSetId(String splSetId) {
    this.splSetId = splSetId;
  }

  /**
   * Returns the properties.
   *
   * @return the properties
   */
  public List<PropertyModel> getPropertyList() {
    if (propertyList == null) {
      propertyList = new ArrayList<>();
    }
    return propertyList;
  }

  /**
   * Sets the property list.
   *
   * @param propertyList the property list
   */
  public void setPropertyList(List<PropertyModel> propertyList) {
    this.propertyList = propertyList;
  }

  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, NdcPropertiesModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  public NdcPropertiesModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, NdcPropertiesModel.class);
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
  public NdcPropertiesModel getModelInCommon(NdcPropertiesModel model,
    boolean analysisMode) throws Exception {
    if (model == null) {
      return null;
    }
    boolean found = false;
    NdcPropertiesModel common = new NdcPropertiesModel();

    if (model.getRxcui() != null && rxcui != null) {
      if (analysisMode && !model.getRxcui().equals(rxcui)) {
        common.setRxcui(InfoModel.MULTIPLE_VALUES);
      } else if (model.getRxcui().equals(rxcui)) {
        common.setRxcui(rxcui);
        found = true;
      }
    }

    if (model.getNdc11() != null && ndc11 != null) {
      if (analysisMode && !model.getNdc11().equals(ndc11)) {
        common.setNdc11(InfoModel.MULTIPLE_VALUES);
      } else if (model.getNdc11().equals(ndc11)) {
        common.setNdc11(ndc11);
        found = true;
      }
    }
    if (model.getNdc10() != null && ndc10 != null) {
      if (analysisMode && !model.getNdc10().equals(ndc10)) {
        common.setNdc10(InfoModel.MULTIPLE_VALUES);
      } else if (model.getNdc10().equals(ndc10)) {
        common.setNdc10(ndc10);
        found = true;
      }
    }
    if (model.getNdc9() != null && ndc9 != null) {
      if (analysisMode && !model.getNdc9().equals(ndc9)) {
        common.setNdc9(InfoModel.MULTIPLE_VALUES);
      } else if (model.getNdc9().equals(ndc9)) {
        common.setNdc9(ndc9);
        found = true;
      }
    }

    if (model.getSplSetId() != null && splSetId != null) {
      if (analysisMode && !model.getSplSetId().equals(splSetId)) {
        common.setSplSetId(InfoModel.MULTIPLE_VALUES);
      } else if (model.getSplSetId().equals(splSetId)) {
        common.setSplSetId(splSetId);
        found = true;
      }
    }
    if (model.getPropertyList() != null && propertyList != null) {
      // Find common ingredient strength values
      for (final PropertyModel in : model.getPropertyList()) {
        for (final PropertyModel in2 : propertyList) {
          final PropertyModel commonProperty =
              in.getModelInCommon(in2, analysisMode);
          if (commonProperty != null
              && !common.getPropertyList().contains(commonProperty)) {
            common.getPropertyList().add(commonProperty);
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
    result = prime * result + ((ndc10 == null) ? 0 : ndc10.hashCode());
    result = prime * result + ((ndc11 == null) ? 0 : ndc11.hashCode());
    result = prime * result + ((ndc9 == null) ? 0 : ndc9.hashCode());
    result =
        prime * result + ((propertyList == null) ? 0 : propertyList.hashCode());
    result = prime * result + ((rxcui == null) ? 0 : rxcui.hashCode());
    result = prime * result + ((splSetId == null) ? 0 : splSetId.hashCode());
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
    NdcPropertiesModel other = (NdcPropertiesModel) obj;
    if (ndc10 == null) {
      if (other.ndc10 != null)
        return false;
    } else if (!ndc10.equals(other.ndc10))
      return false;
    if (ndc11 == null) {
      if (other.ndc11 != null)
        return false;
    } else if (!ndc11.equals(other.ndc11))
      return false;
    if (ndc9 == null) {
      if (other.ndc9 != null)
        return false;
    } else if (!ndc9.equals(other.ndc9))
      return false;
    if (propertyList == null) {
      if (other.propertyList != null)
        return false;
    } else if (!propertyList.equals(other.propertyList))
      return false;
    if (rxcui == null) {
      if (other.rxcui != null)
        return false;
    } else if (!rxcui.equals(other.rxcui))
      return false;
    if (splSetId == null) {
      if (other.splSetId != null)
        return false;
    } else if (!splSetId.equals(other.splSetId))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "NdcPropertiesModel [rxcui=" + rxcui + ", ndc11=" + ndc11
        + ", ndc10=" + ndc10 + ", ndc9=" + ndc9 + ", splSetId=" + splSetId
        + ", propertyList=" + propertyList + "]";
  }

}
