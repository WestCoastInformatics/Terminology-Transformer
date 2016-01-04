/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.wci.tt.MemoryEntry;
import com.wci.tt.helpers.Configurable;
import com.wci.tt.helpers.StringList;
import com.wci.tt.services.TranslationService;

/**
 * The Interface PhraseMemoryHandler.
 */
public interface PhraseMemoryHandler extends Configurable {

  /**
   * Gets the entries as stream.
   *
   * @param entries the entries
   * @return the entries as stream
   * @throws UnsupportedEncodingException the unsupported encoding exception
   */
  ByteArrayInputStream getEntriesAsStream(List<MemoryEntry> entries)
    throws UnsupportedEncodingException;

  /**
   * Gets the entries as list.
   *
   * @param content the content
   * @return the entries as list
   * @throws Exception the exception
   */
  List<MemoryEntry> getEntriesAsList(InputStream content) throws Exception;

  /**
   * Suggest phrase memory.
   *
   * @param phrase the phrase
   * @param translationId the translation id
   * @param translationService the translation service
   * @return the string list
   * @throws Exception the exception
   */
  StringList suggestTranslation(String phrase, Long translationId,
    TranslationService translationService) throws Exception;

}
