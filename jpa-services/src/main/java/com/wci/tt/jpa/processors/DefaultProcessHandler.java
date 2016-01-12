package com.wci.tt.jpa.processors;

import com.wci.tt.DataContext;
import com.wci.tt.ProcessHandler;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.ScoredResultList;
import com.wci.tt.jpa.helpers.ScoredResultListJpa;

public class DefaultProcessHandler implements ProcessHandler {

  @Override
  public ScoredResultList process(ScoredResult qualityResult, DataContext dataContext) {
    ScoredResultList qualityResultList = new ScoredResultListJpa();
    qualityResultList.addObject(qualityResult);
    return qualityResultList;
  }

}
