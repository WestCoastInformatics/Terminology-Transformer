/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.TermType;

/**
 * Represents a sortable list of {@link TermType}
 */
public interface TermTypeList extends ResultList<TermType> {
  // nothing extra, a simple wrapper for easy serialization
}
