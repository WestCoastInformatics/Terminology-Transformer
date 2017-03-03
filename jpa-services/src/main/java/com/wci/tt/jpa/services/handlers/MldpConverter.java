/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.jpa.helpers.DataContextTupleJpa;
import com.wci.tt.jpa.infomodels.ConditionModel;
import com.wci.tt.jpa.infomodels.ProcedureModel;
import com.wci.tt.jpa.infomodels.SiteModel;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.handlers.ConverterHandler;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;

/**
 * Converter for MDLP services, converts identifiers to string names.
 */
public class MldpConverter extends AbstractAcceptsHandler
    implements ConverterHandler {

  /**
   * Instantiates an empty {@link MldpConverter}.
   *
   * @throws Exception the exception
   */
  public MldpConverter() throws Exception {
    // Configure input/output matchers

    // Convert a condition model to itself
    DataContextMatcher matcher = new DataContextMatcher();
    matcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        null, null, null);
    addMatcher(matcher, matcher);

  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public String getName() {
    return "MLDP Converter Handler";
  }

  /* see superclass */
  @Override
  public DataContextTuple convert(String inputString, TransformRecord record)
    throws Exception {
    Logger.getLogger(getClass()).debug("  convert - " + inputString);

    // Pass-thru converter
    DataContextTuple tuple = new DataContextTupleJpa();
    tuple.setData(inputString);
    tuple.setDataContext(record.getProviderOutputContext());

    return tuple;
  }

  /**
   * Returns the name for id.
   *
   * @param service the service
   * @param terminologyId the terminology id
   * @return the name for id
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  public String getNameForId(ContentService service, String terminologyId)
    throws Exception {
    if (terminologyId == null) {
      return null;
    }
    if (terminologyId.equals("")) {
      return "";
    }
    final Concept concept =
        service.getConcept(terminologyId, "HKT", "latest", Branch.ROOT);
    return concept.getName();
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }

  @Override
  public void checkProperties(Properties arg0) throws Exception {
    // n/a
  }
}
