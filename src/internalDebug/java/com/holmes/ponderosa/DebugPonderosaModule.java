package com.holmes.ponderosa;

import com.holmes.ponderosa.data.DebugDataModule;
import com.holmes.ponderosa.ui.DebugUiModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
    addsTo = PonderosaModule.class,
    includes = {
        DebugUiModule.class,
        DebugDataModule.class,
        DebugActionsModule.class
    },
    overrides = true
)
public final class DebugPonderosaModule {
  // Low-tech flag to force certain debug build behaviors when running in an instrumentation test.
  // This value is used in the creation of singletons so it must be set before the graph is created.
  static boolean instrumentationTest = false;

  @Provides @Singleton @IsInstrumentationTest boolean provideIsInstrumentationTest() {
    return instrumentationTest;
  }
}
