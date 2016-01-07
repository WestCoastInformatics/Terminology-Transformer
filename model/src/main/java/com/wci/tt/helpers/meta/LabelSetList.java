/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.LabelSet;

/**
 * Represents a sortable list of {@link LabelSet}
 */
public interface LabelSetList extends ResultList<LabelSet> {
  // nothing extra, a simple wrapper for easy serialization
}
