package com.holmes.ponderosa.data.api.auth;

import com.holmes.ponderosa.data.api.auth.CredentialManager.Credentials;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton public final class AuthInterceptor implements Interceptor {
  private CredentialManager credentialManager;

  @Inject public AuthInterceptor(CredentialManager credentialManager) {
    this.credentialManager = credentialManager;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Credentials credentials = credentialManager.retrieve();

    HttpUrl.Builder urlBuilder = chain.request().url().newBuilder();
    urlBuilder
        .addQueryParameter("user", credentials.username)
        .addQueryParameter("pass", credentials.password);

    Request.Builder requestBuilder = chain.request().newBuilder().url(urlBuilder.build());
    return chain.proceed(requestBuilder.build());
  }
}
