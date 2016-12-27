package com.holmes.ponderosa.data.api.oauth;

import com.f2prateek.rx.preferences.Preference;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton public final class AuthInterceptor implements Interceptor {
  private final Preference<String> accessToken;

  @Inject public AuthInterceptor(@AccessToken Preference<String> accessToken) {
    this.accessToken = accessToken;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    HttpUrl.Builder urlBuilder = chain.request().url().newBuilder();
    urlBuilder
        .addQueryParameter("user", "stop-trying-to-hack-my-house")
        .addQueryParameter("pass", "oh-no-you-dont");

    Request.Builder requestBuilder = chain.request().newBuilder().url(urlBuilder.build());
    return chain.proceed(requestBuilder.build());
  }
}
