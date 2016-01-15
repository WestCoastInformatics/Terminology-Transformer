/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import javax.xml.bind.annotation.XmlRootElement;

import com.wci.tt.DataContext;
import com.wci.tt.helpers.ScoredDataContextTuple;
import com.wci.tt.jpa.DataContextJpa;

/**
 * JPA enabled implementation of {@link DataContextJpa} and an associated Data
 * string.
 */
@XmlRootElement(name = "scoredDataContextTuple")
public class ScoredDataContextTupleJpa implements ScoredDataContextTuple,
    Comparable<ScoredDataContextTuple> {

  /** The id. */
  private Long id;

  /** The data being analyzed. */
  private String data;

  /** The context associated with the data. */
  private DataContext context;

  /** The context associated with the data. */
  private Float score;

  /**
   * Instantiates an empty {@link ScoredDataContextTupleJpa}.
   */
  public ScoredDataContextTupleJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link ScoredDataContextTupleJpa} from the specified
   * parameters.
   *
   * @param tuple the data context tuple
   */
  public ScoredDataContextTupleJpa(ScoredDataContextTuple tuple) {
    this.id = tuple.getId();
    this.data = tuple.getData();
    this.context = tuple.getDataContext();
    this.score = tuple.getScore();
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
  public String getData() {
    return data;
  }

  /* see superclass */
  @Override
  public void setData(String data) {
    this.data = data;
  }

  /* see superclass */
  @Override
  public DataContext getDataContext() {
    return context;
  }

  /* see superclass */
  @Override
  public void setDataContext(DataContext context) {
    this.context = context;
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((data == null) ? 0 : data.hashCode());
    result = prime * result + ((context == null) ? 0 : context.hashCode());
    result = prime * result + Float.floatToIntBits(score);
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
    ScoredDataContextTupleJpa other = (ScoredDataContextTupleJpa) obj;
    if (data == null) {
      if (other.data != null)
        return false;
    } else if (!data.equals(other.data))
      return false;
    if (context == null) {
      if (other.context != null)
        return false;
    } else if (!context.equals(other.context))
      return false;
    if (score == null) {
      if (other.score != null)
        return false;
    } else if (!score.equals(other.score))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ScoredResultJpa [id=" + id + ", data=" + data + ", context="
        + context + ", score=" + score + "]";
  }

  /* see superclass */
  @Override
  public int compareTo(ScoredDataContextTuple o) {
    Float score1 = new Float(this.getScore());
    Float score2 = new Float(o.getScore());

    return score2.compareTo(score1);
  }
}
