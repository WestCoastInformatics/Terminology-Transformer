package com.wci.tt;

import com.wci.tt.helpers.QualityResult;
import com.wci.tt.helpers.QualityResultList;

public interface NormalizeHandler {

  public QualityResultList normalize(String inputStr, DataContext dataContext);
}
