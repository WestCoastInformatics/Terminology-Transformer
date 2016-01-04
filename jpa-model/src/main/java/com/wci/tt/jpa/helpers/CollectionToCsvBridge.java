/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.helpers;

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.search.bridge.StringBridge;

/**
 * Hibernate search field bridge for a list.
 */
public class CollectionToCsvBridge implements StringBridge {

  /* see superclass */
  @Override
  public String objectToString(Object value) {
    if (value != null) {
      StringBuilder buf = new StringBuilder();

      Collection<?> col = (Collection<?>) value;
      Iterator<?> it = col.iterator();
      while (it.hasNext()) {
        String next = it.next().toString();
        buf.append(next);
        if (it.hasNext())
          buf.append(", ");
      }
      return buf.toString();
    }
    return null;
  }
}