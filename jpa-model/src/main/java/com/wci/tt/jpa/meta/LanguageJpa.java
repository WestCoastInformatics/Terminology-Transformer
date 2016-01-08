/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.meta;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

import com.wci.tt.model.meta.Language;

/**
 * JPA-enabled implementation of {@link Language}.
 */
@Entity
@Table(name = "languages", uniqueConstraints = @UniqueConstraint(columnNames = {
    "abbreviation", "terminology"
}))
@Audited
@XmlRootElement(name = "language")
public class LanguageJpa extends AbstractAbbreviation implements Language {

  /** The iso code. */
  @Column(nullable = false)
  private String isoCode;

  /** The iso3 code. */
  @Column(nullable = false)
  private String iso3Code;

  /**
   * Instantiates an empty {@link LanguageJpa}.
   */
  public LanguageJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link LanguageJpa} from the specified parameters.
   *
   * @param language the language
   */
  public LanguageJpa(Language language) {
    super(language);
    isoCode = language.getISOCode();
    iso3Code = language.getISO3Code();
  }

  /* see superclass */
  @Override
  public String getISOCode() {
    return isoCode;
  }

  /* see superclass */
  @Override
  public void setISOCode(String isoCode) {
    this.isoCode = isoCode;
  }

  /* see superclass */
  @Override
  public String getISO3Code() {
    return iso3Code;
  }

  /* see superclass */
  @Override
  public void setISO3Code(String iso3Code) {
    this.iso3Code = iso3Code;
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((iso3Code == null) ? 0 : iso3Code.hashCode());
    result = prime * result + ((isoCode == null) ? 0 : isoCode.hashCode());
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
    LanguageJpa other = (LanguageJpa) obj;
    if (iso3Code == null) {
      if (other.iso3Code != null)
        return false;
    } else if (!iso3Code.equals(other.iso3Code))
      return false;
    if (isoCode == null) {
      if (other.isoCode != null)
        return false;
    } else if (!isoCode.equals(other.isoCode))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "LanguageJpa [" + super.toString() + "isoCode=" + isoCode
        + ", iso3Code=" + iso3Code + "]";
  }

}
