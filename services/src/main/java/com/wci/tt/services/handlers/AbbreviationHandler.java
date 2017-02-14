/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.io.InputStream;
import java.util.List;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Configurable;
import com.wci.umls.server.helpers.PfsParameter;
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
   * @return the conflicts
   * @throws Exception the exception
   */
  public TypeKeyValueList getReviewForAbbreviation(TypeKeyValue abbr)
    throws Exception;

  /**
   * Compute abbreviation statuses.
   *
   * @param terminology the terminology
   * @throws Exception the exception
   */
  public void computeAbbreviationStatuses(String terminology) throws Exception;

  /**
   * Validate abbreviation file.
   *
   * @param terminology the terminology
   * @param inFile the in file
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateAbbreviationFile(String terminology,
    InputStream inFile) throws Exception;

  /**
   * Import abbreviation file.
   *
   * @param terminology the terminology
   * @param inFile the in file
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult importAbbreviationFile(String terminology,
    InputStream inFile) throws Exception;

  /**
   * Export abbreviation file.
   *
   * @param terminology the terminology
   * @param acceptNew the accept new
   * @param readyOnly the ready only
   * @return the input stream
   * @throws Exception the exception
   */
  public InputStream exportAbbreviationFile(String terminology, boolean acceptNew,
    boolean readyOnly) throws Exception;

  /**
   * Close any open resources on application shutdown.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception;

  /**
   * Update workflow status.
   *
   * @param abbr the abbr
   * @throws Exception the exception
   */
  public void updateWorkflowStatus(TypeKeyValue abbr) throws Exception;

  /**
   * Returns the review for abbreviations.
   *
   * @param abbrList the abbr list
   * @return the review for abbreviations
   * @throws Exception the exception
   */
  public TypeKeyValueList getReviewForAbbreviations(List<TypeKeyValue> abbrList)
    throws Exception;

  /**
   * Filter results.
   *
   * @param list the list
   * @param filter the filter
   * @param pfs the pfs
   * @return the type key value list
   * @throws Exception the exception
   */
  public TypeKeyValueList filterResults(TypeKeyValueList list, String filter,
    PfsParameter pfs) throws Exception;

  /**
   * Sets flag whether to check review status of imported content.
   *
   * @param reviewFlag the review flag
   */
  public void setReviewFlag(boolean reviewFlag);

  /**
   * Returns the type used in type-key-value triple for terminology
   *
   * @param terminology the terminology
   * @return the abbr type
   */
  public String getAbbrType(String terminology);

}
