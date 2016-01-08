/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.helper;

import java.util.Map;
import java.util.Properties;

import com.wci.tt.helpers.PrecedenceList;
import com.wci.tt.helpers.TerminologyList;
import com.wci.tt.helpers.meta.RootTerminologyList;
import com.wci.tt.jpa.services.RootServiceJpa;
import com.wci.tt.model.meta.AdditionalRelationshipType;
import com.wci.tt.model.meta.AttributeName;
import com.wci.tt.model.meta.GeneralMetadataEntry;
import com.wci.tt.model.meta.LabelSet;
import com.wci.tt.model.meta.Language;
import com.wci.tt.model.meta.PropertyChain;
import com.wci.tt.model.meta.RelationshipType;
import com.wci.tt.model.meta.RootTerminology;
import com.wci.tt.model.meta.SemanticType;
import com.wci.tt.model.meta.TermType;
import com.wci.tt.model.meta.Terminology;
import com.wci.tt.services.MetadataService;
import com.wci.tt.services.handlers.GraphResolutionHandler;

/**
 * Default implementation of {@link MetadataService}.
 */
public abstract class AbstractMetadataServiceJpaHelper extends RootServiceJpa
    implements MetadataService {

  /**
   * Instantiates an empty {@link AbstractMetadataServiceJpaHelper}.
   *
   * @throws Exception the exception
   */
  public AbstractMetadataServiceJpaHelper() throws Exception {
    super();
  }

  //
  // Not needed for sub-handler

  /* see superclass */
  @Override
  public RootTerminologyList getRootTerminologies() throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public Terminology getTerminology(String terminology, String version)
    throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public TerminologyList getVersions(String terminology) throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public String getLatestVersion(String terminology) throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public TerminologyList getTerminologyLatestVersions() throws Exception {
    // n/a
    return null;
  }

  @Override
  public TerminologyList getTerminologies() throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public Map<String, Map<String, String>> getAllMetadata(String terminology,
    String version) throws Exception {
    // n/a handled by superclass
    return null;
  }

  /* see superclass */
  @Override
  public void enableListeners() {
    // n/a

  }

  /* see superclass */
  @Override
  public void disableListeners() {
    // n/a

  }

  /* see superclass */
  @Override
  public boolean isLastModifiedFlag() {
    // n/a
    return false;
  }

  /* see superclass */
  @Override
  public void setLastModifiedFlag(boolean lastModifiedFlag) {
    // n/a

  }

  /* see superclass */
  @Override
  public SemanticType addSemanticType(SemanticType semanticType)
    throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateSemanticType(SemanticType semanticType) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public void removeSemanticType(Long id) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public AttributeName addAttributeName(AttributeName AttributeName)
    throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateAttributeName(AttributeName AttributeName) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public void removeAttributeName(Long id) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public LabelSet addLabelSet(LabelSet labelSet) throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateLabelSet(LabelSet labelSet) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void removeLabelSet(Long id) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public Language addLanguage(Language Language) throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateLanguage(Language Language) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public void removeLanguage(Long id) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public AdditionalRelationshipType addAdditionalRelationshipType(
    AdditionalRelationshipType additionalRelationshipType) throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateAdditionalRelationshipType(
    AdditionalRelationshipType additionalRelationshipType) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public void removeAdditionalRelationshipType(Long id) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public PropertyChain addPropertyChain(PropertyChain propertyChain)
    throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updatePropertyChain(PropertyChain propertyChain) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void removePropertyChain(Long id) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public RelationshipType addRelationshipType(RelationshipType relationshipType)
    throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateRelationshipType(RelationshipType relationshipType)
    throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void removeRelationshipType(Long id) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public Terminology addTerminology(Terminology terminology) throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateTerminology(Terminology terminology) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public void removeTerminology(Long id) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public RootTerminology addRootTerminology(RootTerminology rootTerminology)
    throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateRootTerminology(RootTerminology rootTerminology)
    throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public void removeRootTerminology(Long id) throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public TermType addTermType(TermType termType) throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateTermType(TermType termType) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void removeTermType(Long id) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public GeneralMetadataEntry addGeneralMetadataEntry(GeneralMetadataEntry entry)
    throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updateGeneralMetadataEntry(GeneralMetadataEntry entry)
    throws Exception {
    // n/a

  }

  /* see superclass */
  @Override
  public void removeGeneralMetadataEntry(Long id) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public PrecedenceList addPrecedenceList(PrecedenceList list) throws Exception {
    // n/a
    return null;
  }

  /* see superclass */
  @Override
  public void updatePrecedenceList(PrecedenceList list) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void removePrecedenceList(Long id) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public GraphResolutionHandler getGraphResolutionHandler(String terminology)
    throws Exception {
    // n/a
    return null;
  }

}
