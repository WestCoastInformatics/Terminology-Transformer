/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.services.handlers;

import java.io.InputStream;
import java.util.List;

import com.wci.tt.DefinitionClause;
import com.wci.tt.Refset;
import com.wci.tt.helpers.Configurable;
import com.wci.tt.rf2.ConceptRefsetMember;

/**
 * Generically represents a handler for importing refset data.
 * 
 * <pre>
 * Requirements
 *  - be able to display available import handlers to a user for importing content
 *  - a global list of import handlers is sufficient (no need to be project specific)
 *  - know that it is importing a refset
 *  - import content from an input stream.
 * </pre>
 */
public interface ImportRefsetHandler extends Configurable {

  /**
   * Returns the file type filter.
   *
   * @return the file type filter
   */
  public String getFileTypeFilter();

  /**
   * Returns the mime type.
   *
   * @return the mime type
   */
  public String getMimeType();

  /**
   * Import members.
   *
   * @param refset the refset
   * @param content the content
   * @return the list
   * @throws Exception the exception
   */
  public List<ConceptRefsetMember> importMembers(Refset refset,
    InputStream content) throws Exception;

  /**
   * Import definition.
   *
   * @param refset the refset
   * @param content the content
   * @return the list
   * @throws Exception the exception
   */
  public List<DefinitionClause> importDefinition(Refset refset,
    InputStream content) throws Exception;

}
