/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.helpers.ScoredResult;

/**
 * JPA enabled scored implementation of {@link ScoredResult}.
 */
@XmlRootElement(name = "scoredResult")
public class ScoredResultJpa implements ScoredResult, Comparable<ScoredResult> {

  /** The id. */
  private Long id;

  /** The score. */
  private float score = 0;

  /** The value. */
  private String value;

  /** The obsolete. */
  private boolean obsolete = false;

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
    this.id = result.getId();
    this.obsolete = result.isObsolete();
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
  public float getScore() {
    return score;
  }

  /* see superclass */
  @Override
  public void setScore(float score) throws Exception {
    if (score < 0 || score > 1) {
      throw new Exception("Score must be between 0-1 inclusive");
    }

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
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    if (score != other.score)
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ScoredResultJpa [id=" + id + ", score=" + score + ", value="
        + value + ", obsolete=" + obsolete + "]";
  }

  /* see superclass */
  @Override
  public int compareTo(ScoredResult o) {
    Float score1 = new Float(this.getScore());
    Float score2 = new Float(o.getScore());

    return score2.compareTo(score1);
  }
}
