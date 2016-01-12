package com.wci.tt.jpa.providers;

import java.util.ArrayList;
import java.util.List;

import com.wci.tt.DataContext;
import com.wci.tt.IdentifyHandler;
import com.wci.tt.NormalizeHandler;
import com.wci.tt.ProcessHandler;
import com.wci.tt.Provider;
import com.wci.tt.helpers.ScoredResultList;
import com.wci.tt.jpa.helpers.ScoredResultListJpa;

public abstract class AbstractProvider implements Provider {

  private List<NormalizeHandler> normalizeHandlers = new ArrayList<>();

  private List<IdentifyHandler> identifyHandlers = new ArrayList<>();

  private List<ProcessHandler> processHandlers = new ArrayList<>();
  
  @Override
  public ScoredResultList processInput(String inputStr,
    DataContext dataContext) throws Exception {
   return new ScoredResultListJpa();
  }

  @Override
  public List<NormalizeHandler> getNormalizeHandlers() {
    return normalizeHandlers;
  }

  @Override
  public void setNormalizeHandlers(List<NormalizeHandler> normalizeHandlers) {
    this.normalizeHandlers = normalizeHandlers;
  }

  @Override
  public List<IdentifyHandler> getIdentifyHandlers() {
    return identifyHandlers;
  }

  @Override
  public void setIdentifyHandlers(List<IdentifyHandler> identifyHandlers) {
    this.identifyHandlers = identifyHandlers;
  }

  @Override
  public List<ProcessHandler> getProcessHandlers() {
    return processHandlers;
  }

  @Override
  public void setProcessHandlers(List<ProcessHandler> processHandlers) {
    this.processHandlers = processHandlers;
  }

  @Override
  public void addProcessHandler(ProcessHandler processHandler) {
    if (processHandlers == null) {
      processHandlers = new ArrayList<>();
    }
    processHandlers.add(processHandler);
  }

  @Override
  public void addIdentifyHandler(IdentifyHandler identifyHandler) {
    if (identifyHandlers == null) {
      identifyHandlers = new ArrayList<>();
    }
    identifyHandlers.add(identifyHandler);
  }

  @Override
  public void addNormalizeHandler(NormalizeHandler normalizeHandler) {
    if (normalizeHandlers == null) {
      normalizeHandlers = new ArrayList<>();
    }
    normalizeHandlers.add(normalizeHandler);
  }

}
