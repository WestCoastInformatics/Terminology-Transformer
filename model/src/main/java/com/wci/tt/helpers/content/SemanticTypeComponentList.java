/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.SemanticTypeComponent;

/**
 * Represents a sortable list of {@link SemanticTypeComponent}
 */
public interface SemanticTypeComponentList extends
    ResultList<SemanticTypeComponent> {
  // nothing extra, a simple wrapper for easy serialization
}
