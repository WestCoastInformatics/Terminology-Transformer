/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import com.wci.tt.helpers.Configurable;
import com.wci.tt.model.content.Atom;
import com.wci.tt.model.content.Attribute;
import com.wci.tt.model.content.Code;
import com.wci.tt.model.content.ComponentHasAttributes;
import com.wci.tt.model.content.Concept;
import com.wci.tt.model.content.Definition;
import com.wci.tt.model.content.Descriptor;
import com.wci.tt.model.content.LexicalClass;
import com.wci.tt.model.content.Relationship;
import com.wci.tt.model.content.SemanticTypeComponent;
import com.wci.tt.model.content.StringClass;
import com.wci.tt.model.content.Subset;
import com.wci.tt.model.content.SubsetMember;

/**
 * Generically represents a validation check on a concept.
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
   * Classification started.
   *
   * @throws Exception the exception
   */
  public void classificationStarted() throws Exception;

  /**
   * Classification finished.
   *
   * @throws Exception the exception
   */
  public void classificationFinished() throws Exception;

  /**
   * Pre classification started.
   *
   * @throws Exception the exception
   */
  public void preClassificationStarted() throws Exception;

  /**
   * Pre classification finished.
   *
   * @throws Exception the exception
   */
  public void preClassificationFinished() throws Exception;

  /**
   * Notification of concept added.
   *
   * @param concept the concept
   * @param action the action
   * @throws Exception the exception
   */
  public void conceptChanged(Concept concept, Action action) throws Exception;

  /**
   * Descriptor of atom changed.
   *
   * @param descriptor the descriptor
   * @param action the action
   * @throws Exception the exception
   */
  public void descriptorChanged(Descriptor descriptor, Action action)
    throws Exception;

  /**
   * Code changed.
   *
   * @param code the code
   * @param action the action
   * @throws Exception the exception
   */
  public void codeChanged(Code code, Action action) throws Exception;

  /**
   * String class changed.
   *
   * @param stringClass the string class
   * @param action the action
   * @throws Exception the exception
   */
  public void stringClassChanged(StringClass stringClass, Action action)
    throws Exception;

  /**
   * Lexical class changed.
   *
   * @param lexicalClass the lexical class
   * @param action the action
   * @throws Exception the exception
   */
  public void lexicalClassChanged(LexicalClass lexicalClass, Action action)
    throws Exception;

  /**
   * Atom changed.
   *
   * @param atom the atom
   * @param action the action
   * @throws Exception the exception
   */
  public void atomChanged(Atom atom, Action action) throws Exception;

  /**
   * Attribute changed.
   *
   * @param attribute the attribute
   * @param action the action
   * @throws Exception the exception
   */
  public void attributeChanged(Attribute attribute, Action action)
    throws Exception;

  /**
   * Definition changed.
   *
   * @param definition the definition
   * @param action the action
   * @throws Exception the exception
   */
  public void definitionChanged(Definition definition, Action action)
    throws Exception;

  /**
   * Relationship changed.
   *
   * @param relationship the relationship
   * @param action the action
   * @throws Exception the exception
   */
  public void relationshipChanged(
    Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes> relationship,
    Action action) throws Exception;

  /**
   * Semantic type changed.
   *
   * @param sty the sty
   * @param action the action
   * @throws Exception the exception
   */
  public void semanticTypeChanged(SemanticTypeComponent sty, Action action)
    throws Exception;

  /**
   * Subset changed.
   *
   * @param subset the subset
   * @param action the action
   */
  public void subsetChanged(Subset subset, Action action);

  /**
   * Subset member changed.
   *
   * @param subsetMember the subset member
   * @param action the action
   */
  public void subsetMemberChanged(
    SubsetMember<? extends ComponentHasAttributes, ? extends Subset> subsetMember,
    Action action);

  /**
   * Metadata changed.
   */
  public void metadataChanged();

  /**
   * Notification of a cancelled operation.
   */
  public void cancel();
}
