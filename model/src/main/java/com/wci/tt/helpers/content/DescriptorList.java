/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.Descriptor;

/**
 * Represents a sortable list of {@link Descriptor}
 */
public interface DescriptorList extends ResultList<Descriptor> {
  // nothing extra, a simple wrapper for easy serialization
}
