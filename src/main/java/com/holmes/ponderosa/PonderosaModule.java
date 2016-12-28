package com.holmes.ponderosa;

import android.app.Application;
import com.holmes.ponderosa.data.DataModule;
import com.holmes.ponderosa.ui.UiModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    includes = {
        UiModule.class,
        DataModule.class
    },
    injects = {
        PonderosaApp.class
    }
)
public final class PonderosaModule {
  private final PonderosaApp app;

  public PonderosaModule(PonderosaApp app) {
    this.app = app;
  }

  @Provides @Singleton Application provideApplication() {
    return app;
  }
}
