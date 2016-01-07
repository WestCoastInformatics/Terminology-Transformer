/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.LexicalClass;

/**
 * Represents a sortable list of {@link LexicalClass}
 */
public interface LexicalClassList extends ResultList<LexicalClass> {
  // nothing extra, a simple wrapper for easy serialization
}
