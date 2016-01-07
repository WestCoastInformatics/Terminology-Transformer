/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.AdditionalRelationshipType;

/**
 * Represents a sortable list of {@link AdditionalRelationshipType}
 */
public interface AdditionalRelationshipTypeList extends
    ResultList<AdditionalRelationshipType> {
  // nothing extra, a simple wrapper for easy serialization
}
