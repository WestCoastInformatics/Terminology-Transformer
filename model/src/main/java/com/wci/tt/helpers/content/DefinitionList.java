/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.Definition;

/**
 * Represents a sortable list of {@link Definition}
 */
public interface DefinitionList extends ResultList<Definition> {
  // nothing extra, a simple wrapper for easy serialization
}
