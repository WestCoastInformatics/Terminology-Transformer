/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.DataContextType;

/**
 * JPA enabled implementation of {@link DataContextJpa}.
 */
@Entity
@Table(name = "data_context")
@Indexed
@XmlRootElement(name = "dataContext")
public class DataContextJpa implements DataContext {

  /** The id. */
  @TableGenerator(name = "EntityIdGen", table = "table_generator", pkColumnValue = "Entity")
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "EntityIdGen")
  Long id;

  /** The terminology. */
  String terminology;

  /** The version. */
  String version;

  /** The data context type. */
  DataContextType type;

  /** The customer. */
  String customer;

  /** The semantic type. */
  String semanticType;

  /** The specialty. */
  String specialty;

  /**
   * Instantiates an empty {@link DataContextJpa}.
   */
  public DataContextJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link DataContextJpa} from the specified parameters.
   *
   * @param context the data context
   */
  public DataContextJpa(DataContext context) {
    super();
    this.terminology = context.getTerminology();
    this.version = context.getVersion();
    this.type = context.getType();
    this.customer = context.getCustomer();
    this.semanticType = context.getSemanticType();
    this.specialty = context.getSpecialty();
  }

  /* see superclass */
  @Override
  public Long getId() {
    return id;
  }

  /* see superclass */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /* see superclass */
  @Override
  public String getTerminology() {
    return this.terminology;
  }

  /* see superclass */
  @Override
  public void setTerminology(String terminology) {
    this.terminology = terminology;

  }

  /* see superclass */
  @Override
  public String getVersion() {
    return this.version;
  }

  /* see superclass */
  @Override
  public void setVersion(String version) {
    this.version = version;
  }

  /* see superclass */
  @Override
  public DataContextType getType() {
    return type;
  }

  /* see superclass */
  @Override
  public void setType(DataContextType type) {
    this.type = type;
  }

  /* see superclass */
  @Override
  public String getCustomer() {
    return customer;
  }

  /* see superclass */
  @Override
  public void setCustomer(String customer) {
    this.customer = customer;
  }

  /* see superclass */
  @Override
  public String getSemanticType() {
    return semanticType;
  }

  /* see superclass */
  @Override
  public void setSemanticType(String semanticType) {
    this.semanticType = semanticType;
  }

  /* see superclass */
  @Override
  public String getSpecialty() {
    return specialty;
  }

  /* see superclass */
  @Override
  public void setSpecialty(String specialty) {
    this.specialty = specialty;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((terminology == null) ? 0 : terminology.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((customer == null) ? 0 : customer.hashCode());
    result =
        prime * result + ((semanticType == null) ? 0 : semanticType.hashCode());
    result = prime * result + ((specialty == null) ? 0 : specialty.hashCode());
    return result;
  }

  /* see superclass */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DataContextJpa other = (DataContextJpa) obj;
    if (terminology == null) {
      if (other.terminology != null)
        return false;
    } else if (!terminology.equals(other.terminology))
      return false;
    if (version == null) {
      if (other.version != null)
        return false;
    } else if (!version.equals(other.version))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (customer == null) {
      if (other.customer != null)
        return false;
    } else if (!customer.equals(other.customer))
      return false;
    if (semanticType == null) {
      if (other.semanticType != null)
        return false;
    } else if (!semanticType.equals(other.semanticType))
      return false;
    if (specialty == null) {
      if (other.specialty != null)
        return false;
    } else if (!specialty.equals(other.specialty))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "DataContextJpa [id=" + id + ", terminology=" + terminology
        + ", version=" + version + ", type=" + type.toString() + ", customer="
        + customer + ", semanticType=" + semanticType + ", specialty="
        + specialty + "]";
  }
}
