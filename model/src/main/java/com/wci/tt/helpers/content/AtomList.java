/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.Atom;

/**
 * Represents a sortable list of {@link Atom}
 */
public interface AtomList extends ResultList<Atom> {
  // nothing extra, a simple wrapper for easy serialization
}
