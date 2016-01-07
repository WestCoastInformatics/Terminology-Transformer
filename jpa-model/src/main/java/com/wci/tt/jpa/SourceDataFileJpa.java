package com.wci.tt.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.wci.tt.SourceDataFile;

// TODO: Auto-generated Javadoc
/**
 * JPA enabled implementation of {@link SourceDataFile}.
 */
@Entity
@Table(name = "source_data_files", uniqueConstraints = @UniqueConstraint(columnNames = {
    "name"
}) )
@Audited
@Indexed
@XmlRootElement(name = "sourceDataFile")
public class SourceDataFileJpa implements SourceDataFile {

  /** The id. Set initial value to 5 to bypass entries in import.sql */
  @TableGenerator(name = "EntityIdGenUser", table = "table_generator_source_data_files", pkColumnValue = "Entity", initialValue = 50)
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "EntityIdGenUser")
  private Long id;

  /** The file name. */
  @Column(nullable = false, unique = true, length = 250)
  private String name;

  /** The file size. */
  @Column(nullable = false, unique = false)
  private Long size;

  /** The file path. */
  @Column(nullable = false, unique = true, length = 250)
  private String path;

  /** The last modified. */
  @Column(nullable = false, unique = false)
  private Date lastModified;

  /** The last modified by. */
  @Column(nullable = false, unique = false, length = 250)
  private String lastModifiedBy;

  /** The source data this file is connected to, by name. */
  @Column(nullable = true, unique = false, length = 250)
  private String sourceDataName;

  /**
   * Instantiates a new source data file jpa.
   */
  public SourceDataFileJpa() {

  }

  /**
   * Instantiates a new source data file jpa.
   *
   * @param name the name
   * @param size the size
   * @param path the path
   * @param lastModified the last modified
   * @param lastModifiedBy the last modified by
   * @param sourceDataName the source data name
   */
  public SourceDataFileJpa(SourceDataFile sourceDataFile, boolean deepCopy) {
    super();
    this.name = sourceDataFile.getName();
    this.size = sourceDataFile.getSize();
    this.path = sourceDataFile.getPath();
    this.lastModified = sourceDataFile.getLastModified();
    this.lastModifiedBy = sourceDataFile.getLastModifiedBy();
    this.sourceDataName = sourceDataFile.getSourceDataName();
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
  public Long getSize() {
    return size;
  }

  /* see superclass */
  @Override
  public void setSize(Long size) {
    this.size = size;
  }

  /* see superclass */
  @Override
  public String getPath() {
    return path;
  }

  /* see superclass */
  @Override
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Checks if is connected.
   *
   * @return true, if is connected
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @XmlTransient
  public boolean isConnected() {
    return this.sourceDataName != null;
  }

  /* see superclass */
  @Override
  @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
  public String getSourceDataName() {
    return this.sourceDataName;
  }

  /* see superclass */
  @Override
  public void setSourceDataName(String sourceDataName) {
    this.sourceDataName = sourceDataName;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result =
        prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
    result = prime * result
        + ((lastModifiedBy == null) ? 0 : lastModifiedBy.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((path == null) ? 0 : path.hashCode());
    result = prime * result + ((size == null) ? 0 : size.hashCode());
    result = prime * result
        + ((sourceDataName == null) ? 0 : sourceDataName.hashCode());
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
    SourceDataFileJpa other = (SourceDataFileJpa) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
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
    if (path == null) {
      if (other.path != null)
        return false;
    } else if (!path.equals(other.path))
      return false;
    if (size == null) {
      if (other.size != null)
        return false;
    } else if (!size.equals(other.size))
      return false;
    if (sourceDataName == null) {
      if (other.sourceDataName != null)
        return false;
    } else if (!sourceDataName.equals(other.sourceDataName))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "SourceDataFileJpa [id=" + id + ", name=" + name + ", size=" + size
        + ", path=" + path + ", lastModified=" + lastModified
        + ", lastModifiedBy=" + lastModifiedBy + ", sourceDataName="
        + sourceDataName + ", isConnected()=" + isConnected() + "]";
  }

}
