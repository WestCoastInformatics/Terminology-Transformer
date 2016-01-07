/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.StringClass;

/**
 * Represents a sortable list of {@link StringClass}
 */
public interface StringClassList extends ResultList<StringClass> {
  // nothing extra, a simple wrapper for easy serialization
}
