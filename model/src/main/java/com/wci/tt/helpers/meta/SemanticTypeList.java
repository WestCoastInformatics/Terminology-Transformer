/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.SemanticType;

/**
 * Represents a sortable list of {@link SemanticType}
 */
public interface SemanticTypeList extends ResultList<SemanticType> {
  // nothing extra, a simple wrapper for easy serialization
}
