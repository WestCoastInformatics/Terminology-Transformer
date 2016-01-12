package com.wci.tt;

import com.wci.tt.helpers.ScoredResultList;

public interface NormalizeHandler {

  public ScoredResultList normalize(String inputStr, DataContext dataContext);
}
