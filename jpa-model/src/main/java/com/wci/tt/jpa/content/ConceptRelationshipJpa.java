/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.content;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.wci.tt.jpa.helpers.MapValueToCsvBridge;
import com.wci.tt.model.content.Concept;
import com.wci.tt.model.content.ConceptRelationship;

/**
 * JPA-enabled implementation of {@link ConceptRelationship}.
 */
@Entity
@Table(name = "concept_relationships", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "version", "id"
}))
@Audited
@Indexed
@XmlRootElement(name = "conceptRelationship")
public class ConceptRelationshipJpa extends
    AbstractRelationship<Concept, Concept> implements ConceptRelationship {

  /** The from concept. */
  @ManyToOne(targetEntity = ConceptJpa.class, optional = false)
  @JoinColumn(nullable = false)
  private Concept from; // index the hibernate id only, rest service/jpa will
                        // find concept

  /** the to concept. */
  @ManyToOne(targetEntity = ConceptJpa.class, optional = false)
  @JoinColumn(nullable = false)
  private Concept to; // index all methods

  /** The alternate terminology ids. */
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(nullable = true)
  private Map<String, String> alternateTerminologyIds; // index

  /**
   * Instantiates an empty {@link ConceptRelationshipJpa}.
   */
  public ConceptRelationshipJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link ConceptRelationshipJpa} from the specified
   * parameters.
   *
   * @param relationship the relationship
   * @param deepCopy the deep copy
   */
  public ConceptRelationshipJpa(ConceptRelationship relationship,
      boolean deepCopy) {
    super(relationship, deepCopy);
    to = relationship.getTo();
    from = relationship.getFrom();
    alternateTerminologyIds =
        new HashMap<>(relationship.getAlternateTerminologyIds());
  }

  /* see superclass */
  @Override
  @XmlTransient
  public Concept getFrom() {
    return from;
  }

  /* see superclass */
  @Override
  public void setFrom(Concept component) {
    this.from = component;
  }

  /**
   * Returns the from id. For JAXB.
   *
   * @return the from id
   */
  public Long getFromId() {
    return from == null ? null : from.getId();
  }

  /**
   * Sets the from id.
   *
   * @param id the from id
   */
  public void setFromId(Long id) {
    if (from == null) {
      from = new ConceptJpa();
    }
    from.setId(id);
  }

  /**
   * Returns the from terminology.
   *
   * @return the from terminology
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getFromTerminology() {
    return from == null ? null : from.getTerminology();
  }

  /**
   * Sets the from terminology.
   *
   * @param terminology the from terminology
   */
  public void setFromTerminology(String terminology) {
    if (from == null) {
      from = new ConceptJpa();
    }
    from.setTerminology(terminology);
  }

  /**
   * Returns the from version.
   *
   * @return the from version
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getFromVersion() {
    return from == null ? null : from.getVersion();
  }

  /**
   * Sets the from terminology id.
   *
   * @param version the from terminology id
   */
  public void setFromVersion(String version) {
    if (from == null) {
      from = new ConceptJpa();
    }
    from.setVersion(version);
  }

  /**
   * Returns the from terminology id.
   *
   * @return the from terminology id
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getFromTerminologyId() {
    return from == null ? null : from.getTerminologyId();
  }

  /**
   * Sets the from terminology id.
   *
   * @param terminologyId the from terminology id
   */
  public void setFromTerminologyId(String terminologyId) {
    if (from == null) {
      from = new ConceptJpa();
    }
    from.setTerminologyId(terminologyId);
  }

  /**
   * Returns the from term. For JAXB.
   *
   * @return the from term
   */
  @Fields({
      @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO, analyzer = @Analyzer(definition = "noStopWord")),
      @Field(name = "fromNameSort", index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  })
  public String getFromName() {
    return from == null ? null : from.getName();
  }

  /**
   * Sets the from term.
   *
   * @param term the from term
   */
  public void setFromName(String term) {
    if (from == null) {
      from = new ConceptJpa();
    }
    from.setName(term);
  }

  /* see superclass */
  @Override
  @XmlTransient
  public Concept getTo() {
    return to;
  }

  /* see superclass */
  @Override
  public void setTo(Concept component) {
    this.to = component;
  }

  /**
   * Returns the to id. For JAXB.
   *
   * @return the to id
   */
  public Long getToId() {
    return to == null ? null : to.getId();
  }

  /**
   * Sets the to id.
   *
   * @param id the to id
   */
  public void setToId(Long id) {
    if (to == null) {
      to = new ConceptJpa();
    }
    to.setId(id);
  }

  /**
   * Returns the to terminology id.
   *
   * @return the to terminology id
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getToTerminologyId() {
    return to == null ? null : to.getTerminologyId();
  }

  /**
   * Sets the to terminology id.
   *
   * @param terminologyId the to terminology id
   */
  public void setToTerminologyId(String terminologyId) {
    if (to == null) {
      to = new ConceptJpa();
    }
    to.setTerminologyId(terminologyId);
  }

  /**
   * Returns the to terminology.
   *
   * @return the to terminology
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getToTerminology() {
    return to == null ? null : to.getTerminology();
  }

  /**
   * Sets the to terminology.
   *
   * @param terminology the to terminology
   */
  public void setToTerminology(String terminology) {
    if (to == null) {
      to = new ConceptJpa();
    }
    to.setTerminology(terminology);
  }

  /**
   * Returns the to terminology version.
   *
   * @return the to terminology version
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getToVersion() {
    return to == null ? null : to.getVersion();
  }

  /**
   * Sets the to terminology version.
   *
   * @param version the to terminology version
   */
  public void setToVersion(String version) {
    if (to == null) {
      to = new ConceptJpa();
    }
    to.setVersion(version);
  }

  /**
   * Returns the to term. For JAXB.
   *
   * @return the to term
   */
  @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
  public String getToName() {
    return to == null ? null : to.getName();
  }

  /**
   * Sets the to term.
   *
   * @param term the to term
   */
  public void setToName(String term) {
    if (to == null) {
      to = new ConceptJpa();
    }
    to.setName(term);
  }

  /* see superclass */
  @Override
  @FieldBridge(impl = MapValueToCsvBridge.class)
  @Field(name = "alternateTerminologyIds", index = Index.YES, analyze = Analyze.YES, store = Store.NO)
  public Map<String, String> getAlternateTerminologyIds() {
    if (alternateTerminologyIds == null) {
      alternateTerminologyIds = new HashMap<>(2);
    }
    return alternateTerminologyIds;
  }

  /* see superclass */
  @Override
  public void setAlternateTerminologyIds(
    Map<String, String> alternateTerminologyIds) {
    this.alternateTerminologyIds = alternateTerminologyIds;
  }

  /* see superclass */
  @Override
  public void putAlternateTerminologyId(String terminology, String terminologyId) {
    if (alternateTerminologyIds == null) {
      alternateTerminologyIds = new HashMap<>(2);
    }
    alternateTerminologyIds.put(terminology, terminologyId);
  }

  /* see superclass */
  @Override
  public void removeAlternateTerminologyId(String terminology) {
    if (alternateTerminologyIds == null) {
      alternateTerminologyIds = new HashMap<>(2);
    }
    alternateTerminologyIds.remove(terminology);

  }

  /**
   * CUSTOM to support to/from/alternateTerminologyIds.
   *
   * @return the int
   * @see com.wci.tt.jpa.content.AbstractRelationship#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime
            * result
            + ((from == null || from.getTerminologyId() == null) ? 0 : from
                .getTerminologyId().hashCode());
    result =
        prime
            * result
            + ((to == null || to.getTerminologyId() == null) ? 0 : to
                .getTerminologyId().hashCode());
    result =
        prime
            * result
            + ((alternateTerminologyIds == null) ? 0 : alternateTerminologyIds
                .toString().hashCode());
    return result;
  }

  /**
   * Custom equals method for to/from.getTerminologyId
   *
   * @param obj the obj
   * @return true, if successful
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConceptRelationshipJpa other = (ConceptRelationshipJpa) obj;
    if (from == null) {
      if (other.from != null)
        return false;
    } else if (from.getTerminologyId() == null) {
      if (other.from != null && other.from.getTerminologyId() != null)
        return false;
    } else if (!from.getTerminologyId().equals(other.from.getTerminologyId()))
      return false;
    if (to == null) {
      if (other.to != null)
        return false;
    } else if (to.getTerminologyId() == null) {
      if (other.to != null && other.to.getTerminologyId() != null)
        return false;
    } else if (!to.getTerminologyId().equals(other.to.getTerminologyId()))
      return false;
    if (alternateTerminologyIds == null) {
      if (other.alternateTerminologyIds != null)
        return false;
    } else if (!alternateTerminologyIds.equals(other.alternateTerminologyIds))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ConceptRelationshipJpa [from=" + from + ", to=" + to
        + ", alternateTerminologyIds=" + alternateTerminologyIds
        + ", getFrom()=" + getFrom() + ", getFromId()=" + getFromId()
        + ", getFromTerminology()=" + getFromTerminology()
        + ", getFromVersion()=" + getFromVersion()
        + ", getFromTerminologyId()=" + getFromTerminologyId()
        + ", getFromName()=" + getFromName() + ", getTo()=" + getTo()
        + ", getToId()=" + getToId() + ", getToTerminologyId()="
        + getToTerminologyId() + ", getToTerminology()=" + getToTerminology()
        + ", getToVersion()=" + getToVersion() + ", getToName()=" + getToName()
        + ", getAlternateTerminologyIds()=" + getAlternateTerminologyIds()
        + ", hashCode()=" + hashCode() + ", getRelationshipType()="
        + getRelationshipType() + ", getAdditionalRelationshipType()="
        + getAdditionalRelationshipType() + ", getGroup()=" + getGroup()
        + ", isInferred()=" + isInferred() + ", isStated()=" + isStated()
        + ", isHierarchical()=" + isHierarchical() + ", isAssertedDirection()="
        + isAssertedDirection() + ", toString()=" + super.toString()
        + ", getAttributes()=" + getAttributes() + ", getId()=" + getId()
        + ", getObjectId()=" + getObjectId() + ", getTimestamp()="
        + getTimestamp() + ", getLastModified()=" + getLastModified()
        + ", getLastModifiedBy()=" + getLastModifiedBy()
        + ", isSuppressible()=" + isSuppressible() + ", isObsolete()="
        + isObsolete() + ", isPublished()=" + isPublished()
        + ", isPublishable()=" + isPublishable() + ", getBranch()="
        + getBranch() + ", getVersion()=" + getVersion()
        + ", getTerminology()=" + getTerminology() + ", getTerminologyId()="
        + getTerminologyId() + ", getClass()=" + getClass() + "]";
  }

}
