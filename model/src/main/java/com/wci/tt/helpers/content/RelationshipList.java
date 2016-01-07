/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.ComponentHasAttributes;
import com.wci.tt.model.content.Relationship;

/**
 * Represents a sortable list of {@link Relationship}
 */
public interface RelationshipList
    extends
    ResultList<Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes>> {
  // nothing extra, a simple wrapper for easy serialization
}
