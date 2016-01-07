/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.Attribute;

/**
 * Represents a sortable list of {@link Attribute}
 */
public interface AttributeList extends ResultList<Attribute> {
  // nothing extra, a simple wrapper for easy serialization
}
