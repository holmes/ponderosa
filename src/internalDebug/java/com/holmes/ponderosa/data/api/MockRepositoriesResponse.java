package com.holmes.ponderosa.data.api;

import com.holmes.ponderosa.data.api.model.RepositoriesResponse;
import java.util.Arrays;
import java.util.Collections;

import static com.holmes.ponderosa.data.api.MockRepositories.BUTTERKNIFE;
import static com.holmes.ponderosa.data.api.MockRepositories.DAGGER;
import static com.holmes.ponderosa.data.api.MockRepositories.JAVAPOET;
import static com.holmes.ponderosa.data.api.MockRepositories.MOSHI;
import static com.holmes.ponderosa.data.api.MockRepositories.OKHTTP;
import static com.holmes.ponderosa.data.api.MockRepositories.OKIO;
import static com.holmes.ponderosa.data.api.MockRepositories.PICASSO;
import static com.holmes.ponderosa.data.api.MockRepositories.RETROFIT;
import static com.holmes.ponderosa.data.api.MockRepositories.SQLBRITE;
import static com.holmes.ponderosa.data.api.MockRepositories.TELESCOPE;
import static com.holmes.ponderosa.data.api.MockRepositories.U2020;
import static com.holmes.ponderosa.data.api.MockRepositories.WIRE;

public enum MockRepositoriesResponse {
  SUCCESS("Success", new RepositoriesResponse(Arrays.asList( //
      BUTTERKNIFE, //
      DAGGER, //
      JAVAPOET, //
      OKHTTP, //
      OKIO, //
      PICASSO, //
      RETROFIT, //
      SQLBRITE, //
      TELESCOPE, //
      U2020, //
      WIRE, //
      MOSHI))),
  ONE("One", new RepositoriesResponse(Collections.singletonList(DAGGER))),
  EMPTY("Empty", new RepositoriesResponse(null));

  public final String name;
  public final RepositoriesResponse response;

  MockRepositoriesResponse(String name, RepositoriesResponse response) {
    this.name = name;
    this.response = response;
  }

  @Override public String toString() {
    return name;
  }
}
