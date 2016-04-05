/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.client;

import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;

import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.services.rest.NdcRxnormRest;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.rest.client.RootClientRest;

/**
 * Class calling the REST Service for Transform routines for
 * {@link TransformServiceRest}.
 */
public class NdcRxnormClientRest extends RootClientRest
    implements NdcRxnormRest {

  /** The config. */
  private Properties config = null;

  /**
   * Instantiates a {@link NdcRxnormClientRest} from the specified parameters.
   *
   * @param config the config
   */
  public NdcRxnormClientRest(Properties config) {
    this.config = config;
  }

  /* see superclass */
  @Override
  public void loadTerminologyNdc(String terminology, String version,
    String inputDir, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug("Content Client - load terminology ndc "
        + terminology + ", " + version + ", " + inputDir);

    validateNotEmpty(terminology, "terminology");
    validateNotEmpty(version, "version");
    validateNotEmpty(inputDir, "inputDir");

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(
        config.getProperty("base.url") + "/terminology/load/ndc?terminology="
            + terminology + "&version=" + version);

    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken).put(Entity.text(inputDir));

    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // do nothing
    } else {
      throw new Exception("Unexpected status " + response.getStatus());
    }

  }
  
  /* see superclass */
  @Override
  public NdcModel process(String inputStr, String authToken) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Transform Client - identify " + inputStr );

    validateNotEmpty(inputStr, "inputStr");

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(
        config.getProperty("base.url") + "/transform/process/" + inputStr);



    // Call Rest method
    Response response = target.request(MediaType.APPLICATION_XML)
        .header("Authorization", authToken)
        .get();

    String resultString = response.readEntity(String.class);
    if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
      // n/a
    } else {
      throw new Exception(resultString);
    }

    // converting to object
    NdcModel result =
        (NdcModel) ConfigUtility.getGraphForString(
            resultString, NdcModel.class);

    return result;
  }


}
