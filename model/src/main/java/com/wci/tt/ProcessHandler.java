package com.wci.tt;

import com.wci.tt.helpers.QualityResult;
import com.wci.tt.helpers.QualityResultList;

public interface  ProcessHandler {

  public QualityResultList process(QualityResult qr, DataContext dataContext);
}
