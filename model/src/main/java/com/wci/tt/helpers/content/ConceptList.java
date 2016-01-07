/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.Concept;

/**
 * Represents a sortable list of {@link Concept}
 */
public interface ConceptList extends ResultList<Concept> {
  // nothing extra, a simple wrapper for easy serialization
}
