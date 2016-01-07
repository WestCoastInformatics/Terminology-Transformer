/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.model.content;

import com.wci.tt.helpers.content.HasRelationships;

/**
 * Represents a terminology component with relationships.
 * @param <T> the relationship type
 */
public interface ComponentHasRelationships<T extends Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes>>
    extends Component, HasRelationships<T> {
  // n/a
}