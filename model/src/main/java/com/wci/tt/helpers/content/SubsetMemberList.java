/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers.content;

import com.wci.tt.helpers.ResultList;
import com.wci.tt.model.content.ComponentHasAttributesAndName;
import com.wci.tt.model.content.Subset;
import com.wci.tt.model.content.SubsetMember;

/**
 * Represents a sortable list of {@link SubsetMember}
 */
public interface SubsetMemberList
    extends
    ResultList<SubsetMember<? extends ComponentHasAttributesAndName, ? extends Subset>> {
  // nothing extra, a simple wrapper for easy serialization
}
