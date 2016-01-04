/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.ReleaseInfo;

/**
 * Represents a sortable list of {@link ReleaseInfo}
 */
public interface ReleaseInfoList extends ResultList<ReleaseInfo> {
  // nothing extra, a simple wrapper for easy serialization
}
