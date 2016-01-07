/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.AttributeName;

/**
 * Represents a sortable list of {@link AttributeName}
 */
public interface AttributeNameList extends ResultList<AttributeName> {
  // nothing extra, a simple wrapper for easy serialization
}
