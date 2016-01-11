/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.algo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.wci.tt.services.helpers.PushBackReader;

/**
 * Container for RF2 readers.
 */
public class RrfReaders {

  /** The sorted rf2 dir. */
  public File inputDir;

  /** The readers. */
  private Map<Keys, PushBackReader> readers = new HashMap<>();

  /**
   * The Enum Keys, RXN or RN format
   */
  public enum Keys {
    
    /**
     * NOTE: Ad-hoc modified to use RXN values where available to match RXNORM data
     */

    /** The mrconso. */
    RXNCONSO,

    /** The mrdef. */
    RXNDEF,

    /** The mrdoc. */
    RXNDOC,

    /** The mrmap. */
    RXNMAP,

    /** The mrrank. */
    RXNRANK,

    /** The mrrel. */
    RXNREL,

    /** The mrsab. */
    RXNSAB,


    /** The mrsat. */
    RXNSAT,


    /** The mrsty. */
    RXNSTY,

    /** The srdef. */
    SRDEF;

  }

  /**
   * Instantiates an empty {@link RrfReaders}.
   *
   * @param inputDir the input dir
   * @throws Exception if anything goes wrong
   */
  public RrfReaders(File inputDir) throws Exception {
    this.inputDir = inputDir;
  }

  /**
   * Open readers.
   *
   * @throws Exception the exception
   */
  public void openReaders() throws Exception {

    readers.put(Keys.RXNCONSO, getReader("consoByConcept.sort"));
    readers.put(Keys.RXNDEF, getReader("defByConcept.sort"));
    readers.put(Keys.RXNDOC, getReader("docByKey.sort"));
    readers.put(Keys.RXNMAP, getReader("mapByConcept.sort"));
    readers.put(Keys.RXNRANK, getReader("rankByRank.sort"));
    readers.put(Keys.RXNREL, getReader("relByConcept.sort"));
    readers.put(Keys.RXNSAB, getReader("sabBySab.sort"));
    readers.put(Keys.RXNSAT, getReader("satByConcept.sort"));
    readers.put(Keys.RXNSTY, getReader("styByConcept.sort"));
    readers.put(Keys.SRDEF, getReader("srdef.sort"));

  }
  

  /**
   * Open original readers.
   *
   * @throws Exception the exception
   */
  public void openOriginalReaders() throws Exception {

    for (Keys key : Keys.values()) {
      readers.put(key, getReader(key.toString() + ".RRF"));
    }
    readers.put(Keys.SRDEF, getReader("SRDEF"));
  }

  /**
   * Close readers.
   *
   * @throws Exception the exception
   */
  public void closeReaders() throws Exception {
    for (BufferedReader reader : readers.values()) {
      try {
        reader.close();
      } catch (Exception e) {
        // do nothing;
      }
    }
  }

  /**
   * Returns the reader.
   *
   * @param filename the filename
   * @return the reader
   * @throws Exception the exception
   */
  private PushBackReader getReader(String filename) throws Exception {
    File file = new File(inputDir, filename);
    
    // if file does not exist, create an empty one
    if (!file.exists()) {
      file.createNewFile();
    }
    
    return new PushBackReader(new BufferedReader(new FileReader(file)));
   /* 
    
      if (file != null && file.exists()) {
        return new PushBackReader(new BufferedReader(new FileReader(file)));
  
    } else {
      return new PushBackReader(null);
    }*/
  }

  /**
   * Returns the reader.
   *
   * @param key the key
   * @return the reader
   */
  public PushBackReader getReader(Keys key) {
    return readers.get(key);
  }

}
