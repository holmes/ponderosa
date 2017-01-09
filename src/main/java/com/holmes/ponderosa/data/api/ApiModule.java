package com.holmes.ponderosa.data.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.moshi.Moshi;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module(
    complete = false,
    library = true
)
public final class ApiModule {
  public static final HttpUrl PRODUCTION_API_URL = HttpUrl.parse("https://connected.homeseer.com/");

  @Provides @Singleton HttpUrl provideBaseUrl() {
    return PRODUCTION_API_URL;
  }

  @Provides @Singleton @Named("Api") OkHttpClient provideApiClient(OkHttpClient client) {
    return createApiClient(client).build();
  }

  @Provides @Singleton Retrofit provideRetrofit(HttpUrl baseUrl, @Named("Api") OkHttpClient client,
      Moshi moshi) {
    return new Retrofit.Builder() //
        .client(client) //
        .baseUrl(baseUrl) //
        .addConverterFactory(MoshiConverterFactory.create(moshi)) //
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //
        .build();
  }

  @Provides @Singleton HomeSeerService provideHomeSeerService(Retrofit retrofit) {
    return retrofit.create(HomeSeerService.class);
  }

  static OkHttpClient.Builder createApiClient(OkHttpClient client) {
    return client.newBuilder()
        .addNetworkInterceptor(new StethoInterceptor());
  }
}
