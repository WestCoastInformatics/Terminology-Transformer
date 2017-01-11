/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.Properties;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.jpa.helpers.DataContextTupleJpa;
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
public class DefaultConverter extends AbstractAcceptsHandler
    implements ConverterHandler {

  /**
   * Instantiates an empty {@link DefaultConverter}.
   *
   * @throws Exception the exception
   */
  public DefaultConverter() throws Exception {
    // Configure input/output matchers
    // Takes a code/returns a code
    DataContextMatcher matcher = new DataContextMatcher();
    matcher.configureContext(DataContextType.NAME, null, null, null, null, null,
        null);
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
    return "Default Converter Handler";
  }

  /* see superclass */
  @Override
  public DataContextTuple convert(String inputString, TransformRecord record)
    throws Exception {

    final DataContext inputContext = record.getInputContext();
    final DataContext outputContext = record.getProviderOutputContext();
    // final String origInputString = record.getInputString();
    // final DataContext origInputContext = record.getOutputContext();

    // Validate input/output context
    validate(inputContext, outputContext);

    // DefaultHandler returns "converted form" of input as-is
    final DataContextTuple tuple = new DataContextTupleJpa();

    // Ensure that input is valid.
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
  
  @Override
  public void checkProperties(Properties arg0) throws Exception {
   // do nothing
  }

}
