/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.Terminology;

/**
 * Represents a sortable list of {@link Terminology}
 */
public interface TerminologyList extends ResultList<Terminology> {
  // nothing extra, a simple wrapper for easy serialization
}
