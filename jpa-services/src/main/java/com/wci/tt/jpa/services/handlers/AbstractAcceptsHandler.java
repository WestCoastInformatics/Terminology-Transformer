/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.jpa.helpers.DataContextJpa;

/**
 * The Class AbstractContextHandler.
 * 
 * To be extended by handlers that support specific contexts.
 */
public abstract class AbstractAcceptsHandler {

  /**
   * The Enum FIELD.
   */
  private enum FIELD {

    /** The customer. */
    CUSTOMER,
    /** The semantic type. */
    SEMANTIC_TYPE,
    /** The specialty. */
    SPECIALTY,
    /** The terminology. */
    TERMINOLOGY,
    /** The version. */
    VERSION;
  }

  /** The input type. */
  /*
   * Supported Input Context values
   */
  protected DataContextType inputType = null;

  /** The input info model name. */
  protected String inputInfoModelName = null;

  /** The input customers. */
  protected Set<String> inputCustomers = new HashSet<>();

  /** The input semantic types. */
  protected Set<String> inputSemanticTypes = new HashSet<>();

  /** The input specialties. */
  protected Set<String> inputSpecialties = new HashSet<>();

  /** The input terminologies. */
  protected Set<String> inputTerminologies = new HashSet<>();

  /** The input versions. */
  protected Set<String> inputVersions = new HashSet<>();

  /** The output type. */
  /*
   * Supported Output Context values
   */
  protected DataContextType outputType = DataContextType.UNKNOWN;

  /** The output info model name. */
  protected String outputInfoModelName = null;

  /** The output customers. */
  protected Set<String> outputCustomers = new HashSet<>();

  /** The output semantic types. */
  protected Set<String> outputSemanticTypes = new HashSet<>();

  /** The output specialties. */
  protected Set<String> outputSpecialties = new HashSet<>();

  /** The output terminologies. */
  protected Set<String> outputTerminologies = new HashSet<>();

  /** The output versions. */
  protected Set<String> outputVersions = new HashSet<>();

  /**
   * Ensures input context is supported. If it is, returns all output contexts
   * supported.
   * 
   * Returns a list because may handle multiple output data contexts.
   *
   * @param inputContext the input context
   * @return the list
   * @throws Exception the exception
   */
  public List<DataContext> accepts(DataContext inputContext) throws Exception {
    if (!inputContextSupported(inputContext)) {
      // Input context is not supported by the AcceptHandler
      return new ArrayList<DataContext>();
    } else {
      // Input context is supported, so identify the available output contexts
      return (createSupportedContexts());
    }
  }

  /**
   * Created accepted contexts.
   *
   * @return the list
   */
  private List<DataContext> createSupportedContexts() {
    List<DataContext> acceptedContexts = new ArrayList<>();

    acceptedContexts =
        populateContexts(outputCustomers, acceptedContexts, FIELD.CUSTOMER);

    acceptedContexts =
        populateContexts(outputSemanticTypes, acceptedContexts,
            FIELD.SEMANTIC_TYPE);

    acceptedContexts =
        populateContexts(outputSpecialties, acceptedContexts, FIELD.SPECIALTY);

    acceptedContexts =
        populateContexts(outputTerminologies, acceptedContexts,
            FIELD.TERMINOLOGY);

    acceptedContexts =
        populateContexts(outputVersions, acceptedContexts, FIELD.VERSION);

    // Having created every context, populate them with the output type (if
    // defined)
    if (outputType != DataContextType.UNKNOWN) {
      for (DataContext c : acceptedContexts) {
        c.setType(outputType);

        if (outputType == DataContextType.INFO_MODEL) {
          c.setInfoModelName(outputInfoModelName);
        }
      }
    }

    return acceptedContexts;
  }

  /**
   * Populate context with specified FIELD.
   *
   * @param values the values
   * @param currentContexts the current contexts
   * @param fieldType the field type
   * @return the list
   */
  private List<DataContext> populateContexts(Set<String> values,
    List<DataContext> currentContexts, FIELD fieldType) {
    List<DataContext> newContexts = new ArrayList<>();

    // Handle values. Nothing to do if they are empty
    if (!values.isEmpty()) {
      // Can't presume currentContexts has been populated yet
      if (currentContexts.isEmpty()) {
        // Current contexts not yet populated, thus populate newContexts
        // directly
        for (String s : values) {
          DataContext c = new DataContextJpa();
          setField(c, s, fieldType);
          newContexts.add(c);
        }
      } else {
        // Create N*M new contexts where N = existing contexts and M = values
        for (DataContext oldContext : currentContexts) {
          for (String s : values) {
            DataContext c = new DataContextJpa(oldContext);
            setField(c, s, fieldType);
            newContexts.add(c);
          }
        }
      }
    }

    return newContexts;
  }

  /**
   * Sets the specified field in the context.
   *
   * @param c the c
   * @param s the s
   * @param fieldType the field type
   */
  private void setField(DataContext c, String s, FIELD fieldType) {
    if (fieldType == FIELD.CUSTOMER) {
      c.setCustomer(s);
    } else if (fieldType == FIELD.SEMANTIC_TYPE) {
      c.setSemanticType(s);
    } else if (fieldType == FIELD.SPECIALTY) {
      c.setSpecialty(s);
    } else if (fieldType == FIELD.TERMINOLOGY) {
      c.setTerminology(s);
    } else if (fieldType == FIELD.VERSION) {
      c.setVersion(s);
    }

  }

  /**
   * Initial context output check.
   *
   * @param context the queried context
   * @return true, if successful
   * @throws Exception the exception
   */
  private boolean inputContextSupported(DataContext context) throws Exception {
    // DataContextType checks
    if (context.getType() != DataContextType.UNKNOWN) {
      // First ensure that if context is of info model type, a name is specified
      if (context.getType() == DataContextType.INFO_MODEL
          && stringHasValue(context.getInfoModelName())) {
        throw new Exception(
            "If context is of type info model, the input context must specify an info model name");
      }

      // if input type is defined, then verify matches context's values
      if (inputType != DataContextType.UNKNOWN) {
        // Verify types match up
        if (inputType != context.getType()) {
          return false;
        }

        // If type is Info_MODEL, verify info model names match up
        if (inputType == DataContextType.INFO_MODEL) {
          if (!inputInfoModelName.equalsIgnoreCase(context.getInfoModelName())) {
            return false;
          }
        }
      }
    }

    if (!checkFieldSupported(context.getCustomer(), inputCustomers)
        || !checkFieldSupported(context.getCustomer(), inputCustomers)
        || !checkFieldSupported(context.getCustomer(), inputCustomers)
        || !checkFieldSupported(context.getCustomer(), inputCustomers)
        || !checkFieldSupported(context.getCustomer(), inputCustomers)) {
      return false;
    }

    return true;
  }

  /**
   * Check field supported.
   *
   * @param str the str
   * @param vals the vals
   * @return true, if successful
   */
  private boolean checkFieldSupported(String str, Set<String> vals) {
    if (stringHasValue(str) && !vals.isEmpty()) {
      if (!vals.contains(str)) {
        return false;
      }
    }

    return true;
  }

  /**
   * String has value.
   *
   * @param str the str
   * @return true, if successful
   */
  private boolean stringHasValue(String str) {
    return (str != null && !str.isEmpty());
  }
}