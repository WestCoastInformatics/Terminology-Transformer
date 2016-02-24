/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.wci.tt.helpers.TypeKeyValue;

/**
 * JPA enabled scored implementation of {@link TypeKeyValue}.
 */
@Entity
@Table(name = "type_key_values")
@Indexed
public class TypeKeyValueJpa implements TypeKeyValue, Comparable<TypeKeyValue> {

  /** The id. */
  @TableGenerator(name = "EntityIdGenTransformer", table = "table_generator_transformer", pkColumnValue = "Entity")
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "EntityIdGenTransformer")
  private Long id;

  /** The type. */
  @Column(nullable = false)
  private String type;

  /** The key. */
  @Column(name = "key_field", nullable = true)
  private String key;

  /** The value. */
  @Column(nullable = true, length = 4000)
  private String value;

  /**
   * Instantiates an empty {@link TypeKeyValueJpa}.
   */
  public TypeKeyValueJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link TypeKeyValueJpa} from the specified parameters.
   *
   * @param typeKeyValue the type key value
   */
  public TypeKeyValueJpa(TypeKeyValue typeKeyValue) {
    this.id = typeKeyValue.getId();
    this.type = typeKeyValue.getType();
    this.key = typeKeyValue.getKey();
    this.value = typeKeyValue.getValue();
  }

  /**
   * Instantiates a {@link TypeKeyValueJpa} from the specified parameters.
   *
   * @param type the type
   * @param key the key
   * @param value the value
   */
  public TypeKeyValueJpa(String type, String key, String value) {
    this.type = type;
    this.value = value;
    this.key = key;
  }

  /**
   * Returns the id.
   *
   * @return the id
   */
  /* see superclass */
  @Override
  public Long getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the id
   */
  /* see superclass */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /* see superclass */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public String getType() {
    return type;
  }

  /* see superclass */
  @Override
  public void setType(String type) {
    this.type = type;
  }

  /* see superclass */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public String getKey() {
    return key;
  }

  /* see superclass */
  @Override
  public void setKey(String key) {
    this.key = key;
  }

  /* see superclass */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public String getValue() {
    return value;
  }

  /* see superclass */
  @Override
  public void setValue(String value) {
    this.value = value;
  }

  /* see superclass */
  @Override
  public int compareTo(TypeKeyValue tkv) {
    int i = type.compareTo(tkv.getType());
    if (i == 0) {
      i = key.compareTo(tkv.getKey());
      if (i == 0) {
        i = value.compareTo(tkv.getValue());
      }
    }
    return i;
  }

  /**
   * Hash code.
   *
   * @return the int
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  /**
   * Equals.
   *
   * @param obj the obj
   * @return true, if successful
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TypeKeyValueJpa other = (TypeKeyValueJpa) obj;
    if (key == null) {
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  /**
   * To string.
   *
   * @return the string
   */
  @Override
  public String toString() {
    return "TypeKeyValueJpa [id=" + id + ", type=" + type + ", key=" + key
        + ", value=" + value + "]";
  }
}
