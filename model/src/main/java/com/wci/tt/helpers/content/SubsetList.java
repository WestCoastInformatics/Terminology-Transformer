/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.Subset;

/**
 * Represents a sortable list of {@link Subset}
 */
public interface SubsetList extends ResultList<Subset> {
  // nothing extra, a simple wrapper for easy serialization
}
