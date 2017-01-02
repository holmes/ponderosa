package com.holmes.ponderosa.ui.device;

import com.holmes.ponderosa.ui.action.ActionsView;
import com.holmes.ponderosa.ui.debug.ContextualDebugActions.DebugAction;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public final class ScrollBottomTrendingDebugAction extends DebugAction<ActionsView> {

  @Inject public ScrollBottomTrendingDebugAction() {
    super(ActionsView.class);
  }

  @Override public String name() {
    return "Scroll to bottom";
  }

  @Override public void run(ActionsView view) {
    view.itemsView.smoothScrollToPosition(view.itemsView.getAdapter().getItemCount() - 1);
  }
}
