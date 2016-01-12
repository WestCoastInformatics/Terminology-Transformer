package com.wci.tt;

import java.util.List;

import com.wci.tt.helpers.ScoredResultList;

public interface Provider {
  
  public List<IdentifyHandler> getIdentifyHandlers();
  
  public void setIdentifyHandlers (List<IdentifyHandler> identifiers);
  
  public List<ProcessHandler> getProcessHandlers();
  
  public void setProcessHandlers(List<ProcessHandler> processHandlers);
  
  public List<NormalizeHandler> getNormalizeHandlers();
  
  public void setNormalizeHandlers(List<NormalizeHandler> normalizeHandlers);
  
  public ScoredResultList processInput(String inputStr, DataContext dataContext) throws Exception;

  void addNormalizeHandler(NormalizeHandler normalizeHandler);

  void addIdentifyHandler(IdentifyHandler identifyHandler);

  void addProcessHandler(ProcessHandler processHandler);
  
}
