package com.holmes.ponderosa.ui.device;

import com.holmes.ponderosa.ui.debug.ContextualDebugActions.DebugAction;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public final class ScrollBottomTrendingDebugAction extends DebugAction<DevicesView> {

  @Inject public ScrollBottomTrendingDebugAction() {
    super(DevicesView.class);
  }

  @Override public String name() {
    return "Scroll to bottom";
  }

  @Override public void run(DevicesView view) {
    view.trendingView.smoothScrollToPosition(view.trendingView.getAdapter().getItemCount() - 1);
  }
}
