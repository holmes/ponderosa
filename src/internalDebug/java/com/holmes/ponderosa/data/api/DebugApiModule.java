package com.holmes.ponderosa.data.api;

import com.f2prateek.rx.preferences.Preference;
import com.holmes.ponderosa.data.api.ApiModule;
import com.holmes.ponderosa.data.api.GithubService;
import com.holmes.ponderosa.data.ApiEndpoint;
import com.holmes.ponderosa.data.IsMockMode;
import com.holmes.ponderosa.data.NetworkDelay;
import com.holmes.ponderosa.data.NetworkFailurePercent;
import com.holmes.ponderosa.data.NetworkVariancePercent;
import com.holmes.ponderosa.data.api.oauth.OauthInterceptor;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;
import timber.log.Timber;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Module(
    complete = false,
    library = true,
    overrides = true
)
public final class DebugApiModule {
  @Provides @Singleton HttpUrl provideHttpUrl(@ApiEndpoint Preference<String> apiEndpoint) {
    return HttpUrl.parse(apiEndpoint.get());
  }

  @Provides @Singleton HttpLoggingInterceptor provideLoggingInterceptor() {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Timber.tag("OkHttp").v(message));
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
    return loggingInterceptor;
  }

  @Provides @Singleton @Named("Api") OkHttpClient provideApiClient(OkHttpClient client,
      OauthInterceptor oauthInterceptor, HttpLoggingInterceptor loggingInterceptor) {
    return ApiModule.createApiClient(client, oauthInterceptor)
        .addInterceptor(loggingInterceptor)
        .build();
  }

  @Provides @Singleton NetworkBehavior provideBehavior(@NetworkDelay Preference<Long> networkDelay,
      @NetworkFailurePercent Preference<Integer> networkFailurePercent,
      @NetworkVariancePercent Preference<Integer> networkVariancePercent) {
    NetworkBehavior behavior = NetworkBehavior.create();
    behavior.setDelay(networkDelay.get(), MILLISECONDS);
    behavior.setFailurePercent(networkFailurePercent.get());
    behavior.setVariancePercent(networkVariancePercent.get());
    return behavior;
  }

  @Provides @Singleton MockRetrofit provideMockRetrofit(Retrofit retrofit,
      NetworkBehavior behavior) {
    return new MockRetrofit.Builder(retrofit)
        .networkBehavior(behavior)
        .build();
  }

  @Provides @Singleton
  GithubService provideGithubService(Retrofit retrofit,
                                     @IsMockMode boolean isMockMode, MockGithubService mockService) {
    return isMockMode ? mockService : retrofit.create(GithubService.class);
  }
}
