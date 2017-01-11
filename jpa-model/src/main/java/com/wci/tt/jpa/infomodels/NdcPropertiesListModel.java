/*
 *    Copyright 2015 West Coast Informatics, LLC
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
 *  { list: [{ rxcui: "1668240", 
 *     rxcuiName: "some drug name",
 *     ndc11 : "00069315083", 
 *     ndc10 : "0069-3150-83"
 *     ndc9 : "0069-3150",
 *     splSetId="3b631aa1-2d46-40bc-a614-d698301ea4f9", 
 *     propertyList: [
 *       {name : "DM_SPL_ID", value : "172467"},
 *       {name : "LABELER", value : "Pfizer Laboratories Div Pfizer Inc"},
 *       {name : "LABEL_TYPE", value : "HUMAN PRESCRIPTION DRUG LABEL"},
 *       {name : "MARKETING_CATEGORY", value : "NDA"},
 *       {name : "MARKETING_EFFECTIVE_TIME_LOW", value : "19970130"},
 *       {name : "MARKETING_STATUS", value : "active"},
 *       {name  : "NDA", value : "NDA050733"} 
 *     ], [...]
 *   }
 * </pre>
 */
@XmlRootElement(name = "ndcPropertiesList")
public class NdcPropertiesListModel
    implements InfoModel<NdcPropertiesListModel> {

  /** The rxcui. */
  private List<NdcPropertiesModel> list;

  /**
   * Instantiates an empty {@link NdcPropertiesListModel}.
   */
  public NdcPropertiesListModel() {
    // n/a
  }

  /**
   * Instantiates a {@link NdcPropertiesListModel} from the specified
   * parameters.
   *
   * @param model the model
   */
  public NdcPropertiesListModel(NdcPropertiesListModel model) {
    list = new ArrayList<>(model.getList());
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
    return "NDC Properties List Model";
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
   * Returns the list.
   *
   * @return the list
   */
  public List<NdcPropertiesModel> getList() {
    if (list == null) {
      list = new ArrayList<>();
    }
    return list;
  }

  /**
   * Sets the list.
   *
   * @param list the list
   */
  public void setList(List<NdcPropertiesModel> list) {
    this.list = list;
  }

  /* see superclass */
  @Override
  public boolean verify(String model) throws Exception {
    // Accept only JSON representation
    try {
      ConfigUtility.getGraphForJson(model, NdcPropertiesListModel.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* see superclass */
  public NdcPropertiesListModel getModel(String model) throws Exception {
    // Only accept json in correct format
    try {
      return ConfigUtility.getGraphForJson(model, NdcPropertiesListModel.class);
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
  public NdcPropertiesListModel getModelInCommon(NdcPropertiesListModel model,
    boolean analysisMode) throws Exception {
    if (model == null) {
      return null;
    }
    boolean found = false;
    NdcPropertiesListModel common = new NdcPropertiesListModel();

    if (model.getList() != null && list != null) {
      // Find common ingredient strength values
      for (final NdcPropertiesModel in : model.getList()) {
        for (final NdcPropertiesModel in2 : list) {
          final NdcPropertiesModel commonProperties =
              in.getModelInCommon(in2, analysisMode);
          if (commonProperties != null
              && !common.getList().contains(commonProperties)) {
            common.getList().add(commonProperties);
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
    result = prime * result + ((list == null) ? 0 : list.hashCode());
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
    NdcPropertiesListModel other = (NdcPropertiesListModel) obj;
    if (list == null) {
      if (other.list != null)
        return false;
    } else if (!list.equals(other.list))
      return false;

    return true;
  }

  @Override
  public String toString() {
    return "NdcPropertiesModelList [list=" + list + "]";
  }
  
  @Override
  public void checkProperties(Properties arg0) throws Exception {
   // do nothing
  }

}
