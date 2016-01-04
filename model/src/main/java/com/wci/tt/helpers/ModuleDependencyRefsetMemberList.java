/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.rf2.ModuleDependencyRefsetMember;

/**
 * Represents a sortable list of {@link ModuleDependencyRefsetMember}
 */
public interface ModuleDependencyRefsetMemberList extends
    ResultList<ModuleDependencyRefsetMember> {
  // nothing extra, a simple wrapper for easy serialization
}
