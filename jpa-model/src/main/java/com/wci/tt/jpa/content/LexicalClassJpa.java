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
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.wci.tt.model.content.LexicalClass;

/**
 * JPA-enabled implementation of {@link LexicalClass}.
 */
@Entity
@Table(name = "lexical_classes", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "version", "id"
}))
@Audited
@XmlRootElement(name = "lexicalClass")
@Indexed
public class LexicalClassJpa extends AbstractAtomClass implements LexicalClass {

  /** The normalized string. */
  @Column(nullable = true, length = 4000)
  private String normalizedName;

  /** The label sets. */
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(nullable = true)
  List<String> labels;

  /**
   * Instantiates an empty {@link LexicalClassJpa}.
   */
  public LexicalClassJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link LexicalClassJpa} from the specified parameters.
   *
   * @param lexicalClass the lexical class
   * @param deepCopy the deep copy
   */
  public LexicalClassJpa(LexicalClass lexicalClass, boolean deepCopy) {
    super(lexicalClass, deepCopy);
    normalizedName = lexicalClass.getNormalizedName();
    if (lexicalClass.getLabels() != null) {
      labels = new ArrayList<>(lexicalClass.getLabels());
    }
  }

  /**
   * Returns the normalized string.
   *
   * @return the normalized string
   */
  @Override
  @Fields({
      @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO),
      @Field(name = "normalizedNameSort", index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  })
  @Analyzer(definition = "noStopWord")
  public String getNormalizedName() {
    return normalizedName;
  }

  /**
   * Sets the normalized string.
   *
   * @param normalizedName the normalized string
   */
  @Override
  public void setNormalizedName(String normalizedName) {
    this.normalizedName = normalizedName;
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

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime * result
            + ((normalizedName == null) ? 0 : normalizedName.hashCode());
    return result;
  }

  /* see superclass */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    LexicalClassJpa other = (LexicalClassJpa) obj;
    if (normalizedName == null) {
      if (other.normalizedName != null)
        return false;
    } else if (!normalizedName.equals(other.normalizedName))
      return false;
    return true;
  }

}
