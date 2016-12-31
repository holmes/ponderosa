package com.holmes.ponderosa.data.sql;

import android.app.Application;
import com.holmes.ponderosa.data.DataFetcher;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Module(
    complete = false,
    library = true) public class SQLModule {
  @Provides @Singleton PonderosaSQLiteOpenHelper provideSQLiteOpenHelper(Application app) {
    return new PonderosaSQLiteOpenHelper(app);
  }

  @Provides @Singleton BriteDatabase provideBriteDatabase(PonderosaSQLiteOpenHelper sqLiteOpenHelper) {
    SqlBrite builder = new SqlBrite.Builder() //
        .logger(message -> Timber.tag("Database").v(message)) //
        .build();

    return builder.wrapDatabaseHelper(sqLiteOpenHelper, Schedulers.io());
  }

  @Provides @Singleton DataFetcher provideDataFetcher(BriteDatabase db, HomeSeerService homeSeerService) {
    return new DataFetcher(db, homeSeerService);
  }
}
