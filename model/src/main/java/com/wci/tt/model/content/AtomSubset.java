/**
 * Copyright 2015 West Coast Informatics, LLC
 */
/*************************************************************
 * Subset: Subset.java
 * Last Updated: Feb 27, 2009
 *************************************************************/
package com.wci.tt.model.content;

import com.wci.tt.helpers.content.HasMembers;

/**
 * Represents a subset of {@link Atom}s asserted by a terminology.
 */
public interface AtomSubset extends Subset, HasMembers<AtomSubsetMember> {
  // n/a
}
