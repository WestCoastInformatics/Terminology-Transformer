/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.MemberValidationResult;

/**
 * Represents a sortable list of {@link MemberValidationResult}.
 */
public interface MemberValidationResultList extends
    ResultList<MemberValidationResult> {
  // nothing extra, a simple wrapper for easy serialization
}
