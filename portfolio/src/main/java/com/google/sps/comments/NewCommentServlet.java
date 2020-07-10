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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

/**Servlet that inserts a comment in datastore*/
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (AuthServlet.hasUserNicknameSet()) {
      // Insert new comment into the array list.
      addNewComment(request);

      // Redirect back to the HTML page.
      response.sendRedirect(COMMONS.INDEX_PAGE);
    } else {
      response.sendRedirect(COMMONS.AUTH_URL);
    }
  }

  /**
   * Retrieve data from the webpage's form
   */
  private void addNewComment(HttpServletRequest request) throws IOException {
    // Get input values from the form.
    String commentMessage = request.getParameter("message");
    Date time = new Date();
    User user = AuthServlet.getUserData();

    // Create and prepare entity for datastore.
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("username", user.getName());
    commentEntity.setProperty("userId", user.getId());
    commentEntity.setProperty("timestamp", time);
    commentEntity.setProperty("message", commentMessage);
    commentEntity.setProperty("votes", 0);

    // Determine if a sentiment is good, bad or neutal (based on API's documentation scale).
    double sentiment = sentimentAnalyzer(commentMessage);
    COMMONS.opinion commentOpinion;
    if (sentiment >= 0.5) commentOpinion = COMMONS.opinion.GOOD;
    else if(sentiment <= -0.5) commentOpinion = COMMONS.opinion.BAD;
    else commentOpinion = COMMONS.opinion.NEUTRAL;

    commentEntity.setProperty("opinion", commentOpinion);

    // Put entity into datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }

  // Analyze the overall sentiment of the comment message
  private double sentimentAnalyzer(String commentMessage) throws IOException {
    try (LanguageServiceClient languageService = LanguageServiceClient.create()) {
      Document document = Document.newBuilder().setContent(commentMessage).setType(Document.Type.PLAIN_TEXT).build();
      return (double) languageService.analyzeSentiment(document).getDocumentSentiment().getScore();
    }
  }
}
