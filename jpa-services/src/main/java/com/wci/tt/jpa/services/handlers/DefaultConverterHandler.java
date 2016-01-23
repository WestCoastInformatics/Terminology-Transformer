/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.jpa.helpers.DataContextJpa;
import com.wci.tt.jpa.helpers.DataContextTupleJpa;
import com.wci.tt.services.handlers.ConverterHandler;

/**
 * Default implementation of {@link ConverterHandler}.
 * 
 * This class demonstrates a "naive" implementation of the Converter.
 * 
 * Class created to prove that supporting functionality works, not to provide
 * meaningful results.
 * 
 * Thus, {@link AbstractAcceptsHandler} not extended.
 */
public class DefaultConverterHandler implements ConverterHandler {
  /**
   * Instantiates an empty {@link DefaultConverterHandler}.
   */
  public DefaultConverterHandler() {
    // n/a
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // N/A
  }

  /* see superclass */
  @Override
  public String getName() {
    return "Default Converter handler";
  }

  /* see superclass */
  @Override
  public List<DataContext> accepts(DataContext context) throws Exception {
    // DefaultHandler supports any context passed in
    List<DataContext> contexts = new ArrayList<DataContext>();

    // Ensure that input is valid although calling method with empty/null
    // context is permissible
    if (context != null) {
      contexts.add(context);
    } else {
      contexts.add(new DataContextJpa());
    }

    return contexts;
  }

  /* see superclass */
  @Override
  public DataContextTuple convert(String inputStr, DataContext inputContext,
    DataContext outputContext) throws Exception {
    // DefaultHandler returns "converted form" of input as-is
    DataContextTuple tuple = new DataContextTupleJpa();

    // Ensure that input is valid.
    if (inputStr != null && !inputStr.isEmpty() && inputContext != null
        && outputContext != null) {
      tuple.setDataContext(outputContext);
      tuple.setData(inputStr);
    }

    return tuple;
  }

}
