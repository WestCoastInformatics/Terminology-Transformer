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
import com.wci.tt.helpers.QualityResult;
import com.wci.tt.helpers.QualityResultList;
import com.wci.tt.jpa.helpers.QualityResultListJpa;
import com.wci.tt.jpa.identifiers.DefaultIdentifyHandler;
import com.wci.tt.jpa.normalizers.DefaultNormalizeHandler;
import com.wci.tt.jpa.processors.DefaultProcessHandler;

public class DefaultProvider implements Provider {

  private List<NormalizeHandler> normalizeHandlers = new ArrayList<>();

  private List<IdentifyHandler> identifyHandlers = new ArrayList<>();

  private List<ProcessHandler> processHandlers = new ArrayList<>();

  public DefaultProvider() {
    this.addNormalizeHandler(new DefaultNormalizeHandler());
    this.addIdentifyHandler(new DefaultIdentifyHandler());
    this.addProcessHandler(new DefaultProcessHandler());
  }

  @Override
  public QualityResultList processInput(String inputStr,
    DataContext dataContext) throws Exception {

    Logger.getLogger(this.getClass()).info("Processing input: " + inputStr);

    Set<QualityResult> normalizedQRs = new HashSet<>();
    Set<QualityResult> identifiedQRs = new HashSet<>();
    Set<QualityResult> processedQRs = new HashSet<>();


    // normalize
    for (NormalizeHandler handler : normalizeHandlers) {
      for (QualityResult qr : handler.normalize(inputStr, dataContext)
          .getObjects()) {
        normalizedQRs.add(qr);
        Logger.getLogger(this.getClass())
            .info("  Normalized input: " + qr.getValue());
      }
    }

    // identify
    for (IdentifyHandler handler : identifyHandlers) {
      for (QualityResult qrNorm : normalizedQRs) {
        for (QualityResult qrId : handler.identify(qrNorm, dataContext)
            .getObjects()) {
          identifiedQRs.add(qrId);
          Logger.getLogger(this.getClass())
              .info("  Identified input: " + qrId.getValue());
        }
      }
    }

    // process
    for (ProcessHandler handler : processHandlers) {
      for (QualityResult qrId : identifiedQRs) {
        for (QualityResult qrProc : handler.process(qrId, dataContext)
            .getObjects()) {
          processedQRs.add(qrProc);
          Logger.getLogger(this.getClass())
              .info("  Processed input: " + qrId.getValue());

        }
      }
    }

    QualityResultList results = new QualityResultListJpa();
    for (QualityResult qrProc : processedQRs) {
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
