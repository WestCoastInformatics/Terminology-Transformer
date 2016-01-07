/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.Code;

/**
 * Represents a sortable list of {@link Code}
 */
public interface CodeList extends ResultList<Code> {
  // nothing extra, a simple wrapper for easy serialization
}
