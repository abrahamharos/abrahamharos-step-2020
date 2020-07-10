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

package com.google.sps.comments;

import com.google.sps.COMMONS;
import com.google.sps.auth.AuthServlet;
import com.google.sps.auth.User;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**Servlet that returns an array of comments in JSON format*/
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (AuthServlet.hasUserNicknameSet()) {
      int numberOfComments =  Integer.parseInt(request.getParameter("numberOfComments"));
      String orderBy = (String) request.getParameter("orderBy");

      List<Comment> comments = retrieveComments(numberOfComments, orderBy);
      // Convert the array of comments retrieved to JSON.
      String json = convertCommentsToJson(comments);

      // Send the JSON as the response.
      response.setContentType("application/json; charset=UTF-8");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().println(json);
    } else {
      response.sendRedirect(COMMONS.AUTH_URL);
    }
  }

  /**
   * Converts a Comments instance into a JSON string using the Gson library.
   */
  private String convertCommentsToJson(List<Comment> comments) {
    Gson gson = new Gson();
    return gson.toJson(comments);
  }

  /**
   * Retrieve comments from datastore
   */
  private List<Comment> retrieveComments(int numberOfComments, String orderBy) {
    List<Comment> comments = new ArrayList<>();
    
    // Prepares query that will retrieve comments and sort comment depending on user's preferences.
    Query query = new Query("Comment");
    if (orderBy.equals("popular")) {
      query.addSort("votes", SortDirection.DESCENDING);
    } else {
      query.addSort("timestamp", SortDirection.DESCENDING);
    }

    // Execute query.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    User user = AuthServlet.getUserData();
    // Applies limit in the number of comments.
    List<Entity> resultsLimited = results.asList(FetchOptions.Builder.withLimit(numberOfComments));

    for (Entity entity : resultsLimited) {
      // Reads data from an entity.
      long commentId = entity.getKey().getId();
      String commentUsername = (String) entity.getProperty("username");
      String commentUserId = (String) entity.getProperty("userId");
      Date commentTimestamp = (Date) entity.getProperty("timestamp");
      String commentMessage = (String) entity.getProperty("message");
      long votes = (long) entity.getProperty("votes");
      double commentSentiment = (double) entity.getProperty("opinion");

      // Check if the comment is posted by the same user logged in.
      boolean postedBySameUser = user.getId().equals(commentUserId) ? true : false;

      // Add new comment to the array list.
      comments.add(new Comment(commentId, commentTimestamp, commentUsername, commentUserId,
              commentMessage, votes, postedBySameUser, commentSentiment));
    }
    return comments;
  }
}
