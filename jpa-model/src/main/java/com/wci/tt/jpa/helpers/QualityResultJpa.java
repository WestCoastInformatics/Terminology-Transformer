package com.wci.tt.jpa.helpers;

import com.wci.tt.helpers.SearchResult;

// TODO: Auto-generated Javadoc
/**
 * The Class QualityResult.
 */
public class QualityResultJpa implements com.wci.tt.helpers.QualityResult {

  /** The id. */
  private Long id;

  /** The terminology. */
  private String terminology;

  /** The version. */
  private String version;

  /** The terminology id. */
  private String terminologyId;

  /** The quality. */
  private float quality;

  /** The value. */
  private String value;

  /** The obsolete. */
  private boolean obsolete;
  
  public QualityResultJpa() { }
  
  public QualityResultJpa(SearchResult sr) {
    this.id = sr.getId();
    this.obsolete = sr.isObsolete();
    this.terminology = sr.getTerminology();
    this.version = sr.getVersion();
    this.terminologyId = sr.getTerminologyId();
    this.value = sr.getValue();
    
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  @Override
  public Long getId() {
    return id;
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
   * Gets the terminology.
   *
   * @return the terminology
   */
  @Override
  public String getTerminology() {
    return terminology;
  }

  /**
   * Sets the terminology.
   *
   * @param terminology the new terminology
   */
  @Override
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  @Override
  public String getVersion() {
    return version;
  }

  /**
   * Sets the version.
   *
   * @param version the new version
   */
  @Override
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Gets the terminology id.
   *
   * @return the terminology id
   */
  @Override
  public String getTerminologyId() {
    return terminologyId;
  }

  /**
   * Sets the terminology id.
   *
   * @param terminologyId the new terminology id
   */
  @Override
  public void setTerminologyId(String terminologyId) {
    this.terminologyId = terminologyId;
  }

  /**
   * Gets the quality.
   *
   * @return the quality
   */
  @Override
  public float getQuality() {
    return quality;
  }

  /**
   * Sets the quality.
   *
   * @param quality the new quality
   */
  @Override
  public void setQuality(float quality) {
    this.quality = quality;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  @Override
  public String getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the new value
   */
  @Override
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Checks if is obsolete.
   *
   * @return true, if is obsolete
   */
  @Override
  public boolean isObsolete() {
    return obsolete;
  }

  /**
   * Sets the obsolete.
   *
   * @param obsolete the new obsolete
   */
  @Override
  public void setObsolete(boolean obsolete) {
    this.obsolete = obsolete;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (obsolete ? 1231 : 1237);
    result = prime * result + Float.floatToIntBits(quality);
    result =
        prime * result + ((terminology == null) ? 0 : terminology.hashCode());
    result = prime * result
        + ((terminologyId == null) ? 0 : terminologyId.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    result = prime * result + ((version == null) ? 0 : version.hashCode());
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
    QualityResultJpa other = (QualityResultJpa) obj;
    if (obsolete != other.obsolete)
      return false;
    if (Float.floatToIntBits(quality) != Float.floatToIntBits(other.quality))
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

  @Override
  public String toString() {
    return "QualityResultJpa [id=" + id + ", terminology=" + terminology
        + ", version=" + version + ", terminologyId=" + terminologyId
        + ", quality=" + quality + ", value=" + value + ", obsolete=" + obsolete
        + "]";
  }

}
