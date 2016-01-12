package com.wci.tt.jpa.identifiers;

import java.util.HashSet;
import java.util.Set;

import com.wci.tt.DataContext;
import com.wci.tt.IdentifyHandler;
import com.wci.tt.helpers.PfscParameter;
import com.wci.tt.helpers.QualityResult;
import com.wci.tt.helpers.QualityResultList;
import com.wci.tt.helpers.SearchResult;
import com.wci.tt.helpers.SearchResultList;
import com.wci.tt.helpers.TerminologyList;
import com.wci.tt.jpa.helpers.PfscParameterJpa;
import com.wci.tt.jpa.helpers.QualityResultJpa;
import com.wci.tt.jpa.helpers.QualityResultListJpa;
import com.wci.tt.jpa.helpers.meta.TerminologyListJpa;
import com.wci.tt.jpa.services.ContentServiceJpa;
import com.wci.tt.model.meta.Terminology;
import com.wci.tt.services.ContentService;

/**
 * The Class DefaultIdentifyHandler.
 */
public class DefaultIdentifyHandler implements IdentifyHandler {

  /* see superclass */
  @Override
  public QualityResultList identify(String string, DataContext dataContext) throws Exception {
    QualityResult qr = new QualityResultJpa();
    qr.setTerminology(dataContext.getTerminology());
    qr.setVersion(dataContext.getVersion());
    qr.setQuality(1.0f);
    qr.setValue(string);
    return this.identify(qr, dataContext);
  }
  
  /* see superclass */
  @Override
  public QualityResultList identify(QualityResult qr, DataContext dataContext)
    throws Exception {

    Set<QualityResult> qrSet = new HashSet<>();

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
        QualityResult qualityResult = new QualityResultJpa(sr);
        qualityResult.setQuality(1.0f * qr.getQuality());
        qrSet.add(qualityResult);
      }

    }
    
    QualityResultList results = new QualityResultListJpa();
    for (QualityResult result : qrSet) {
      results.addObject(result);
    }
    results.setTotalCount(qrSet.size());

    return results;
  }

}
