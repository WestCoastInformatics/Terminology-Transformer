/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.helpers;

/**
 * Generically something that will report progress to a listener.
 */
public interface ProgressReporter {

  /**
   * Adds a {@link ProgressListener}.
   * @param l the {@link ProgressListener}
   */
  public void addProgressListener(ProgressListener l);

  /**
   * Removes a {@link ProgressListener}.
   * @param l the {@link ProgressListener}
   */
  public void removeProgressListener(ProgressListener l);
}
