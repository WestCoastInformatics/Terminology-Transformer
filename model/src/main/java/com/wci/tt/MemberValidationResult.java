/*
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt;

import com.wci.tt.rf2.ConceptRefsetMember;

/**
 * Generically represents a validation result, either an error or a warning
 * about a {@link ConceptRefsetMember}.
 */
public interface MemberValidationResult extends ValidationResult {

  /**
   * Returns the concept refset member.
   *
   * @return the concept refset member
   */
  public ConceptRefsetMember getMember();

  /**
   * Sets the concept refset member.
   *
   * @param member the member
   */
  public void setMember(ConceptRefsetMember member);
}
