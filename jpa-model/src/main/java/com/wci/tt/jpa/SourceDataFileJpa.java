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
  
  @Column(nullable = false)
  private boolean directory;

  /** The file size. */
  @Column(nullable = false, unique = false)
  private Long size;

  /** The file path. */
  @Column(nullable = false, unique = true, length = 250)
  private String path;
  
  /** Time uploaded. */
  @Column(nullable = false, unique = false)
  private Date dateUploaded;
  
  /** The timestamp. */
  @Column(nullable = false, unique = false)
  private Date timestamp;

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
   * @param sourceDataFile the source data file
   * @param deepCopy the deep copy
   */
  public SourceDataFileJpa(SourceDataFile sourceDataFile, boolean deepCopy) {
    super();
    this.name = sourceDataFile.getName();
    this.size = sourceDataFile.getSize();
    this.directory = sourceDataFile.isDirectory();
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

  /**
   * Gets the size.
   *
   * @return the size
   */
  /* see superclass */
  @Override
  public Long getSize() {
    return size;
  }

  /**
   * Sets the size.
   *
   * @param size the new size
   */
  /* see superclass */
  @Override
  public void setSize(Long size) {
    this.size = size;
  }

  /**
   * Gets the path.
   *
   * @return the path
   */
  /* see superclass */
  @Override
  public String getPath() {
    return path;
  }

  /**
   * Sets the path.
   *
   * @param path the new path
   */
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

  /**
   * Gets the source data name.
   *
   * @return the source data name
   */
  /* see superclass */
  @Override
  @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
  public String getSourceDataName() {
    return this.sourceDataName;
  }

  /**
   * Sets the source data name.
   *
   * @param sourceDataName the new source data name
   */
  /* see superclass */
  @Override
  public void setSourceDataName(String sourceDataName) {
    this.sourceDataName = sourceDataName;
  }
  
  /**
   * Gets the date uploaded.
   *
   * @return the date uploaded
   */
  @Override
  public Date getDateUploaded() {
    return this.dateUploaded;
  }

  /**
   * Sets the date uploaded.
   *
   * @param dateUploaded the new date uploaded
   */
  @Override
  public void setDateUploaded(Date dateUploaded) {
    this.dateUploaded = dateUploaded;
  }
  

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((dateUploaded == null) ? 0 : dateUploaded.hashCode());
    result = prime * result + (directory ? 1231 : 1237);
    result =
        prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
    result = prime * result
        + ((lastModifiedBy == null) ? 0 : lastModifiedBy.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((path == null) ? 0 : path.hashCode());
    result = prime * result + ((size == null) ? 0 : size.hashCode());
    result = prime * result
        + ((sourceDataName == null) ? 0 : sourceDataName.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SourceDataFileJpa other = (SourceDataFileJpa) obj;
    if (dateUploaded == null) {
      if (other.dateUploaded != null)
        return false;
    } else if (!dateUploaded.equals(other.dateUploaded))
      return false;
    if (directory != other.directory)
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
    if (timestamp == null) {
      if (other.timestamp != null)
        return false;
    } else if (!timestamp.equals(other.timestamp))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SourceDataFileJpa [id=" + id + ", name=" + name + ", directory="
        + directory + ", size=" + size + ", path=" + path + ", dateUploaded="
        + dateUploaded + ", timestamp=" + timestamp + ", lastModified="
        + lastModified + ", lastModifiedBy=" + lastModifiedBy
        + ", sourceDataName=" + sourceDataName + "]";
  }

  /**
   * Gets the timestamp.
   *
   * @return the timestamp
   */
  @Override
  public Date getTimestamp() {
    return this.timestamp;
  }

  /**
   * Sets the timestamp.
   *
   * @param timestamp the new timestamp
   */
  @Override
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
   
  }

  @Override
  public boolean isDirectory() {
    return directory;
  }

  @Override
  public void setDirectory(boolean directory) {
    this.directory = directory;
  }



}
