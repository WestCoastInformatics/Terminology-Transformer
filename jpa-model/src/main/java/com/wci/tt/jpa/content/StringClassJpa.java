/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.content;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import com.wci.tt.model.content.StringClass;

/**
 * JPA-enabled implementation of {@link StringClass}.
 */
@Entity
@Table(name = "string_classes", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "version", "id"
}))
@Audited
@XmlRootElement(name = "stringClass")
@Indexed
public class StringClassJpa extends AbstractAtomClass implements StringClass {

  /** The labels. */
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(nullable = true)
  List<String> labels;

  /**
   * Instantiates an empty {@link StringClassJpa}.
   */
  public StringClassJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link StringClassJpa} from the specified parameters.
   *
   * @param stringClass the string class
   * @param deepCopy the deep copy
   */
  public StringClassJpa(StringClass stringClass, boolean deepCopy) {
    super(stringClass, deepCopy);
    if (stringClass.getLabels() != null) {
      labels = new ArrayList<>(stringClass.getLabels());
    }
  }

  /* see superclass */
  @Override
  public List<String> getLabels() {
    return labels;
  }

  /* see superclass */
  @Override
  public void setLabels(List<String> labels) {
    this.labels = labels;

  }

  /* see superclass */
  @Override
  public void addLabel(String label) {
    if (labels == null) {
      labels = new ArrayList<String>();
    }
    labels.add(label);
  }

  /* see superclass */
  @Override
  public void removeLabel(String label) {
    if (labels == null) {
      labels = new ArrayList<String>();
    }
    labels.remove(label);

  }
}
