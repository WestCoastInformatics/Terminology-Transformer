/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.rest;

import com.wci.tt.User;
import com.wci.tt.UserPreferences;
import com.wci.tt.helpers.StringList;
import com.wci.tt.helpers.UserList;
import com.wci.tt.jpa.UserJpa;
import com.wci.tt.jpa.UserPreferencesJpa;
import com.wci.tt.jpa.helpers.PfsParameterJpa;

/**
 * Represents a security available via a REST service.
 */
public interface SecurityServiceRest {

  /**
   * Authenticate.
   * 
   * @param userName the userName
   * @param password the password
   * @return the string
   * @throws Exception if anything goes wrong
   */
  public User authenticate(String userName, String password) throws Exception;

  /**
   * Logout.
   *
   * @param authToken the auth token
   * @return the string
   * @throws Exception the exception
   */
  public String logout(String authToken) throws Exception;

  /**
   * Get user by id.
   *
   * @param id the id
   * @param authToken the auth token
   * @return the user
   * @throws Exception the exception
   */
  public User getUser(Long id, String authToken) throws Exception;

  /**
   * Get user by user.
   *
   * @param userName the userName
   * @param authToken the auth token
   * @return the user
   * @throws Exception the exception
   */
  public User getUser(String userName, String authToken) throws Exception;

  /**
   * Returns the user for auth token.
   *
   * @param authToken the auth token
   * @return the user for auth token
   * @throws Exception the exception
   */
  public User getUserForAuthToken(String authToken) throws Exception;

  /**
   * Returns the users.
   *
   * @param authToken the auth token
   * @return the users
   * @throws Exception the exception
   */
  public UserList getUsers(String authToken) throws Exception;

  /**
   * Adds the user.
   *
   * @param user the user
   * @param authToken the auth token
   * @return the user
   * @throws Exception the exception
   */
  public User addUser(UserJpa user, String authToken) throws Exception;

  /**
   * Removes the user.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeUser(Long id, String authToken) throws Exception;

  /**
   * Update user.
   *
   * @param user the user
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void updateUser(UserJpa user, String authToken) throws Exception;

  /**
   * Returns the application roles.
   *
   * @param authToken the auth token
   * @return the application roles
   * @throws Exception the exception
   */
  public StringList getApplicationRoles(String authToken) throws Exception;

  /**
   * Find users.
   *
   * @param query the query
   * @param pfs the pfs
   * @param authToken the auth token
   * @return the user list
   * @throws Exception the exception
   */
  public UserList findUsersForQuery(String query, PfsParameterJpa pfs,
    String authToken) throws Exception;

  /**
   * Adds the user preferences.
   *
   * @param userPreferences the user preferences
   * @param authToken the auth token
   * @return the user preferences
   * @throws Exception the exception
   */
  public UserPreferences addUserPreferences(UserPreferencesJpa userPreferences,
    String authToken) throws Exception;

  /**
   * Removes the user preferences.
   *
   * @param id the id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeUserPreferences(Long id, String authToken) throws Exception;

  /**
   * Update user preferences. Returns user preferences because of CASCADE
   * features, need to be able to pick up identifiers.
   *
   * @param userPreferences the user preferences
   * @param authToken the auth token
   * @return the user preferences
   * @throws Exception the exception
   */
  public UserPreferences updateUserPreferences(
    UserPreferencesJpa userPreferences, String authToken) throws Exception;
}