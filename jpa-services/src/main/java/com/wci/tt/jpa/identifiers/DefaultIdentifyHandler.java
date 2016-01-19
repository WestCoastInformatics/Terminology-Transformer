package com.wci.tt.jpa.identifiers;

import java.util.HashSet;
import java.util.Set;

import com.wci.tt.DataContext;
import com.wci.tt.IdentifyHandler;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.ScoredResultList;
import com.wci.tt.jpa.helpers.ScoredResultJpa;
import com.wci.tt.jpa.helpers.ScoredResultListJpa;
import com.wci.umls.server.helpers.PfscParameter;
import com.wci.umls.server.helpers.SearchResult;
import com.wci.umls.server.helpers.SearchResultList;
import com.wci.umls.server.helpers.meta.TerminologyList;
import com.wci.umls.server.jpa.helpers.PfscParameterJpa;
import com.wci.umls.server.jpa.helpers.meta.TerminologyListJpa;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.services.ContentService;

/**
 * The Class DefaultIdentifyHandler.
 */
public class DefaultIdentifyHandler implements IdentifyHandler {

  /* see superclass */
  @Override
  public ScoredResultList identify(String string, DataContext dataContext) throws Exception {
    ScoredResult qr = new ScoredResultJpa();
    qr.setTerminology(dataContext.getTerminology());
    qr.setVersion(dataContext.getVersion());
    qr.setQuality(1.0f);
    qr.setValue(string);
    return this.identify(qr, dataContext);
  }
  
  /* see superclass */
  @Override
  public ScoredResultList identify(ScoredResult qr, DataContext dataContext)
    throws Exception {

    Set<ScoredResult> qrSet = new HashSet<>();

    ContentService contentService = new ContentServiceJpa();

    TerminologyList terminologiesToSearch = new TerminologyListJpa();
    
    PfscParameter pfsc = new PfscParameterJpa();
    pfsc.setActiveOnly(true);
    pfsc.setStartIndex(0);
    pfsc.setMaxResults(200);

    // if context specifies terminology, find/verify/use that terminology
    if (dataContext.getTerminology() != null
        && dataContext.getVersion() != null) {
      terminologiesToSearch.addObject(contentService.getTerminology(
          dataContext.getTerminology(), dataContext.getVersion()));
    }

    // otherwise search all terminologies
    else {
      terminologiesToSearch = contentService.getTerminologies();
    }
    
    System.out.println("Searching # of terminologies: " + terminologiesToSearch.getCount());

    // cycle over selected terminologies and perform query
    for (Terminology terminology : terminologiesToSearch.getObjects()) {
      SearchResultList results =
          contentService.findConceptsForQuery(terminology.getTerminology(),
              terminology.getVersion(), "$", qr.getValue(), pfsc);

      for (SearchResult sr : results.getObjects()) {
        ScoredResult qualityResult = new ScoredResultJpa(sr);
        qualityResult.setQuality(1.0f * qr.getQuality());
        qrSet.add(qualityResult);
      }

    }
    
    ScoredResultList results = new ScoredResultListJpa();
    for (ScoredResult result : qrSet) {
      results.addObject(result);
    }
    results.setTotalCount(qrSet.size());

    return results;
  }

}
