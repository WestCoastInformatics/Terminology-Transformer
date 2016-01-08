/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services;

import java.util.List;

import com.wci.tt.helpers.PfsParameter;

// TODO: Auto-generated Javadoc
/**
 * Generically represents a service.
 */
public interface RootService {

  /** The logging object ct threshold. */
  public final static int logCt = 2000;

  /** The commit count. */
  public final static int commitCt = 2000;

  /**
   * Open the factory.
   *
   * @throws Exception the exception
   */
  public void openFactory() throws Exception;

  /**
   * Close the factory.
   *
   * @throws Exception the exception
   */
  public void closeFactory() throws Exception;

  /**
   * Gets the transaction per operation.
   *
   * @return the transaction per operation
   * @throws Exception the exception
   */
  public boolean getTransactionPerOperation() throws Exception;

  /**
   * Sets the transaction per operation.
   *
   * @param transactionPerOperation the new transaction per operation
   * @throws Exception the exception
   */
  public void setTransactionPerOperation(boolean transactionPerOperation)
    throws Exception;

  /**
   * Commit.
   *
   * @throws Exception the exception
   */
  public void commit() throws Exception;

  /**
   * Rollback.
   *
   * @throws Exception the exception
   */
  public void rollback() throws Exception;

  /**
   * Begin transaction.
   *
   * @throws Exception the exception
   */
  public void beginTransaction() throws Exception;

  /**
   * Closes the manager.
   *
   * @throws Exception the exception
   */
  public void close() throws Exception;

  /**
   * Clears the manager.
   *
   * @throws Exception the exception
   */
  public void clear() throws Exception;

  /**
   * Refresh caches.
   *
   * @throws Exception the exception
   */
  public void refreshCaches() throws Exception;

  /**
   * Apply pfs to list.
   *
   * @param <T> the generic type
   * @param list the list
   * @param clazz the clazz
   * @param pfs the pfs
   * @return the list
   * @throws Exception the exception
   */
  public <T> List<T> applyPfsToList(List<T> list, Class<T> clazz,
    PfsParameter pfs) throws Exception;

  /**
   * Checks if is last modified flag.
   *
   * @return true, if is last modified flag
   */
  public boolean isLastModifiedFlag();

  /**
   * Sets the last modified flag.
   *
   * @param lastModifiedFlag the new last modified flag
   */
  public void setLastModifiedFlag(boolean lastModifiedFlag);
  
  /**
   * Commit clear begin transaction.
   *
   * @throws Exception the exception
   */
  public void commitClearBegin() throws Exception;

  /**
   * Log and commit.
   *
   * @param objectCt the object ct
   * @param logCt the log ct
   * @param commitCt the commit ct
   * @throws Exception the exception
   */
  public void logAndCommit(int objectCt, int logCt, int commitCt)
    throws Exception;
}