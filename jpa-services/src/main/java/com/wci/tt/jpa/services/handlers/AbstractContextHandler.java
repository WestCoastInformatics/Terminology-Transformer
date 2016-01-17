/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.Set;

import com.wci.tt.helpers.DataContextType;

/**
 * The Class AbstractProviderHandler.
 * 
 * To be extended by handlers that support specific contexts.
 */
public abstract class AbstractContextHandler {

  /** The provider type. */
  private Set<DataContextType> providerType = null;

  /** The provider customer. */
  private Set<String> providerCustomer = null;

  /** The provider semantic type. */
  private Set<String> providerSemanticType = null;

  /** The provider specialty. */
  private Set<String> providerSpecialty = null;

  /** The provider terminology. */
  private Set<String> providerTerminology = null;

  /** The provider version. */
  private Set<String> providerVersion = null;

  /**
   * Returns the provider type.
   *
   * @return the provider type
   */
  public final Set<DataContextType> getProviderType() {
    return providerType;
  }

  /**
   * Sets the provider type.
   *
   * @param providerType the provider type
   */
  public final void setProviderType(Set<DataContextType> providerType) {
    this.providerType = providerType;
  }

  /**
   * Returns the provider customer.
   *
   * @return the provider customer
   */
  public final Set<String> getProviderCustomer() {
    return providerCustomer;
  }

  /**
   * Sets the provider customer.
   *
   * @param providerCustomer the provider customer
   */
  public final void setProviderCustomer(Set<String> providerCustomer) {
    this.providerCustomer = providerCustomer;
  }

  /**
   * Returns the provider semantic type.
   *
   * @return the provider semantic type
   */
  public final Set<String> getProviderSemanticType() {
    return providerSemanticType;
  }

  /**
   * Sets the provider semantic type.
   *
   * @param providerSemanticType the provider semantic type
   */
  public final void setProviderSemanticType(Set<String> providerSemanticType) {
    this.providerSemanticType = providerSemanticType;
  }

  /**
   * Returns the provider specialty.
   *
   * @return the provider specialty
   */
  public final Set<String> getProviderSpecialty() {
    return providerSpecialty;
  }

  /**
   * Sets the provider specialty.
   *
   * @param providerSpecialty the provider specialty
   */
  public final void setProviderSpecialty(Set<String> providerSpecialty) {
    this.providerSpecialty = providerSpecialty;
  }

  /**
   * Returns the provider terminology.
   *
   * @return the provider terminology
   */
  public final Set<String> getProviderTerminology() {
    return providerTerminology;
  }

  /**
   * Sets the provider terminology.
   *
   * @param providerTerminology the provider terminology
   */
  public final void setProviderTerminology(Set<String> providerTerminology) {
    this.providerTerminology = providerTerminology;
  }

  /**
   * Returns the provider version.
   *
   * @return the provider version
   */
  public final Set<String> getProviderVersion() {
    return providerVersion;
  }

  /**
   * Sets the provider version.
   *
   * @param providerVersion the provider version
   */
  public final void setProviderVersion(Set<String> providerVersion) {
    this.providerVersion = providerVersion;
  }
}
