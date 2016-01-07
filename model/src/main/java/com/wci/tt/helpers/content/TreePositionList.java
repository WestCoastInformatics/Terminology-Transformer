/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.AtomClass;
import com.wci.tt.model.content.TreePosition;

/**
 * Represents a sortable list of {@link TreePosition}
 */
public interface TreePositionList extends
    ResultList<TreePosition<? extends AtomClass>> {
  // nothing extra, a simple wrapper for easy serialization
}
