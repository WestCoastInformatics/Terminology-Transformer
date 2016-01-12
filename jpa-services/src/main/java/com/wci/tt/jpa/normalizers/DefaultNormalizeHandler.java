package com.wci.tt.jpa.normalizers;

import com.wci.tt.DataContext;
import com.wci.tt.NormalizeHandler;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.ScoredResultList;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.jpa.helpers.ScoredResultListJpa;

/**
 * Default normalizer: Passes unchanged input
 *
 */
public class DefaultNormalizeHandler implements NormalizeHandler {


  @Override
  public ScoredResultList normalize(String inputStr, DataContext dataContext) {

    ScoredResultList qrl = new ScoredResultListJpa();

    ScoredResult qr = new ScoredResultJpa();
    qr.setQuality(1.0f);
    qr.setValue(inputStr);

    qrl.addObject(qr);

    return qrl;

  }

}
