package com.holmes.ponderosa.ui.trending;

import com.holmes.ponderosa.ui.debug.ContextualDebugActions.DebugAction;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public final class ScrollTopTrendingDebugAction extends DebugAction<DevicesView> {

  @Inject public ScrollTopTrendingDebugAction() {
    super(DevicesView.class);
  }

  @Override public String name() {
    return "Scroll to top";
  }

  @Override public void run(DevicesView view) {
    view.trendingView.smoothScrollToPosition(0);
  }
}
