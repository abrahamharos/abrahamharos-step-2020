// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.auth;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.COMMONS;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

  /**
   * doGet method returns the user data when user is registered (has a nickname).
   * if the user does not have a nickname, the servlet return a temporal user
   * with the login URL and the auth status
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/json");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      if (hasUserNicknameSet()) {
        // If user is completely registered return user's data.
        User user = getUserData();
        user.setAuthStatus("hasNickname");
        response.getWriter().println(userToJson(user));
      } else {
        // If user is logged in but not registered return auth status.
        String userUrl = userService.createLogoutURL(COMMONS.AUTH_URL);
        String userId = userService.getCurrentUser().getUserId();

        User tempUser = new User(userId, null, userUrl, "loggedIn");
        response.getWriter().println(userToJson(tempUser));
      }
    } else {
      // If user is logged out, return the log in url.
      String userUrl = userService.createLoginURL(COMMONS.AUTH_URL);
      User tempUser = new User(null, null, userUrl, "loggedOut");
      response.getWriter().println(userToJson(tempUser));
    }

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect(COMMONS.AUTH_URL);
      return;
    }

    String username = request.getParameter("username");
    String id = userService.getCurrentUser().getUserId();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("UserInfo", id);
    entity.setProperty("id", id);
    entity.setProperty("username", username);
    // The put() function automatically inserts new data or updates existing data based on ID.
    datastore.put(entity);
    response.sendRedirect(COMMONS.INDEX_PAGE);
  }

  /**
   * Returns the nickname of the user with id, or empty String if the user has not set a nickname.
   */
  public static String getUserName(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
            new Query("UserInfo")
                    .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) return null;
    return (String) entity.getProperty("username");
  }

  // Function that returns if a user is registered.
  public static boolean hasUserNicknameSet() {
    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) return false;

    // If user has not set a nickname, return false
    // User is logged in and has a nickname, so the request can proceed
    String nickname = getUserName(userService.getCurrentUser().getUserId());
    return nickname != null;
  }

  // Converts User object to JSON.
  private String userToJson(User userData){
    Gson gson = new Gson();
    return gson.toJson(userData);
  }

  // Function that returns user data in Json format.
  public static User getUserData() {
    // Initialize userService and Datastore to retrieve user data.
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String id = userService.getCurrentUser().getUserId();

    Query query =
            new Query("UserInfo")
                    .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();

    // Creates User Object to return user's information.
    String userId = (String) entity.getProperty("id");
    String userName = (String) entity.getProperty("username");
    String userUrl = userService.createLogoutURL(COMMONS.AUTH_URL);
    return new User(userId, userName, userUrl, null);
  }
}