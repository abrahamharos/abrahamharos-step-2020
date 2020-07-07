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
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String username = getUserName(userService.getCurrentUser().getUserId());
      String logoutUrl = userService.createLogoutURL("/auth");
      printRegistrationPage(out, username, logoutUrl);
    } else {
      String loginUrl = userService.createLoginURL("/auth");
      printLoginPage(out, loginUrl);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/auth");
      return;
    }

    String username = request.getParameter("username");
    String id = userService.getCurrentUser().getUserId();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("UserInfo", id);
    entity.setProperty("id", id);
    entity.setProperty("username", username);
    // The put() function automatically inserts new data or updates existing data based on ID
    datastore.put(entity);

    response.sendRedirect("/index.html");
  }

  /**
   * Returns the nickname of the user with id, or empty String if the user has not set a nickname.
   */
  private String getUserName(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
            new Query("UserInfo")
                    .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String username = (String) entity.getProperty("username");
    return username;
  }

  /* Prints the html body of the login page */
  private void printLoginPage(PrintWriter out, String loginUrl) {
    out.println("<link rel=\"stylesheet\" href=\"style.css\">");
    out.println("<div id=\"content\">");
    out.println("<header><h1>Authentication page</h1></header>");
    out.println("<article>");
    out.println("<p>Welcome to Abraham Haros' portfolio, In order to access the content, please login bellow.</p>");
    out.println("<a href=\"" + loginUrl + "\" class=\"btn\">Click here to log in.</a>");
    out.println("</article></div>");
  }

  private void printRegistrationPage(PrintWriter out, String username, String logoutUrl) {
    out.println("<link rel=\"stylesheet\" href=\"style.css\">");
    out.println("<div id=\"content\">");
    out.println("<header><h1>Registration page</h1></header>");
    out.println("<article>");
    out.println("<p>Welcome to Abraham Haros' portfolio, In order to access the content, please register your" +
            " username bellow.</p>");
    out.println("<form method=\"POST\" action=\"/auth\">");
    out.println("<input class=\"text-input\" name=\"username\" value=\"" + username + "\" placeholder=\"Username\" />");
    out.println("<input type=\"submit\" value=\"Submit\" class=\"btn\">");
    out.println("</form>");
    out.println("<a href=\"" + logoutUrl + "\" class=\"btn\">Click here to log out.</a>");
    out.println("</article></div>");
  }
}
