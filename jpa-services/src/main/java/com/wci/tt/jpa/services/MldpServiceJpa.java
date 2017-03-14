/*

 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.helpers.ScoredDataContextTupleList;
import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.jpa.DataContextJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleJpa;
import com.wci.tt.jpa.helpers.ScoredDataContextTupleListJpa;
import com.wci.tt.jpa.infomodels.MedicationModel;
import com.wci.tt.services.CoordinatorService;
import com.wci.tt.services.MldpService;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.model.workflow.WorkflowStatus;

/**
 * JPA and JAXB-enabled implementation of {@link CoordinatorService}.
 */
public class MldpServiceJpa extends CoordinatorServiceJpa
    implements MldpService {

  private final static Pattern typePattern =
      Pattern.compile("\\\"type\\\"\\s*:\\s*\"([^,]*)\",");

  public MldpServiceJpa() throws Exception {
    super();

  }

  @Override
  public ScoredDataContextTupleList processTerm(TypeKeyValue term)
    throws Exception {
    Logger.getLogger(getClass()).info("process term " + term);

    System.setProperty("mldpdebug", "true");

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

    final ScoredResult scoredResult = results.get(0);
    updateFromResult(term, scoredResult);
    updateHasLastModified(term);

    for (final ScoredResult result : results) {
      final ScoredDataContextTuple tuple = new ScoredDataContextTupleJpa();
      tuple.setData(result.getValue());
      tuple.setScore(result.getScore());
      tuple.setDataContext(outputContext);
      tuples.getObjects().add(tuple);

    }
    return tuples;
  }

  @Override
  public void processTerms(List<TypeKeyValue> terms) throws Exception {

    System.out.println("Processing " + terms.size() + " terms");
    System.setProperty("mldpdebug", "false");
    if (terms.size() > 0) {

      // input context: NAME
      final DataContext inputContext = new DataContextJpa();
      inputContext.setType(DataContextType.NAME);

      final String terminology = terms.get(0).getType().replace("-TERM", "");
      final String version =
          getTerminologyLatestVersion(terminology).getVersion();
      inputContext.setTerminology(terminology);
      inputContext.setVersion(version);
      inputContext.getParameters().put("cacheMode", "true");

      // output context: MEDICATION_MODEL
      final DataContext outputContext = new DataContextJpa();
      outputContext.setType(DataContextType.INFO_MODEL);
      outputContext.setInfoModelClass(MedicationModel.class.getName());

      outputContext.setTerminology(terminology);
      outputContext.setVersion(version);

      final Long lastCommitTime = System.currentTimeMillis();
      try {
        setTransactionPerOperation(false);
        beginTransaction();

        int i = 0;

        for (final TypeKeyValue term : terms) {
          // skip all terms with user-marked hold
          if (WorkflowStatus.EDITING_IN_PROGRESS
              .equals(term.getWorkflowStatus())) {
            continue;
          }
          final List<ScoredResult> results =
              process(term.getKey(), inputContext, outputContext);
          if (results.size() == 0) {
            throw new Exception("No results returned for " + term);
          }
          final ScoredResult scoredResult = results.get(0);
          updateFromResult(term, scoredResult);

          // term.setValue(model.getType());
          updateHasLastModified(term);

          logAndCommit(++i, 1000, 1000);

        }
        commit();
      } catch (Exception e) {
        rollback();
        throw e;
      }
    }

  }

  private void updateFromResult(TypeKeyValue term, ScoredResult scoredResult) {

    // TODO Use getModel once unit tests settled
    // final MedicationOutputModel model =
    // ConfigUtility.getGraphForJson(result.getValue(),
    // MedicationOutputModel.class);

    final Matcher matcher = typePattern.matcher(scoredResult.getValue());
    if (matcher.find()) {
      term.setValue(matcher.group(1));
    }

    // set the workflow
    // TODO This seriously needs to reworked, score mismatch potential very high
    // Incorporate into model later, perhaps
    if (scoredResult.getScore() == 0.0f) {
      // represents incomplete coverage
      term.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
    } else if (scoredResult.getScore() == 1.0f) {
      // represents complete coverage
      term.setWorkflowStatus(WorkflowStatus.PUBLISHED);
    } else if (scoredResult.getScore() == 0.5f) {
      // represents excluded terms
      term.setWorkflowStatus(WorkflowStatus.REVIEW_DONE);

    } else if (scoredResult.getScore() == 0.70f) {
      // represents ingr/str mismatch i.e. concentrations
      term.setWorkflowStatus(WorkflowStatus.REVIEW_NEW);
    } else if (scoredResult.getScore() == 0.75f) {
      // represents ingr/str mismatch i.e. concentrations
      term.setWorkflowStatus(WorkflowStatus.REVIEW_IN_PROGRESS);
    } else {
      // represents errors
      term.setWorkflowStatus(WorkflowStatus.DEMOTION);
    }
  }

}
