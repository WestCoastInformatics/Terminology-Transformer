/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.helper;

import javax.persistence.NoResultException;

import com.wci.tt.helpers.PrecedenceList;
import com.wci.tt.helpers.meta.AdditionalRelationshipTypeList;
import com.wci.tt.helpers.meta.AttributeNameList;
import com.wci.tt.helpers.meta.GeneralMetadataEntryList;
import com.wci.tt.helpers.meta.LabelSetList;
import com.wci.tt.helpers.meta.LanguageList;
import com.wci.tt.helpers.meta.PropertyChainList;
import com.wci.tt.helpers.meta.RelationshipTypeList;
import com.wci.tt.helpers.meta.SemanticTypeList;
import com.wci.tt.helpers.meta.TermTypeList;
import com.wci.tt.jpa.helpers.meta.AdditionalRelationshipTypeListJpa;
import com.wci.tt.jpa.helpers.meta.AttributeNameListJpa;
import com.wci.tt.jpa.helpers.meta.GeneralMetadataEntryListJpa;
import com.wci.tt.jpa.helpers.meta.LabelSetListJpa;
import com.wci.tt.jpa.helpers.meta.LanguageListJpa;
import com.wci.tt.jpa.helpers.meta.PropertyChainListJpa;
import com.wci.tt.jpa.helpers.meta.RelationshipTypeListJpa;
import com.wci.tt.jpa.helpers.meta.SemanticTypeListJpa;
import com.wci.tt.jpa.helpers.meta.TermTypeListJpa;
import com.wci.tt.model.content.Relationship;
import com.wci.tt.services.MetadataService;

/**
 * Default implementation of {@link MetadataService}.
 */
public class StandardMetadataServiceJpaHelper extends
    AbstractMetadataServiceJpaHelper {

  /**
   * Instantiates an empty {@link StandardMetadataServiceJpaHelper}.
   *
   * @throws Exception the exception
   */
  public StandardMetadataServiceJpaHelper() throws Exception {
    super();
  }

  /* see superclass */
  @SuppressWarnings({
    "unchecked"
  })
  @Override
  public RelationshipTypeList getRelationshipTypes(String terminology,
    String version) throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("SELECT r from RelationshipTypeJpa r where terminology = :terminology"
                + " and version = :version");

    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    RelationshipTypeList types = new RelationshipTypeListJpa();
    types.setObjects(query.getResultList());
    types.setTotalCount(types.getObjects().size());
    return types;
  }

  /* see superclass */
  @SuppressWarnings({
    "unchecked"
  })
  @Override
  public LanguageList getLanguages(String terminology, String version)
    throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("SELECT r from LanguageJpa r where terminology = :terminology"
                + " and version = :version");

    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    LanguageList types = new LanguageListJpa();
    types.setObjects(query.getResultList());
    types.setTotalCount(types.getObjects().size());
    return types;
  }

  /* see superclass */
  @SuppressWarnings("unchecked")
  @Override
  public PropertyChainList getPropertyChains(String terminology, String version)
    throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("SELECT r from PropertyChainJpa r where terminology = :terminology"
                + " and version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    PropertyChainList types = new PropertyChainListJpa();
    types.setObjects(query.getResultList());
    types.setTotalCount(types.getObjects().size());
    return types;
  }

  /* see superclass */
  @SuppressWarnings("unchecked")
  @Override
  public AdditionalRelationshipTypeList getAdditionalRelationshipTypes(
    String terminology, String version) throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("SELECT r from AdditionalRelationshipTypeJpa r where terminology = :terminology"
                + " and version = :version");

    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    AdditionalRelationshipTypeList types =
        new AdditionalRelationshipTypeListJpa();
    types.setObjects(query.getResultList());
    types.setTotalCount(types.getObjects().size());

    return types;
  }

  /* see superclass */
  @SuppressWarnings("unchecked")
  @Override
  public AttributeNameList getAttributeNames(String terminology, String version)
    throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("SELECT a from AttributeNameJpa a where terminology = :terminology"
                + " and version = :version");

    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    AttributeNameList names = new AttributeNameListJpa();
    names.setObjects(query.getResultList());
    names.setTotalCount(names.getObjects().size());
    return names;
  }

  /* see superclass */
  @SuppressWarnings("unchecked")
  @Override
  public LabelSetList getLabelSets(String terminology, String version)
    throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("SELECT a from LabelSetJpa a where terminology = :terminology"
                + " and version = :version");

    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    LabelSetList labelSets = new LabelSetListJpa();
    labelSets.setObjects(query.getResultList());
    labelSets.setTotalCount(labelSets.getObjects().size());
    return labelSets;
  }

  /* see superclass */
  @SuppressWarnings("unchecked")
  @Override
  public SemanticTypeList getSemanticTypes(String terminology, String version)
    throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("SELECT s from SemanticTypeJpa s where terminology = :terminology"
                + " and version = :version");

    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    SemanticTypeList types = new SemanticTypeListJpa();
    types.setObjects(query.getResultList());
    types.setTotalCount(types.getObjects().size());
    return types;
  }

  /* see superclass */
  @SuppressWarnings("unchecked")
  @Override
  public TermTypeList getTermTypes(String terminology, String version)
    throws Exception {
    javax.persistence.Query query =
        manager
            .createQuery("SELECT t from TermTypeJpa t where terminology = :terminology"
                + " and version = :version");

    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    TermTypeList types = new TermTypeListJpa();
    types.setObjects(query.getResultList());
    types.setTotalCount(types.getObjects().size());
    return types;
  }

  /* see superclass */
  @Override
  public boolean isStatedRelationship(Relationship<?, ?> relationship) {
    return true;
  }

  /* see superclass */
  @Override
  public boolean isInferredRelationship(Relationship<?, ?> relationship) {
    return true;
  }

  /* see superclass */
  @SuppressWarnings("unchecked")
  @Override
  public RelationshipTypeList getNonGroupingRelationshipTypes(
    String terminology, String version) throws Exception {
    javax.persistence.Query query =
        manager.createQuery("SELECT r from RelationshipTypeJpa r "
            + " where groupingType = 0" + " and terminology = :terminology"
            + " and version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    RelationshipTypeList types = new RelationshipTypeListJpa();
    types.setObjects(query.getResultList());
    types.setTotalCount(types.getObjects().size());
    return types;
  }

  /* see superclass */
  @SuppressWarnings("unchecked")
  @Override
  public GeneralMetadataEntryList getGeneralMetadataEntries(String terminology,
    String version) {
    javax.persistence.Query query =
        manager.createQuery("SELECT g from GeneralMetadataEntryJpa g"
            + " where terminology = :terminology" + " and version = :version");

    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    GeneralMetadataEntryList entries = new GeneralMetadataEntryListJpa();
    entries.setObjects(query.getResultList());
    entries.setTotalCount(entries.getObjects().size());
    return entries;
  }

  /* see superclass */
  @Override
  public PrecedenceList getDefaultPrecedenceList(String terminology,
    String version) throws Exception {

    javax.persistence.Query query =
        manager.createQuery("SELECT p from PrecedenceListJpa p"
            + " where defaultList = 1 and terminology = :terminology "
            + " and version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    try {
      return (PrecedenceList) query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  /* see superclass */
  @Override
  public void refreshCaches() throws Exception {
    close();
    manager = factory.createEntityManager();
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

}
