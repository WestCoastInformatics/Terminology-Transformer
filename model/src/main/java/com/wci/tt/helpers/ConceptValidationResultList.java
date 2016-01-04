/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.ConceptValidationResult;


/**
 * Represents a sortable list of {@link ConceptValidationResult}.
 */
public interface ConceptValidationResultList extends
    ResultList<ConceptValidationResult> {
  // nothing extra, a simple wrapper for easy serialization
}
