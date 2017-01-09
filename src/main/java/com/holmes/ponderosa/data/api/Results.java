package com.holmes.ponderosa.data.api;

import com.jakewharton.retrofit2.adapter.rxjava2.Result;
import io.reactivex.functions.Predicate;

public final class Results {
  private static final Predicate<Result> SUCCESSFUL =
          result -> !result.isError() && result.response().isSuccessful();

  public static Predicate<Result> isSuccessful() {
    return SUCCESSFUL;
  }

  private Results() {
    throw new AssertionError("No instances.");
  }
}
