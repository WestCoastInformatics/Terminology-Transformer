/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.io.InputStream;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Configurable;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.services.ProjectService;

/**
 * Interface responsible for analyzing input data and context and producing
 * characterizations and statistics suitable for use cases like machine learning
 * and reporting.
 */
public interface AbbreviationHandler extends Configurable {

  /**
   * Indicates whether or not header line is the case.
   *
   * @param line the line
   * @return <code>true</code> if so, <code>false</code> otherwise
   */

  public boolean isHeaderLine(String line);

  /**
   * Sets the service.
   *
   * @param service the service
   */
  public void setService(ProjectService service);

  
  /**
   * Returns the conflicts.
   *
   * @param abbr the abbr
   * @param service the service
   * @return the conflicts
   */
  public TypeKeyValueList getConflicts(TypeKeyValue abbr) throws Exception;

  /**
   * Validate abbreviation file.
   *
   * @param abbrType the abbr type
   * @param inFile the in file
   * @param service the service
   * @return the validation result
   * @throws Exception 
   */
  public ValidationResult validateAbbreviationFile(String abbrType,
    InputStream inFile) throws Exception;

  /**
   * Import abbreviation file.
   *
   * @param abbrType the abbr type
   * @param inFile the in file
   * @param service the service
   * @return the validation result
   * @throws Exception 
   */
  public ValidationResult importAbbreviationFile(String abbrType,
    InputStream inFile) throws Exception;

  public InputStream exportAbbreviationFile(String abbrType) throws Exception;

  /**
   * Close any open resources on application shutdown.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception;

}
