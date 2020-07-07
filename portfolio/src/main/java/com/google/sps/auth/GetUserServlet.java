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
import com.google.sps.auth.User;
import com.google.sps.auth.AuthServlet;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/getUser")
public class GetUserServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (AuthServlet.isRegistered()) {
      //Convert the user retrieved to JSON
      String json = userToJson(getUserData());

      // Send the JSON as the response
      response.setContentType("application/json;");
      response.getWriter().println(json);
    }else {
      response.sendRedirect("/auth");
    }
  }

  /* Converts User object to JSON */
  private String userToJson(User userData){
    Gson gson = new Gson();
    return gson.toJson(userData);
  }

  /* Function that returns user data in Json format */
  public static User getUserData() {
    //Initialize userService and Datastore to retrieve user data
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String id = userService.getCurrentUser().getUserId();

    Query query =
            new Query("UserInfo")
                    .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();

    //Creates user Object and converts it to Json
    String userId = (String) entity.getProperty("id");
    String userName = (String) entity.getProperty("username");
    String userLogoutUrl = userService.createLogoutURL("/auth");
    return new User(userId, userName, userLogoutUrl);
  }
}
