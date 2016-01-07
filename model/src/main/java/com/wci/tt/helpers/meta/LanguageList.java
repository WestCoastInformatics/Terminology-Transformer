/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.meta;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.meta.Language;

/**
 * Represents a sortable list of {@link Language}
 */
public interface LanguageList extends ResultList<Language> {
  // nothing extra, a simple wrapper for easy serialization
}
