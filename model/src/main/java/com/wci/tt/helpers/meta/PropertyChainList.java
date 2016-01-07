/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.PropertyChain;

/**
 * Represents a sortable list of {@link PropertyChain}
 */
public interface PropertyChainList extends ResultList<PropertyChain> {
  // nothing extra, a simple wrapper for easy serialization
}
