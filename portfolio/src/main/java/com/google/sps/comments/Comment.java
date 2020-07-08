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

import java.util.Date;

/** Class containing comment Object. */
public final class Comment {

  private final Date datePosted;
  private final String username;
  private final String userId;
  private final String message;
  private final long id;
  private long votes;
  private final boolean postedBySameUser;

  public Comment(long id, Date datePosted, String username, String userId, String message, long votes, boolean postedBySameUser) {
    this.datePosted = datePosted;
    this.username = username;
    this.userId = userId;
    this.message = message;
    this.id = id;
    this.votes = votes;
    this.postedBySameUser = postedBySameUser;
  }

  public Date getDatePosted() {
    return datePosted;
  }

  public String getUsername() {
    return username;
  }

  public String getUserId() {
    return userId;
  }

  public String getMessage() {
    return message;
  }

  public long getId() {
    return id;
  }

  public long getVotes(){
    return votes; 
  }

  public void setVotes(long vote){
      votes = vote;
  }

  public boolean isPostedBySameUser() {
    return postedBySameUser;
  }
}
