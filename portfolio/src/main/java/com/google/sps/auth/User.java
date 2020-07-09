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

import com.google.sps.COMMONS;

public final class User {
  private final String id;
  private final String name;
  private String url;
  private COMMONS.authStatus userStatus;

  public User(String id, String name, String url, COMMONS.authStatus userStatus) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.userStatus = userStatus;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setAuthStatus(COMMONS.authStatus userStatus) {
    this.userStatus = userStatus;
  }
}
