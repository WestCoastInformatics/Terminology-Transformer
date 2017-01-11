/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.services.MetadataServiceJpa;
import com.wci.umls.server.rest.impl.ConfigureServiceRestImpl;
import com.wci.umls.server.services.MetadataService;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * Transformer applicatgion entry point (for jersey).
 */
@ApplicationPath("/")
public class TransformerServerApplication extends ResourceConfig {

  /** The API_VERSION - also used in "swagger.htmL" */
  public final static String API_VERSION = "1.0.0";

  /** The timer. */
  Timer timer;

  /**
   * Instantiates an empty {@link TransformerServerApplication}.
   *
   * @throws Exception the exception
   */
  public TransformerServerApplication() throws Exception {
    Logger.getLogger(getClass())
        .info("WCI Terminology Transformer APPLICATION START");
    
    // register REST implementations
    register(NdcServiceRestImpl.class);
    
    // register swagger classes
    register(io.swagger.jaxrs.listing.ApiListingResource.class);
    register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
    
    
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setTitle("WCI Terminology Transformer service API");
    beanConfig.setDescription("RESTful calls for WCI Terminology Transformer");
    beanConfig.setVersion(API_VERSION);
    beanConfig.setBasePath(
        ConfigUtility.getConfigProperties().getProperty("base.url"));
    beanConfig.setResourcePackage("com.wci.tt.rest.impl");
    beanConfig.setScan(true);

    // Set up a timer task to run at 2AM every day
    // Set up a timer task to run at 2AM every day
    TimerTask task = new InitializationTask();
    timer = new Timer();
    Calendar today = Calendar.getInstance();
    today.set(Calendar.HOUR_OF_DAY, 2);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    timer.scheduleAtFixedRate(task, today.getTime(), 6 * 60 * 60 * 1000);

  }

  /**
   * Initialization task.
   */
  class InitializationTask extends TimerTask {

    /* see superclass */
    @Override
    public void run() {
      try {

        // We need to "ping" the server to keep DB connections alive.
        // Do 4 times per day. Just get users list.
        Logger.getLogger(getClass()).info("  PING");
        if (new ConfigureServiceRestImpl().isConfigured()) {
          MetadataService service = new MetadataServiceJpa();
          service.getRootTerminologies();
          service.close();
        }

      } catch (Exception e) {
        timer.cancel();
        e.printStackTrace();
        Logger.getLogger(getClass()).error("Error running the process to xxx.");
      }
    }
  }


}