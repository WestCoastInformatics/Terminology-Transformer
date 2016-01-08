/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.ReleaseProperty;

/**
 * Represents a sortable list of {@link ReleaseProperty}
 */
public interface ReleasePropertyList extends ResultList<ReleaseProperty> {
  // nothing extra, a simple wrapper for easy serialization
}
