/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.RelationshipType;

/**
 * Represents a sortable list of {@link RelationshipType}
 */
public interface RelationshipTypeList extends ResultList<RelationshipType> {
  // nothing extra, a simple wrapper for easy serialization
}
