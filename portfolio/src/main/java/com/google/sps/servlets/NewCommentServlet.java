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

/**Servlet that inserts a comment in datastore*/
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Insert new comment into the array list
    addNewComment(request);
    
    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  /**
   * Retrieve data from the webpage's form
   */
  private void addNewComment(HttpServletRequest request) {
    //Get input values from the form.
    String commentUser = request.getParameter("username");
    String commentMessage = request.getParameter("message");
    Date time = new Date();

    //Create and prepare entity for datastore
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("username", commentUser);
    commentEntity.setProperty("timestamp", time);
    commentEntity.setProperty("message", commentMessage);
    commentEntity.setProperty("votes", 0);

    //Put entity into datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }
}
