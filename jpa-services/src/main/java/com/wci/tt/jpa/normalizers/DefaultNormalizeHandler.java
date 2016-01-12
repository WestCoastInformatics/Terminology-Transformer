package com.wci.tt.jpa.normalizers;

import com.wci.tt.DataContext;
import com.wci.tt.NormalizeHandler;
import com.wci.tt.helpers.QualityResult;
import com.wci.tt.helpers.QualityResultList;
import com.wci.tt.jpa.helpers.QualityResultJpa;
import com.wci.tt.jpa.helpers.QualityResultListJpa;

/**
 * Default normalizer: Passes unchanged input
 *
 */
public class DefaultNormalizeHandler implements NormalizeHandler {


  @Override
  public QualityResultList normalize(String inputStr, DataContext dataContext) {

    QualityResultList qrl = new QualityResultListJpa();

    QualityResult qr = new QualityResultJpa();
    qr.setQuality(1.0f);
    qr.setValue(inputStr);

    qrl.addObject(qr);

    return qrl;

  }

}
