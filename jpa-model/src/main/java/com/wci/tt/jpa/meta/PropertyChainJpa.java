/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.meta;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

import com.wci.tt.model.meta.AdditionalRelationshipType;
import com.wci.tt.model.meta.PropertyChain;

/**
 * JPA-enabled implementation of {@link PropertyChain}.
 */
@Entity
@Table(name = "property_chains", uniqueConstraints = @UniqueConstraint(columnNames = {
    "abbreviation", "terminology"
}))
@Audited
@XmlRootElement(name = "propertyChain")
public class PropertyChainJpa extends AbstractAbbreviation implements
    PropertyChain {

  /** The chain. */
  @ManyToMany(targetEntity = AdditionalRelationshipTypeJpa.class)
  private List<AdditionalRelationshipType> chain;

  /** The result. */
  @ManyToOne(targetEntity = AdditionalRelationshipTypeJpa.class, optional = false)
  private AdditionalRelationshipType result;

  /**
   * Instantiates an empty {@link PropertyChainJpa}.
   */
  public PropertyChainJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link PropertyChainJpa} from the specified parameters.
   *
   * @param chain the chain
   */
  public PropertyChainJpa(PropertyChain chain) {
    super(chain);
    this.chain = chain.getChain();
    result = chain.getResult();
  }

  /* see superclass */
  @Override
  public List<AdditionalRelationshipType> getChain() {
    if (chain == null) {
      chain = new ArrayList<>();
    }
    return chain;
  }

  /* see superclass */
  @Override
  public void setChain(List<AdditionalRelationshipType> chain) {
    this.chain = chain;
  }

  /* see superclass */
  @Override
  public AdditionalRelationshipType getResult() {
    return result;
  }

  /* see superclass */
  @Override
  public void setResult(AdditionalRelationshipType result) {
    this.result = result;
  }

}
