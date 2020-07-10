package com.google.sps;

// Definition of constants that servlets will use.

public interface COMMONS {
  String AUTH_URL = "/index.html";
  String INDEX_PAGE = "/index.html";
  String DEFAULT_USER_NICKNAME = "";
  enum authStatus {
    LOGGED_IN, LOGGED_OUT, HAS_NICKNAME
  }
}
