/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import com.wci.tt.Refset;
import com.wci.tt.Translation;
import com.wci.tt.User;
import com.wci.tt.UserRole;
import com.wci.tt.ValidationResult;
import com.wci.tt.helpers.ConceptList;
import com.wci.tt.helpers.Configurable;
import com.wci.tt.helpers.PfsParameter;
import com.wci.tt.helpers.RefsetList;
import com.wci.tt.rf2.Concept;
import com.wci.tt.services.WorkflowService;
import com.wci.tt.workflow.TrackingRecord;
import com.wci.tt.workflow.WorkflowAction;

/**
 * Generically represents a handler for performing workflow actions.
 */
public interface WorkflowActionHandler extends Configurable {

  /**
   * Validate workflow action.
   *
   * @param refset the refset
   * @param user the user
   * @param projectRole the project role
   * @param action the action
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateWorkflowAction(Refset refset, User user,
    UserRole projectRole, WorkflowAction action, WorkflowService service)
    throws Exception;

  /**
   * Find available editing work. Something like dual independent review would
   * force the workflow action handler to implement this differently.
   *
   * @param translation the translation
   * @param user the user
   * @param pfs the pfs
   * @param service the service
   * @return the concept list
   * @throws Exception the exception
   */
  public ConceptList findAvailableEditingConcepts(Translation translation,
    User user, PfsParameter pfs, WorkflowService service) throws Exception;

  /**
   * Find available review work.
   *
   * @param translation the translation
   * @param user the user
   * @param pfs the pfs
   * @param service the service
   * @return the concept list
   * @throws Exception the exception
   */
  public ConceptList findAvailableReviewConcepts(Translation translation,
    User user, PfsParameter pfs, WorkflowService service) throws Exception;

  /**
   * Find available editing refsets.
   *
   * @param projectId the project id
   * @param user the user
   * @param pfs the pfs
   * @param service the service
   * @return the refset list
   * @throws Exception the exception
   */
  public RefsetList findAvailableEditingRefsets(Long projectId, User user,
    PfsParameter pfs, WorkflowService service) throws Exception;

  /**
   * Find available review refsets.
   *
   * @param projectId the project id
   * @param user the user
   * @param pfs the pfs
   * @param service the service
   * @return the refset list
   * @throws Exception the exception
   */
  public RefsetList findAvailableReviewRefsets(Long projectId, User user,
    PfsParameter pfs, WorkflowService service) throws Exception;

  /**
   * Validate workflow action.
   *
   * @param translation the translation
   * @param user the user
   * @param projectRole the project role
   * @param action the action
   * @param concept the concept
   * @param service the service
   * @return the validation result
   * @throws Exception the exception
   */
  public ValidationResult validateWorkflowAction(Translation translation,
    User user, UserRole projectRole, WorkflowAction action, Concept concept,
    WorkflowService service) throws Exception;

  /**
   * Perform workflow action.
   *
   * @param refset the refset
   * @param user the user
   * @param projectRole the project role
   * @param action the action
   * @param service the service
   * @return the tracking record
   * @throws Exception the exception
   */
  public TrackingRecord performWorkflowAction(Refset refset, User user,
    UserRole projectRole, WorkflowAction action, WorkflowService service)
    throws Exception;

  /**
   * Perform workflow action.
   *
   * @param translation the translation
   * @param user the user
   * @param projectRole the project role
   * @param action the action
   * @param concept the concept
   * @param service the service
   * @return the tracking record
   * @throws Exception the exception
   */
  public TrackingRecord performWorkflowAction(Translation translation,
    User user, UserRole projectRole, WorkflowAction action, Concept concept,
    WorkflowService service) throws Exception;

}
