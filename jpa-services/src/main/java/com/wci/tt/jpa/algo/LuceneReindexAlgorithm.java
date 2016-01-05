/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.algo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

import com.wci.tt.algo.Algorithm;
import com.wci.tt.jpa.UserJpa;
import com.wci.tt.jpa.services.RootServiceJpa;
import com.wci.tt.services.helpers.ProgressEvent;
import com.wci.tt.services.helpers.ProgressListener;

/**
 * Implementation of an algorithm to regenerate indexes.
 */
public class LuceneReindexAlgorithm extends RootServiceJpa implements Algorithm {

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The request cancel flag. */
  boolean requestCancel = false;

  /** The terminology. */
  private String indexedObjects;

  /** The full text entity manager. */
  private FullTextEntityManager fullTextEntityManager;

  /**
   * Instantiates an empty {@link LuceneReindexAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public LuceneReindexAlgorithm() throws Exception {
    super();
  }

  /**
   * Sets the indexed objects.
   *
   * @param indexedObjects the indexed objects
   */
  public void setIndexedObjects(String indexedObjects) {
    this.indexedObjects = indexedObjects;
  }

  /* see superclass */
  @Override
  public void compute() throws Exception {
    if (fullTextEntityManager == null) {
      fullTextEntityManager = Search.getFullTextEntityManager(manager);
    }
    computeLuceneIndexes(indexedObjects);
    // fullTextEntityManager.close();
  }

  /* see superclass */
  @Override
  public void reset() throws Exception {
    if (fullTextEntityManager == null) {
      fullTextEntityManager = Search.getFullTextEntityManager(manager);
    }
    clearLuceneIndexes();
    // fullTextEntityManager.close();
  }

  /**
   * Compute lucene indexes.
   *
   * @param indexedObjects the indexed objects
   * @throws Exception the exception
   */
  private void computeLuceneIndexes(String indexedObjects) throws Exception {
    // set of objects to be re-indexed
    Set<String> objectsToReindex = new HashSet<>();

    // if no parameter specified, re-index all objects
    if (indexedObjects == null || indexedObjects.isEmpty()) {

      objectsToReindex.add("ConceptJpa");
      objectsToReindex.add("ProjectJpa");
      objectsToReindex.add("PhraseMemoryJpa");
      objectsToReindex.add("ReleaseInfoJpa");
      objectsToReindex.add("RefsetJpa");
      objectsToReindex.add("TranslationJpa");
      objectsToReindex.add("TrackingRecordJpa");
      objectsToReindex.add("UserJpa");
      objectsToReindex.add("ConceptRefsetMemberJpa");

      // otherwise, construct set of indexed objects
    } else {

      // remove white-space and split by comma
      String[] objects = indexedObjects.replaceAll(" ", "").split(",");

      // add each value to the set
      for (String object : objects)
        objectsToReindex.add(object);

    }

    Logger.getLogger(getClass()).info("Starting reindexing for:");
    for (String objectToReindex : objectsToReindex) {
      Logger.getLogger(getClass()).info("  " + objectToReindex);
    }

    // full text entity manager
    FullTextEntityManager fullTextEntityManager =
        Search.getFullTextEntityManager(manager);


    if (objectsToReindex.contains("UserJpa")) {
      Logger.getLogger(getClass()).info("  Creating indexes for UserJpa");
      fullTextEntityManager.purgeAll(UserJpa.class);
      fullTextEntityManager.flushToIndexes();
      fullTextEntityManager.createIndexer(UserJpa.class)
          .batchSizeToLoadObjects(100).cacheMode(CacheMode.NORMAL)
          .threadsToLoadObjects(4).startAndWait();

      objectsToReindex.remove("UserJpa");
    }


    if (objectsToReindex.size() != 0) {
      throw new Exception(
          "The following objects were specified for re-indexing, but do not exist as indexed objects: "
              + objectsToReindex.toString());
    }

    // Cleanup
    Logger.getLogger(getClass()).info("done ...");
  }

  /**
   * Clear lucene indexes.
   *
   * @throws Exception the exception
   */
  private void clearLuceneIndexes() throws Exception {
    fullTextEntityManager.purgeAll(UserJpa.class);
  }

  /**
   * Fires a {@link ProgressEvent}.
   * @param pct percent done
   * @param note progress note
   */
  public void fireProgressEvent(int pct, String note) {
    ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(getClass()).info("    " + pct + "% " + note);
  }

  /* see superclass */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /* see superclass */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /* see superclass */
  @Override
  public void cancel() {
    requestCancel = true;
  }

  /* see superclass */
  @Override
  public void refreshCaches() throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void checkPreconditions() throws Exception {
    // n/a
  }
}
