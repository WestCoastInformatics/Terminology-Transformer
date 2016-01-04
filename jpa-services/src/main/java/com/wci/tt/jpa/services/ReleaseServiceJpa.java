/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services;

import org.apache.log4j.Logger;
import com.wci.tt.ReleaseArtifact;
import com.wci.tt.ReleaseInfo;
import com.wci.tt.jpa.ReleaseArtifactJpa;
import com.wci.tt.jpa.ReleaseInfoJpa;
import com.wci.tt.services.ReleaseService;

/**
 * JPA enabled implementation of {@link ReleaseService}.
 */
public class ReleaseServiceJpa extends ProjectServiceJpa implements
    ReleaseService {

  /**
   * Instantiates an empty {@link ReleaseServiceJpa}.
   *
   * @throws Exception the exception
   */
  public ReleaseServiceJpa() throws Exception {
    super();
  }

  /* see superclass */
  @Override
  public ReleaseInfo getReleaseInfo(Long releaseInfoId) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - get release info " + releaseInfoId);
    ReleaseInfo info = manager.find(ReleaseInfoJpa.class, releaseInfoId);
    // lazy init
    info.getProperties().size();
    info.getArtifacts().size();
    return info;
  }

  /* see superclass */
  @Override
  public ReleaseInfo addReleaseInfo(ReleaseInfo releaseInfo) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - add release info " + releaseInfo.getName());
    return this.addHasLastModified(releaseInfo);

  }

  /* see superclass */
  @Override
  public ReleaseArtifact addReleaseArtifact(ReleaseArtifact artifact)
    throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - add release info " + artifact.getName());
    return addHasLastModified(artifact);
  }

  /* see superclass */
  @Override
  public void updateReleaseArtifact(ReleaseArtifact artifact) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - add release info " + artifact.getName());
    updateHasLastModified(artifact);
  }

  /* see superclass */
  @Override
  public void removeReleaseArtifact(Long artifactId) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - remove release artifact " + artifactId);
    removeHasLastModified(artifactId, ReleaseArtifactJpa.class);
  }

  /* see superclass */
  @Override
  public void removeReleaseInfo(Long id) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - remove release info " + id);
    removeHasLastModified(id, ReleaseInfoJpa.class);
  }

  /* see superclass */
  @Override
  public void updateReleaseInfo(ReleaseInfo releaseInfo) throws Exception {
    Logger.getLogger(getClass()).debug(
        "Release Service - update release info " + releaseInfo.getName());
    updateHasLastModified(releaseInfo);
  }

  @Override
  public ReleaseArtifact getReleaseArtifact(Long id) throws Exception {
    Logger.getLogger(getClass()).debug(
        "ReleaseArtifact Service - get artifact " + id);
    ReleaseArtifact artifact = getHasLastModified(id, ReleaseArtifactJpa.class);
    return artifact;
  }
}
