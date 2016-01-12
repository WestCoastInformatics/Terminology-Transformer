package com.wci.tt.jpa.processors;

import com.wci.tt.DataContext;
import com.wci.tt.ProcessHandler;
import com.wci.tt.helpers.QualityResult;
import com.wci.tt.helpers.QualityResultList;
import com.wci.tt.jpa.helpers.QualityResultListJpa;

public class DefaultProcessHandler implements ProcessHandler {

  @Override
  public QualityResultList process(QualityResult qualityResult, DataContext dataContext) {
    QualityResultList qualityResultList = new QualityResultListJpa();
    qualityResultList.addObject(qualityResult);
    return qualityResultList;
  }

}
