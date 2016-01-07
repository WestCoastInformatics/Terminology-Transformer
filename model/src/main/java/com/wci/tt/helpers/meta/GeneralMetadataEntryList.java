/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.GeneralMetadataEntry;

/**
 * Represents a sortable list of {@link GeneralMetadataEntry}
 */
public interface GeneralMetadataEntryList extends
    ResultList<GeneralMetadataEntry> {
  // nothing extra, a simple wrapper for easy serialization
}
