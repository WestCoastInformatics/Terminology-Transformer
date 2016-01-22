/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.HashSet;
import java.util.Set;

import com.wci.tt.helpers.DataContextType;

/**
 * The Class AbstractProviderHandler.
 * 
 * To be extended by handlers that support specific contexts.
 */
public abstract class AbstractContextHandler {

  /** The provider type. */
  protected DataContextType providerType = DataContextType.UNKNOWN;

  /** The provider customers. */
  protected Set<String> providerCustomers = new HashSet<>();

  /** The provider semantic types. */
  protected Set<String> providerSemanticTypes = new HashSet<>();

  /** The provider specialties. */
  protected Set<String> providerSpecialties = new HashSet<>();

  /** The provider terminologies. */
  protected Set<String> providerTerminologies = new HashSet<>();

  /** The provider versions. */
  protected Set<String> providerVersions = new HashSet<>();

  /** The information model name. */
  protected String informationModelName = null;

  /**
   * Returns the provider type.
   *
   * @return the provider type
   */
  public final DataContextType getProviderType() {
    return providerType;
  }

  /**
   * Sets the provider type.
   *
   * @param providerType the provider type
   */
  public final void setProviderType(DataContextType providerType) {
    this.providerType = providerType;
  }

  /**
   * Returns the provider customers.
   *
   * @return the provider customers
   */
  public final Set<String> getProviderCustomers() {
    return providerCustomers;
  }

  /**
   * Sets the provider customers.
   *
   * @param providerCustomers the provider customers
   */
  public final void setProviderCustomers(Set<String> providerCustomers) {
    this.providerCustomers = providerCustomers;
  }

  /**
   * Returns the provider semantic types.
   *
   * @return the provider semantic types
   */
  public final Set<String> getProviderSemanticTypes() {
    return providerSemanticTypes;
  }

  /**
   * Sets the provider semantic types.
   *
   * @param providerSemanticTypes the provider semantic types
   */
  public final void setProviderSemanticTypes(Set<String> providerSemanticTypes) {
    this.providerSemanticTypes = providerSemanticTypes;
  }

  /**
   * Returns the provider specialties.
   *
   * @return the provider specialties
   */
  public final Set<String> getProviderSpecialties() {
    return providerSpecialties;
  }

  /**
   * Sets the provider specialties.
   *
   * @param providerSpecialties the provider specialties
   */
  public final void setProviderSpecialties(Set<String> providerSpecialties) {
    this.providerSpecialties = providerSpecialties;
  }

  /**
   * Returns the provider terminologies.
   *
   * @return the provider terminologies
   */
  public final Set<String> getProviderTerminologies() {
    return providerTerminologies;
  }

  /**
   * Sets the provider terminologies.
   *
   * @param providerTerminologies the provider terminologies
   */
  public final void setProviderTerminologies(Set<String> providerTerminologies) {
    this.providerTerminologies = providerTerminologies;
  }

  /**
   * Returns the provider versions.
   *
   * @return the provider versions
   */
  public final Set<String> getProviderVersions() {
    return providerVersions;
  }

  /**
   * Sets the provider versions.
   *
   * @param providerVersions the provider versions
   */
  public final void setProviderVersions(Set<String> providerVersions) {
    this.providerVersions = providerVersions;
  }

  /**
   * Returns the information model name.
   *
   * @return the information model name
   */
  public final String getInformationModelName() {
    return informationModelName;
  }

  /**
   * Sets the information model name.
   *
   * @param informationModelName the information model name
   */
  public final void setInformationModelName(String informationModelName) {
    this.informationModelName = informationModelName;
  }
}
