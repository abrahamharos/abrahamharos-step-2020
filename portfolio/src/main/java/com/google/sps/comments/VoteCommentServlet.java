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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.COMMONS;
import com.google.sps.auth.AuthServlet;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**Servlet that votes a comment*/
@WebServlet("/vote-comment")
public class VoteCommentServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (AuthServlet.hasUserNicknameSet()) {
      // Retrieve commentId from the request
      long commentId = Long.parseLong(request.getParameter("commentId"));
      boolean typeOfVote = Boolean.parseBoolean(request.getParameter("vote"));

      // If key does not exist, catch the error.
      try {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key commentEntityKey = KeyFactory.createKey("Comment", commentId);
        Entity comment = datastore.get(commentEntityKey);

        // Updates the entity by creating a new one and override it at datastore.
        long numberOfVotes = (long) comment.getProperty("votes");
        // Add or subtract vote.
        numberOfVotes = (typeOfVote) ? numberOfVotes + 1 : numberOfVotes - 1;
        comment.setProperty("votes", numberOfVotes);
        datastore.put(comment);
      } catch(com.google.appengine.api.datastore.EntityNotFoundException e){
        System.out.println(e);
      }
    } else {
      response.sendRedirect(COMMONS.AUTH_URL);
    }
  }
}
