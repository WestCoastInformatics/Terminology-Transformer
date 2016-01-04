/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import com.wci.tt.Refset;
import com.wci.tt.Translation;
import com.wci.tt.helpers.Configurable;
import com.wci.tt.rf2.Concept;
import com.wci.tt.rf2.ConceptRefsetMember;
import com.wci.tt.rf2.DescriptionType;
import com.wci.tt.rf2.RefsetDescriptorRefsetMember;

/**
 * Generically represents a listener for workflow actions.
 */
public interface WorkflowListener extends Configurable {

  /**
   * Represents change actions on components.
   */
  public enum Action {

    /** The add. */
    ADD,
    /** The remove. */
    REMOVE,
    /** The update. */
    UPDATE
  }

  /**
   * Notification of transaction starting.
   *
   * @throws Exception the exception
   */
  public void beginTransaction() throws Exception;

  /**
   * Notification pre-commit.
   *
   * @throws Exception the exception
   */
  public void preCommit() throws Exception;

  /**
   * Notification post-commit.
   *
   * @throws Exception the exception
   */
  public void postCommit() throws Exception;

  /**
   * Notification of a cancelled operation.
   */
  public void cancel();

  /**
   * Refset changed.
   *
   * @param refset the refset
   * @param action the action
   * @throws Exception the exception
   */
  public void refsetChanged(Refset refset, Action action) throws Exception;

  /**
   * Translation changed.
   *
   * @param translation the translation
   * @param action the action
   * @throws Exception the exception
   */
  public void translationChanged(Translation translation, Action action)
    throws Exception;

  /**
   * Refset descriptor ref set member changed.
   *
   * @param member the member
   * @param action the action
   * @throws Exception the exception
   */
  public void refsetDescriptorRefsetMemberChanged(
    RefsetDescriptorRefsetMember member, Action action) throws Exception;

  /**
   * Description type ref set member changed.
   *
   * @param member the member
   * @param action the action
   * @throws Exception the exception
   */
  public void descriptionTypeRefsetMemberChanged(
    DescriptionType member, Action action) throws Exception;

  /**
   * Concept changed.
   *
   * @param concept the concept
   * @param action the action
   * @throws Exception the exception
   */
  public void conceptChanged(Concept concept, Action action) throws Exception;

  /**
   * Member changed.
   *
   * @param member the member
   * @param action the action
   * @throws Exception the exception
   */
  public void memberChanged(ConceptRefsetMember member, Action action)
    throws Exception;
}
