package com.holmes.ponderosa;

import com.holmes.ponderosa.ui.InternalReleaseUiModule;
import dagger.Module;

@Module(
    addsTo = PonderosaModule.class,
    includes = InternalReleaseUiModule.class,
    overrides = true
)
public final class InternalReleasePonderosaModule {
}
