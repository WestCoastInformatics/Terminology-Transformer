/*

 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import java.util.List;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleListJpa;
import com.wci.tt.jpa.infomodels.MedicationModel;
import com.wci.tt.jpa.infomodels.MedicationOutputModel;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.MldpService;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.model.workflow.WorkflowStatus;

/**
 * JPA and JAXB-enabled implementation of {@link CoordinatorService}.
 */
public class MldpServiceJpa extends CoordinatorServiceJpa
    implements MldpService {

  public MldpServiceJpa() throws Exception {
    super();

  }
  

  @Override
  public ScoredDataContextTupleList processTerm(TypeKeyValue term)
    throws Exception {

    // Translate tuples into JPA object
    final ScoredDataContextTupleList tuples =
        new ScoredDataContextTupleListJpa();

    final DataContext inputContext = new DataContextJpa();
    inputContext.setType(DataContextType.NAME);

    final String terminology = term.getType().replace("-TERM", "");
    final String version =
        getTerminologyLatestVersion(terminology).getVersion();
    inputContext.setTerminology(terminology);
    inputContext.setVersion(version);

    final DataContext outputContext = new DataContextJpa();
    outputContext.setType(DataContextType.INFO_MODEL);
    outputContext.setTerminology(terminology);
    outputContext.setVersion(version);

    final List<ScoredResult> results =
        process(term.getKey(), inputContext, outputContext);

    if (results.size() == 0) {
      throw new Exception("No results returned for " + term);
    }
    if (results.get(0).getScore() == 0.0f) {
      // represents incomplete coverage
      term.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);

    } else if (results.get(0).getScore() == 1.0f) {
      // represents complete coverage
      term.setWorkflowStatus(WorkflowStatus.PUBLISHED);
      updateHasLastModified(term);
    } else if (results.get(0).getScore() == 0.5f) {
      // represents excluded terms
      term.setWorkflowStatus(WorkflowStatus.REVIEW_DONE);

    } else {
      // represents errors
      term.setWorkflowStatus(WorkflowStatus.DEMOTION);
    }
    updateHasLastModified(term);

    for (final ScoredResult result : results) {
      final ScoredDataContextTuple tuple = new ScoredDataContextTupleJpa();
      tuple.setData(result.getModel().getModelValue());
      tuple.setScore(result.getScore());
      tuple.setDataContext(outputContext);
      tuples.getObjects().add(tuple);

    }
    return tuples;
  }

  @Override
  public void processTerms(List<TypeKeyValue> terms) throws Exception {

    System.out.println("Processing " + terms.size() + " terms");

    if (terms.size() > 0) {

      // input context: NAME
      final DataContext inputContext = new DataContextJpa();
      inputContext.setType(DataContextType.NAME);

      final String terminology = terms.get(0).getType().replace("-TERM", "");
      final String version =
          getTerminologyLatestVersion(terminology).getVersion();
      inputContext.setTerminology(terminology);
      inputContext.setVersion(version);

      // output context: MEDICATION_MODEL
      final DataContext outputContext = new DataContextJpa();
      outputContext.setType(DataContextType.INFO_MODEL);
      outputContext.setInfoModelClass(MedicationModel.class.getName());\

      outputContext.setTerminology(terminology);
      outputContext.setVersion(version);
      
      final Long lastCommitTime = System.currentTimeMillis();
      try {
        setTransactionPerOperation(false);
        beginTransaction();

        int i = 0;

        for (final TypeKeyValue term : terms) {
          final List<ScoredResult> results =
              process(term.getKey(), inputContext, outputContext);
          if (results.size() == 0) {
            throw new Exception("No results returned for " + term);
          }
          final ScoredResult result = results.get(0);
          
          if (result.getScore() == 0.0f) {
            // represents incomplete coverage
            term.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);

          } else if (result.getScore() == 1.0f) {
            // represents complete coverage
            term.setWorkflowStatus(WorkflowStatus.PUBLISHED);
            updateHasLastModified(term);
          } else if (result.getScore() == 0.5f) {
            // represents excluded terms
            term.setWorkflowStatus(WorkflowStatus.REVIEW_DONE);

          } else {
            // represents errors
            term.setWorkflowStatus(WorkflowStatus.DEMOTION);
          }
          
          term.setValue(((MedicationOutputModel) result.getModel()).getType()); 
          updateHasLastModified(term);
      
          logAndCommit(++i, 200, 200);
//          if (Math.floorMod(i, 10) == 0) {
//            Logger.getLogger(getClass()).info(RootService.commitCt / ((System.currentTimeMillis() - lastCommitTime) / 1000));
//            lastCommitTime = System.currentTimeMillis();
//          }

        }
        commit();
      } catch (Exception e) {
        rollback();
        throw e;
      }
    }

  }

}
