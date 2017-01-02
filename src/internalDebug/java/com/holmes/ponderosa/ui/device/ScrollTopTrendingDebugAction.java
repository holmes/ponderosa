package com.holmes.ponderosa.ui.device;

import com.holmes.ponderosa.ui.action.ActionsView;
import com.holmes.ponderosa.ui.debug.ContextualDebugActions.DebugAction;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public final class ScrollTopTrendingDebugAction extends DebugAction<ActionsView> {

  @Inject public ScrollTopTrendingDebugAction() {
    super(ActionsView.class);
  }

  @Override public String name() {
    return "Scroll to top";
  }

  @Override public void run(ActionsView view) {
    view.itemsView.smoothScrollToPosition(0);
  }
}
