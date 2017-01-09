package com.holmes.ponderosa.data;

import android.app.Application;
import android.content.SharedPreferences;
import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.holmes.ponderosa.data.api.ApiModule;
import com.holmes.ponderosa.data.api.auth.AuthInterceptor;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import javax.inject.Singleton;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.threeten.bp.Clock;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.jakewharton.byteunits.DecimalByteUnit.MEGABYTES;

@Module(
    includes = ApiModule.class,
    complete = false,
    library = true
)
public final class DataModule {
  static final int DISK_CACHE_SIZE = (int) MEGABYTES.toBytes(50);

  @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
    return app.getSharedPreferences("u2020", MODE_PRIVATE);
  }

  @Provides @Singleton RxSharedPreferences provideRxSharedPreferences(SharedPreferences prefs) {
    return RxSharedPreferences.create(prefs);
  }

  @Provides @Singleton Moshi provideMoshi() {
    return new Moshi.Builder()
        .add(new InstantAdapter())
        .build();
  }

  @Provides @Singleton Clock provideClock() {
    return Clock.systemDefaultZone();
  }

  @Provides @Singleton IntentFactory provideIntentFactory() {
    return IntentFactory.REAL;
  }

  @Provides @Singleton OkHttpClient provideOkHttpClient(Application app, AuthInterceptor authInterceptor) {
    return createOkHttpClient(app, authInterceptor).build();
  }

  @Provides @Singleton Picasso providePicasso(Application app, OkHttpClient client) {
    OkHttp3Downloader downloader = new OkHttp3Downloader(client);

    return new Picasso.Builder(app)
        .downloader(downloader)
        .listener((picasso, uri, e) -> Timber.e(e, "Failed to load image: %s", uri))
        .build();
  }

  static OkHttpClient.Builder createOkHttpClient(Application app, AuthInterceptor authInterceptor) {
    // Install an HTTP cache in the application cache directory.
    File cacheDir = new File(app.getCacheDir(), "http");
    Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

    return new OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .cache(cache);
  }
}
