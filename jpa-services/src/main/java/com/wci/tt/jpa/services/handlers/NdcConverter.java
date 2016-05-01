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
import com.wci.tt.jpa.infomodels.NdcModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModel;
import com.wci.tt.jpa.infomodels.NdcPropertiesModelList;
import com.wci.tt.jpa.infomodels.RxcuiModel;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.handlers.ConverterHandler;

/**
 * Default implementation of {@link ConverterHandler}.
 * 
 * This class demonstrates a "naive" implementation of the Converter.
 * 
 * Class created to prove that supporting functionality works, not to provide
 * meaningful results.
 * 
 */
public class NdcConverter extends AbstractAcceptsHandler
    implements ConverterHandler {

  /**
   * Instantiates an empty {@link NdcConverter}.
   *
   * @throws Exception the exception
   */
  public NdcConverter() throws Exception {
    // Configure input/output matchers
    DataContextMatcher matcher = new DataContextMatcher();
    matcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        NdcModel.class.getName(), null, null);    
    addMatcher(matcher, matcher);
    
    matcher = new DataContextMatcher();
    matcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        RxcuiModel.class.getName(), null, null);    
    addMatcher(matcher, matcher);
    
    matcher = new DataContextMatcher();
    matcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        NdcPropertiesModel.class.getName(), null, null);    
    addMatcher(matcher, matcher);
      
    matcher = new DataContextMatcher();
    matcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        NdcPropertiesModelList.class.getName(), null, null);    
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
    return "NDC Converter Handler";
  }

  /* see superclass */
  @Override
  public DataContextTuple convert(String inputString, TransformRecord record)
    throws Exception {
    Logger.getLogger(getClass()).debug("  convert - " + inputString);
    
    final DataContext inputContext = record.getInputContext();
    final DataContext outputContext = record.getProviderOutputContext();
    // final String origInputString = record.getInputString();
    // final DataContext origInputContext = record.getOutputContext();

    // Validate input/output context
    validate(inputContext, outputContext);

    // DefaultHandler returns "converted form" of input as-is
    final DataContextTuple tuple = new DataContextTupleJpa();

    // Ensure that input is valid, then pass through
    if (inputString != null && !inputString.isEmpty() && inputContext != null
        && outputContext != null) {
      tuple.setDataContext(outputContext);
      tuple.setData(inputString);
    }

    return tuple;
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }

}
