package com.wci.tt.jpa.services.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.wci.tt.IdentifyHandler;
import com.wci.tt.NormalizeHandler;
import com.wci.tt.ProcessHandler;
import com.wci.tt.Provider;
import com.wci.umls.server.helpers.ConfigUtility;

public class ProviderUtility {

  private static List<Provider> providers = null;

  public static List<Provider> getProviders() {
    if (providers == null) {
      try {
        
        providers = new ArrayList<>();
        
        // get the specified providers
        Properties prop = ConfigUtility.getConfigProperties();

        Logger.getLogger(ProviderUtility.class.getName())
            .info("Provider classes specified: "
                + prop.getProperty("source.data.providers"));

        for (String providerName : prop.getProperty("source.data.providers")
            .split(",")) {
          
          String providerClassName = prop
              .getProperty("source.data.providers." + providerName + ".class");
          Logger.getLogger(ProviderUtility.class.getName())
              .info("Instantiating provider class " + providerClassName);
          Provider provider =
              (Provider) Class.forName(providerClassName).newInstance();

          // add the normalizers
          for (String normalizerClassName : prop
              .getProperty(
                  "source.data.providers." + providerName + ".normalizers")
              .split(",")) {

            Logger.getLogger(ProviderUtility.class.getName())
                .info("   Adding normalizer class " + normalizerClassName);

            NormalizeHandler normalizeHandler = (NormalizeHandler) Class
                .forName(normalizerClassName).newInstance();
            provider.addNormalizeHandler(normalizeHandler);
          }

          // add the identifiers
          for (String identifierClassName : prop
              .getProperty(
                  "source.data.providers." + providerName + ".identifiers")
              .split(",")) {

            Logger.getLogger(ProviderUtility.class.getName())
                .info("   Adding identifier class " + identifierClassName);
            IdentifyHandler identifyHandler = (IdentifyHandler) Class
                .forName(identifierClassName).newInstance();
            provider.addIdentifyHandler(identifyHandler);
          }

          // add the processors
          for (String processorClassName : prop
              .getProperty(
                  "source.data.providers." + providerName + ".processors")
              .split(",")) {
            Logger.getLogger(ProviderUtility.class.getName())
                .info("   Adding processor class " + processorClassName);
            ProcessHandler processHandler = (ProcessHandler) Class
                .forName(processorClassName).newInstance();
            provider.addProcessHandler(processHandler);
          }

          providers.add(provider);
        }
      } catch (Exception e) {
        e.printStackTrace();
        providers = null;
      }
    }

    return providers;
  }

}
