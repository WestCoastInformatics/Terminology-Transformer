package com.wci.tt;

import com.wci.tt.helpers.QualityResult;
import com.wci.tt.helpers.QualityResultList;

/**
 * The Interface IdentifyHandler.
 */
public interface IdentifyHandler {

  /**
   * Identify.
   *
   * @param string the string
   * @param dataContext the data context
   * @return the quality result list
   * @throws Exception the exception
   */
  QualityResultList identify(String string, DataContext dataContext)
    throws Exception;

  /**
   * Identify.
   *
   * @param qr the qr
   * @param dataContext the data context
   * @return the quality result list
   * @throws Exception the exception
   */
  QualityResultList identify(QualityResult qr, DataContext dataContext)
    throws Exception;
}
