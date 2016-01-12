package com.wci.tt;

import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.ScoredResultList;

public interface  ProcessHandler {

  public ScoredResultList process(ScoredResult qr, DataContext dataContext);
}
