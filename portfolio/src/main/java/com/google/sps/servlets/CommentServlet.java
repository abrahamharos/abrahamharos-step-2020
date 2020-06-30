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
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**Servlet that returns an array of comments in JSON format*/
@WebServlet("/comments")
public class CommentServlet extends HttpServlet {

  private List<Comment> comments = new ArrayList<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Convert the array of comments to JSON
    String json = convertToJson();

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Insert new comment into the array list
    addNewComment(request);
    
    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  /**
   * Converts a Comments instance into a JSON string using the Gson library.
   */
  private String convertToJson() {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  /**
   * Retrieve data from the webpage's form
   */
  private void addNewComment(HttpServletRequest request) {
    //Get input values from the form.
    String commentUser = request.getParameter("username");
    String commentMessage = request.getParameter("message");
    Date time = new Date();
    //Convert input into a message object
    Comment newComment = new Comment(time, commentUser, commentMessage);

    //Add new comment to the array list
    comments.add(newComment);

  }
}
