/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.search.annotations.Indexed;

import com.wci.tt.helpers.ScoredResult;
import com.wci.tt.helpers.SearchResult;

/**
 * JPA enabled scored implementation of {@link SearchResult}.
 */
@Entity
@Table(name = "scored_result")
@Indexed
@XmlRootElement(name = "scoredResult")
public class ScoredResultJpa implements ScoredResult {

  /** The id. */
  @TableGenerator(name = "EntityIdGen", table = "table_generator", pkColumnValue = "Entity")
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "EntityIdGen")
  private Long id;

  /** The terminology. */
  private String terminology;

  /** The version. */
  private String version;

  /** The terminology id. */
  private String terminologyId;

  /** The score. */
  private float score;

  /** The value. */
  private String value;

  /** The obsolete. */
  private boolean obsolete;

  /**
   * Instantiates an empty {@link ScoredResultJpa}.
   */
  public ScoredResultJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link ScoredResultJpa} from the specified parameters.
   *
   * @param result the scored result
   */
  public ScoredResultJpa(ScoredResult result) {
    this.obsolete = result.isObsolete();
    this.terminology = result.getTerminology();
    this.version = result.getVersion();
    this.terminologyId = result.getTerminologyId();
    this.value = result.getValue();
    this.score = result.getScore();
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
    return terminology;
  }

  /* see superclass */
  @Override
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  /* see superclass */
  @Override
  public String getVersion() {
    return version;
  }

  /* see superclass */
  @Override
  public void setVersion(String version) {
    this.version = version;
  }

  /* see superclass */
  @Override
  public String getTerminologyId() {
    return terminologyId;
  }

  /* see superclass */
  @Override
  public void setTerminologyId(String terminologyId) {
    this.terminologyId = terminologyId;
  }

  /* see superclass */
  @Override
  public float getScore() {
    return score;
  }

  /* see superclass */
  @Override
  public void setScore(float score) {
    this.score = score;
  }

  /* see superclass */
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
  public boolean isObsolete() {
    return obsolete;
  }

  /* see superclass */
  @Override
  public void setObsolete(boolean obsolete) {
    this.obsolete = obsolete;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (obsolete ? 1231 : 1237);
    result = prime * result + Float.floatToIntBits(score);
    result =
        prime * result + ((terminology == null) ? 0 : terminology.hashCode());
    result =
        prime * result
            + ((terminologyId == null) ? 0 : terminologyId.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
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
    ScoredResultJpa other = (ScoredResultJpa) obj;
    if (obsolete != other.obsolete)
      return false;
    if (Float.floatToIntBits(score) != Float.floatToIntBits(other.score))
      return false;
    if (terminology == null) {
      if (other.terminology != null)
        return false;
    } else if (!terminology.equals(other.terminology))
      return false;
    if (terminologyId == null) {
      if (other.terminologyId != null)
        return false;
    } else if (!terminologyId.equals(other.terminologyId))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    if (version == null) {
      if (other.version != null)
        return false;
    } else if (!version.equals(other.version))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ScoredResultJpa [id=" + id + ", terminology=" + terminology
        + ", version=" + version + ", terminologyId=" + terminologyId
        + ", score=" + score + ", value=" + value + ", obsolete=" + obsolete
        + "]";
  }
}
