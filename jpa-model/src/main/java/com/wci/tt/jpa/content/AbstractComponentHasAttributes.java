/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.content;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.envers.Audited;

import com.wci.tt.model.content.Attribute;
import com.wci.tt.model.content.ComponentHasAttributes;

/**
 * Abstract implementation of {@link ComponentHasAttributes} for use with JPA.
 */
@Audited
@MappedSuperclass
public abstract class AbstractComponentHasAttributes extends AbstractComponent
    implements ComponentHasAttributes {

  /** The attributes. */
  @OneToMany(targetEntity = AttributeJpa.class)
  // @IndexedEmbedded(targetElement = AttributeJpa.class)
  private List<Attribute> attributes = null;

  /**
   * Instantiates an empty {@link AbstractComponentHasAttributes}.
   */
  public AbstractComponentHasAttributes() {
    // do nothing
  }

  /**
   * Instantiates a {@link AbstractComponentHasAttributes} from the specified
   * parameters.
   *
   * @param component the component
   * @param deepCopy the deep copy
   */
  public AbstractComponentHasAttributes(ComponentHasAttributes component,
      boolean deepCopy) {
    super(component);

    if (deepCopy) {
      for (Attribute attribute : component.getAttributes()) {
        addAttribute(new AttributeJpa(attribute));
      }
    }
  }

  /* see superclass */
  @Override
  @XmlElement(type = AttributeJpa.class, name = "attribute")
  public List<Attribute> getAttributes() {
    if (attributes == null) {
      attributes = new ArrayList<>(1);
    }
    return attributes;
  }

  /* see superclass */
  @Override
  public void setAttributes(List<Attribute> attributes) {
    this.attributes = attributes;
  }

  /* see superclass */
  @Override
  public void addAttribute(Attribute attribute) {
    if (attributes == null) {
      attributes = new ArrayList<>(1);
    }
    attributes.add(attribute);
  }

  /* see superclass */
  @Override
  public void removeAttribute(Attribute attribute) {
    if (attributes == null) {
      attributes = new ArrayList<>(1);
    }
    attributes.remove(attribute);
  }

  /* see superclass */
  @Override
  public Attribute getAttributeByName(String name) {
    for (Attribute attribute : getAttributes()) {
      // If there are more than one, this just returns the first.
      if (attribute.getName().equals(name)) {
        return attribute;
      }
    }
    return null;
  }

}
