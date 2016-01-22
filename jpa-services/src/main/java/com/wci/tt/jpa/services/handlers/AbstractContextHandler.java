/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.HashSet;
import java.util.Set;

import com.wci.tt.helpers.DataContextType;

/**
 * The Class AbstractContextHandler.
 * 
 * To be extended by handlers that support specific contexts.
 */
public abstract class AbstractContextHandler {

  /** The supported type. */
  protected DataContextType supportedType = DataContextType.UNKNOWN;

  /** The supported customers. */
  protected Set<String> supportedCustomers = new HashSet<>();

  /** The supported semantic types. */
  protected Set<String> supportedSemanticTypes = new HashSet<>();

  /** The supported specialties. */
  protected Set<String> supportedSpecialties = new HashSet<>();

  /** The supported terminologies. */
  protected Set<String> supportedTerminologies = new HashSet<>();

  /** The supported versions. */
  protected Set<String> supportedVersions = new HashSet<>();

  /** The supported info model name. */
  protected String supportedInfoModelName = null;

  /**
   * Returns the supported type.
   *
   * @return the supported type
   */
  public final DataContextType getSupportedType() {
    return supportedType;
  }

  /**
   * Sets the supported type.
   *
   * @param supportedType the supported type
   */
  public final void setSupportedType(DataContextType supportedType) {
    this.supportedType = supportedType;
  }

  /**
   * Returns the supported customers.
   *
   * @return the supported customers
   */
  public final Set<String> getSupportedCustomers() {
    return supportedCustomers;
  }

  /**
   * Sets the supported customers.
   *
   * @param supportedCustomers the supported customers
   */
  public final void setSupportedCustomers(Set<String> supportedCustomers) {
    this.supportedCustomers = supportedCustomers;
  }

  /**
   * Returns the supported semantic types.
   *
   * @return the supported semantic types
   */
  public final Set<String> getSupportedSemanticTypes() {
    return supportedSemanticTypes;
  }

  /**
   * Sets the supported semantic types.
   *
   * @param supportedSemanticTypes the supported semantic types
   */
  public final void setSupportedSemanticTypes(Set<String> supportedSemanticTypes) {
    this.supportedSemanticTypes = supportedSemanticTypes;
  }

  /**
   * Returns the supported specialties.
   *
   * @return the supported specialties
   */
  public final Set<String> getSupportedSpecialties() {
    return supportedSpecialties;
  }

  /**
   * Sets the supported specialties.
   *
   * @param supportedSpecialties the supported specialties
   */
  public final void setSupportedSpecialties(Set<String> supportedSpecialties) {
    this.supportedSpecialties = supportedSpecialties;
  }

  /**
   * Returns the supported terminologies.
   *
   * @return the supported terminologies
   */
  public final Set<String> getSupportedTerminologies() {
    return supportedTerminologies;
  }

  /**
   * Sets the supported terminologies.
   *
   * @param supportedTerminologies the supported terminologies
   */
  public final void setSupportedTerminologies(Set<String> supportedTerminologies) {
    this.supportedTerminologies = supportedTerminologies;
  }

  /**
   * Returns the supported versions.
   *
   * @return the supported versions
   */
  public final Set<String> getSupportedVersions() {
    return supportedVersions;
  }

  /**
   * Sets the supported versions.
   *
   * @param supportedVersions the supported versions
   */
  public final void setSupportedVersions(Set<String> supportedVersions) {
    this.supportedVersions = supportedVersions;
  }

  /**
   * Returns the supported info model name.
   *
   * @return the supported info model name
   */
  public final String getSupportedInfoModelName() {
    return supportedInfoModelName;
  }

  /**
   * Sets the supported info model name.
   *
   * @param supportedInfoModelName the supported info model name
   */
  public final void setSupportedInfoModelName(String supportedInfoModelName) {
    this.supportedInfoModelName = supportedInfoModelName;
  }
}