/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.RootTerminology;

/**
 * Represents a sortable list of {@link RootTerminology}
 */
public interface RootTerminologyList extends ResultList<RootTerminology> {
  // nothing extra, a simple wrapper for easy serialization
}
