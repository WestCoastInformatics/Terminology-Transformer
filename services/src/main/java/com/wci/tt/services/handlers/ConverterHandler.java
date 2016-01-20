/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.util.List;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.umls.server.helpers.Configurable;

/**
 * Interface responsible for taking the input string defined by its input
 * context and converting it to output in the form defined by the output
 * context.
 * 
 * Results returned are ranked by probability per provider.
 */
public interface ConverterHandler extends Configurable {

  /**
   * Reports what Data Contexts a converter is setup to handle based on an input
   * context.
   *
   * Returns a list because some input contexts may be blank yet converter may
   * handle multiple data context options simultaneously.
   *
   * @param context the context
   * @return the list
   * @throws Exception the exception
   */
  public List<DataContext> accepts(DataContext context) throws Exception;

  /**
   * Changes the representation of the inputStr defined by the specified
   * input context into output in the form as defined by the output context.
   * 
   * Four convert possibilities exist:
   *    1) A code to an Information Model 
   *    2) A code to a code 
   *    3) A Information Model to a code 
   *    4) A Information Model to a Information Model
   *
   * @param inputStr the input string
   * @param inputContext the input context
   * @param outputContext the output context
   * @return the data context tuple
   * @throws Exception the exception
   */
  public DataContextTuple convert(String inputStr, DataContext inputContext,
    DataContext outputContext) throws Exception;
}
