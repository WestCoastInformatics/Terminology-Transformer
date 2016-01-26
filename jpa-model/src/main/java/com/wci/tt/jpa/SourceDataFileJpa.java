/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.wci.tt.SourceData;
import com.wci.tt.SourceDataFile;

/**
 * JPA enabled implementation of {@link SourceDataFile}.
 */
@Entity
@Table(name = "source_data_files", uniqueConstraints = @UniqueConstraint(columnNames = {
    "path", "name", "directory"
}) )
@Audited
@Indexed
@XmlRootElement(name = "file")
public class SourceDataFileJpa implements SourceDataFile {

  /** The id. Set initial value to 5 to bypass entries in import.sql */
  @TableGenerator(name = "EntityIdGen", table = "table_generator", pkColumnValue = "Entity")
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "EntityIdGenUser")
  private Long id;

  /** The source data. */
  @ManyToOne(targetEntity = SourceDataJpa.class, optional = true)
  @JoinColumn(nullable = true)
  private SourceData sourceData;

  /** The file name. */
  @Column(nullable = false, unique = true, length = 250)
  private String name;

  /** The directory. */
  @Column(nullable = false)
  private boolean directory;

  /** The file size. */
  @Column(nullable = false, unique = false)
  private Long size;

  /** The file path. */
  @Column(nullable = false, unique = true, length = 250)
  private String path;

  /** The timestamp. */
  @Column(nullable = false, unique = false)
  private Date timestamp = new Date();

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
    // n/a
  }

  /**
   * Instantiates a new source data file jpa.
   *
   * @param sourceDataFile the source data file
   * @param deepCopy the deep copy
   */
  public SourceDataFileJpa(SourceDataFile sourceDataFile, boolean deepCopy) {
    super();
    this.id = sourceDataFile.getId();
    this.name = sourceDataFile.getName();
    this.size = sourceDataFile.getSize();
    this.directory = sourceDataFile.isDirectory();
    this.path = sourceDataFile.getPath();
    this.lastModified = sourceDataFile.getLastModified();
    this.lastModifiedBy = sourceDataFile.getLastModifiedBy();
    this.sourceDataName = sourceDataFile.getSourceDataName();
  }

  /* see superclass */
  @Override
  public Date getLastModified() {
    return this.lastModified;
  }

  /* see superclass */
  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  /* see superclass */
  @Override
  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  /* see superclass */
  @Override
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  /* see superclass */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public Long getId() {
    return this.id;
  }

  /* see superclass */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /* see superclass */
  @Override
  @Fields({
      @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO),
      @Field(name = "nameSort", index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  })
  public String getName() {
    return this.name;
  }

  /* see superclass */
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
   * Indicates whether or not connected is the case.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @XmlTransient
  public boolean isConnected() {
    return this.sourceDataName != null;
  }

  /* see superclass */
  @Override
  @Fields({
      @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO),
      @Field(name = "sourceDataNameSort", index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  })
  public String getSourceDataName() {
    return this.sourceDataName;
  }

  /* see superclass */
  @Override
  public Date getTimestamp() {
    return this.timestamp;
  }

  /* see superclass */
  @Override
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;

  }

  /* see superclass */
  @Override
  public boolean isDirectory() {
    return directory;
  }

  /* see superclass */
  @Override
  public void setDirectory(boolean directory) {
    this.directory = directory;
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
    result = prime * result + (directory ? 1231 : 1237);
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
    if (directory != other.directory)
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
    return "SourceDataFileJpa [id=" + id + ", name=" + name + ", directory="
        + directory + ", size=" + size + ", path=" + path + ", timestamp="
        + timestamp + ", lastModified=" + lastModified + ", lastModifiedBy="
        + lastModifiedBy + ", sourceDataName=" + sourceDataName + "]";
  }

}
