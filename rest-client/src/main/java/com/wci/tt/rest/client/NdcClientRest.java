/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.client;

import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;

import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesListModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.RxcuiModel;
import com.wci.tt.jpa.services.rest.NdcServiceRest;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.rest.client.RootClientRest;

/**
 * Class calling the REST Service for Transform routines for
 * {@link TransformServiceRest}.
 */
public class NdcClientRest extends RootClientRest implements NdcServiceRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link NdcClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public NdcClientRest(Properties config) {
    this.config = config;
  }

  /* see superclass */
  @Override
  public NdcModel getNdcInfo(String ndc, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug("NDC Client - get ndc info - " + ndc);

    validateNotEmpty(ndc, "ndc");

    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(config.getProperty("base.url") + "/rxnorm/ndc/" + ndc);

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    NdcModel result = (NdcModel) ConfigUtility.getGraphForString(resultString,
        NdcModel.class);

    return result;
  }

  /* see superclass */
  @Override
  public RxcuiModel getRxcuiInfo(String rxcui, String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("NDC Client - get rxcui info - " + rxcui);

    validateNotEmpty(rxcui, "rxcui");

    Client client = ClientBuilder.newClient();
    WebTarget target = client
        .target(config.getProperty("base.url") + "/rxnorm/rxcui/" + rxcui);

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    RxcuiModel result = (RxcuiModel) ConfigUtility
        .getGraphForString(resultString, RxcuiModel.class);

    return result;
  }

  /* see superclass */
  @Override
  public NdcPropertiesModel getNdcProperties(String ndc, String authToken)
    throws Exception {
    Logger.getLogger(getClass())
        .debug("NDC Client - get NDC properties - " + ndc);

    validateNotEmpty(ndc, "ndc");

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(
        config.getProperty("base.url") + "/rxnorm/ndc/" + ndc + "/properties");

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    NdcPropertiesModel result = (NdcPropertiesModel) ConfigUtility
        .getGraphForString(resultString, NdcPropertiesModel.class);

    return result;
  }

  /* see superclass */
  @Override
  public NdcPropertiesListModel getNdcPropertiesForSplSetId(String splSetId,
    String authToken) throws Exception {
    Logger.getLogger(getClass())
        .debug("NDC Client - get NDC properties for SPL_SET_ID " + splSetId);

    validateNotEmpty(splSetId, "splSetId");

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(config.getProperty("base.url")
        + "/rxnorm/spl/" + splSetId + "/ndc/properties");

    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    NdcPropertiesListModel result = (NdcPropertiesListModel) ConfigUtility
        .getGraphForString(resultString, NdcPropertiesListModel.class);

    return result;
  }

}
