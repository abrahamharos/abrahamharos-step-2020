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

package com.google.sps.servlets;

import com.google.sps.comments.Comment;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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

  private List<Comment> comments = new ArrayList<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Convert the array of comments retrieved to JSON
    String json = retrieveComments();

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Converts a Comments instance into a JSON string using the Gson library.
   */
  private String convertCommentsToJson() {
    Gson gson = new Gson();
    return gson.toJson(comments);
  }

  /**
   * Retrieve comments from datastore
   */
  private void retrieveComments() {
    //Prepares query that will retrieve comments
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    //Execute query
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      //Reads data from an entity
      long commentId = entity.getKey().getId();
      String commentUsername = (String) entity.getProperty("username");
      String commentTimestamp = (String) entity.getProperty("timestamp");
      String commentMessage = (String) entity.getProperty("message");

      //Add new comment to the array list
      comments.add(new Comment(commentTimestamp, commentUsername, commentMessage)); 
    }
  }
}
