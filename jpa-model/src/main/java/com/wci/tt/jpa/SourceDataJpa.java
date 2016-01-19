package com.wci.tt.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;

/**
 * JPA enabled implementation of {@link SourceDataFile}.
 */
@Entity
@Table(name = "source_datas", uniqueConstraints = @UniqueConstraint(columnNames = {
    "name"
}) )
@Audited
@Indexed
@XmlRootElement(name = "sourceData")
public class SourceDataJpa implements SourceData {

  /** The id. Set initial value to 5 to bypass entries in import.sql */
  @TableGenerator(name = "EntityIdGenUser", table = "table_generator_source_datas", pkColumnValue = "Entity", initialValue = 50)
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "EntityIdGenUser")
  private Long id;

  /** The file name. */
  @Column(nullable = false, unique = true, length = 250)
  private String name;

  /** The source data description */
  @Column(nullable = true, unique = false, length = 4000)
  private String description;

  /** The timestamp. */
  @Column(nullable = false, unique = false, length = 250)
  private Date timestamp = new Date();

  /** The last modified. */
  @Column(nullable = false, unique = false, length = 250)
  private Date lastModified = new Date();

  /** The last modified by. */
  @Column(nullable = false, unique = false, length = 250)
  private String lastModifiedBy;

  /** The data files. */
  @OneToMany(targetEntity = SourceDataFileJpa.class)
  private List<SourceDataFile> sourceDataFiles = new ArrayList<>();

  /**
   * The converter (as fully specified class name) used to process the source
   * data files.
   */
  @Column(nullable = true, unique = false, length = 4000)
  private String converterName;

  /**
   * Instantiates a new source data file jpa.
   */
  public SourceDataJpa() {

  }

  /**
   * Instantiates a new source data jpa.
   *
   * @param sourceData the source data
   * @param deepCopy the deep copy
   */
  public SourceDataJpa(SourceData sourceData, boolean deepCopy) {
    super();
    this.name = sourceData.getName();
    this.lastModified = sourceData.getLastModified();
    this.lastModifiedBy = sourceData.getLastModifiedBy();
    for (SourceDataFile s : sourceData.getSourceDataFiles()) {
      this.sourceDataFiles.add(new SourceDataFileJpa(s, deepCopy));
    }
  }

  /* see superclass */
  @Override
  public Date getTimestamp() {
    return timestamp;
  }

  /* see superclass */
  @Override
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Gets the last modified.
   *
   * @return the last modified
   */
  @Override
  public Date getLastModified() {
    return this.lastModified;
  }

  /**
   * Sets the last modified.
   *
   * @param lastModified the new last modified
   */
  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  /**
   * Gets the last modified by.
   *
   * @return the last modified by
   */
  @Override
  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  /**
   * Sets the last modified by.
   *
   * @param lastModifiedBy the new last modified by
   */
  @Override
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  @Override
  @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /* see superclass */
  @Override
  @XmlElement(type = SourceDataFileJpa.class)
  public List<SourceDataFile> getSourceDataFiles() {
    return this.sourceDataFiles;
  }

  /* see superclass */
  @Override
  public void setSourceDataFiles(List<SourceDataFile> sourceDataFiles) {
    this.sourceDataFiles = sourceDataFiles;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
    result = prime * result
        + ((lastModifiedBy == null) ? 0 : lastModifiedBy.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result
        + ((sourceDataFiles == null) ? 0 : sourceDataFiles.hashCode());
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
    SourceDataJpa other = (SourceDataJpa) obj;
    if (lastModified == null) {
      if (other.lastModified != null)
        return false;
    } else if (!lastModified.equals(other.lastModified))
      return false;
    if (lastModifiedBy == null) {
      if (other.lastModifiedBy != null)
        return false;
    } else if (!lastModifiedBy.equals(other.lastModifiedBy))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (sourceDataFiles == null) {
      if (other.sourceDataFiles != null)
        return false;
    } else if (!sourceDataFiles.equals(other.sourceDataFiles))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "SourceDataJpa [id=" + id + ", name=" + name + ", lastModified="
        + lastModified + ", lastModifiedBy=" + lastModifiedBy
        + ", sourceDataFiles=" + sourceDataFiles + "]";
  }

  /* see superclass */
  @Override
  public void setConverterName(String converterName) {
    this.converterName = converterName;
  }

  /* see superclass */
  @Override
  public String getConverterName() {
    return this.converterName;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

}
