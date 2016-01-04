/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.helpers;

import com.wci.tt.rf2.LanguageRefsetMember;

/**
 * Represents a sortable list of {@link LanguageRefsetMember}
 */
public interface LanguageRefsetMemberList extends
    ResultList<LanguageRefsetMember> {
  // nothing extra, a simple wrapper for easy serialization
}
