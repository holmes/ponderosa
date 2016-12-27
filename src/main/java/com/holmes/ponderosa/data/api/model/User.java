package com.holmes.ponderosa.data.api.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.holmes.ponderosa.util.Preconditions;

import static com.holmes.ponderosa.util.Preconditions.checkNotNull;

public final class User {
  @NonNull public final String login;
  @Nullable public final String avatar_url;

  public User(String login, @Nullable String avatar_url) {
    this.login = Preconditions.checkNotNull(login, "login == null");
    this.avatar_url = avatar_url;
  }

  @Override public String toString() {
    return "User{" +
        "login='" + login + '\'' +
        ", avatar_url='" + avatar_url + '\'' +
        '}';
  }
}
