package com.wci.tt.jpa.providers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.IdentifyHandler;
import com.wci.tt.NormalizeHandler;
import com.wci.tt.ProcessHandler;
import com.wci.tt.Provider;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.ScoredResultList;
import com.wci.tt.jpa.helpers.ScoredResultListJpa;

public class DefaultProvider implements Provider {

  private List<NormalizeHandler> normalizeHandlers = new ArrayList<>();

  private List<IdentifyHandler> identifyHandlers = new ArrayList<>();

  private List<ProcessHandler> processHandlers = new ArrayList<>();

  public DefaultProvider() throws Exception {
    super();
  }

  @Override
  public ScoredResultList processInput(String inputStr,
    DataContext dataContext) throws Exception {

    Logger.getLogger(this.getClass()).info("Processing input: " + inputStr);
    Logger.getLogger(this.getClass()).info("  # of normalizers/identifiers/processors: " + normalizeHandlers.size() + "/" + identifyHandlers.size() + "/" + processHandlers.size());;

    Set<ScoredResult> normalizedQRs = new HashSet<>();
    Set<ScoredResult> identifiedQRs = new HashSet<>();
    Set<ScoredResult> processedQRs = new HashSet<>();


    // normalize
    for (NormalizeHandler handler : normalizeHandlers) {
      for (ScoredResult qr : handler.normalize(inputStr, dataContext)
          .getObjects()) {
        normalizedQRs.add(qr);
        Logger.getLogger(this.getClass())
            .info("  Normalized input: " + qr.getValue());
      }
    }

    // identify
    for (IdentifyHandler handler : identifyHandlers) {
      for (ScoredResult qrNorm : normalizedQRs) {
        for (ScoredResult qrId : handler.identify(qrNorm, dataContext)
            .getObjects()) {
          identifiedQRs.add(qrId);
          Logger.getLogger(this.getClass())
              .info("  Identified input: " + qrId.getValue());
        }
      }
    }

    // process
    for (ProcessHandler handler : processHandlers) {
      for (ScoredResult qrId : identifiedQRs) {
        for (ScoredResult qrProc : handler.process(qrId, dataContext)
            .getObjects()) {
          processedQRs.add(qrProc);
          Logger.getLogger(this.getClass())
              .info("  Processed input: " + qrId.getValue());

        }
      }
    }

    ScoredResultList results = new ScoredResultListJpa();
    for (ScoredResult qrProc : processedQRs) {
      results.addObject(qrProc);
    }
    return results;
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
