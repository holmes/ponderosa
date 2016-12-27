package com.holmes.ponderosa;

import com.holmes.ponderosa.ui.debug.ContextualDebugActions.DebugAction;
import com.holmes.ponderosa.ui.trending.ScrollBottomTrendingDebugAction;
import com.holmes.ponderosa.ui.trending.ScrollTopTrendingDebugAction;
import dagger.Module;
import dagger.Provides;
import java.util.LinkedHashSet;
import java.util.Set;

import static dagger.Provides.Type.SET_VALUES;

@Module(complete = false, library = true) public final class DebugActionsModule {
  @Provides(type = SET_VALUES) Set<DebugAction> provideDebugActions(
      ScrollBottomTrendingDebugAction scrollBottomTrendingAction,
      ScrollTopTrendingDebugAction scrollTopTrendingAction) {
    Set<DebugAction> actions = new LinkedHashSet<>();
    actions.add(scrollTopTrendingAction);
    actions.add(scrollBottomTrendingAction);
    return actions;
  }
}
