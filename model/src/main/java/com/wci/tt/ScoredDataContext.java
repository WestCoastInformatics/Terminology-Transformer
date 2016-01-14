/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt;

import com.wci.tt.helpers.HasScore;

/**
 * Interface representing a DataContext along with its associated confidence
 * score.
 */
public interface ScoredDataContext extends DataContext, HasScore {
}
