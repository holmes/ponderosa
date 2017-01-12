package com.holmes.ponderosa.ui.action;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.DataFetcher;
import com.holmes.ponderosa.data.Injector;
import com.holmes.ponderosa.data.IntentFactory;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.ui.device.DevicePresenter;
import com.holmes.ponderosa.ui.event.EventPresenter;
import com.holmes.ponderosa.ui.misc.BetterViewAnimator;
import com.holmes.ponderosa.ui.misc.DividerItemDecoration;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import javax.inject.Inject;

public final class ActionsView extends CoordinatorLayout implements SwipeRefreshLayout.OnRefreshListener {
  public static String TAG = "ACTIONS_VIEW_TAG";

  @BindView(R.id.actions_toolbar) Toolbar toolbarView;
  @BindView(R.id.action_title) TextView actionTitle;
  @BindView(R.id.action_filter) Spinner filterView;
  @BindView(R.id.actions_animator) BetterViewAnimator animatorView;
  @BindView(R.id.actions_swipe_refresh) SwipeRefreshLayout swipeRefreshView;
  @BindView(R.id.actions_list) public RecyclerView itemsView;
  @BindView(R.id.actions_loading_message) TextView loadingMessageView;

  @BindView(R.id.actions_fab) FloatingActionButton floatingActionButton;
  //@BindView(R.id.fab_actions_menu) LinearLayout floatingActionMenu;

  @BindDimen(R.dimen.trending_divider_padding_start) float dividerPaddingStart;

  @Inject BriteDatabase db;
  @Inject DataFetcher dataFetcher;
  @Inject Picasso picasso;
  @Inject HomeSeerService homeSeerService;
  @Inject IntentFactory intentFactory;
  @Inject DrawerLayout drawerLayout;

  @Inject DevicePresenter devicePresenter;
  @Inject EventPresenter eventPresenter;
  private ActionPresenter currentPresenter;
  @NonNull private CompositeDisposable subscriptions = new CompositeDisposable();

  private final PublishSubject<String> filterSubject;
  private final FilterAdapter filterAdapter;

  private Consumer<Integer> updateViewAction = count -> {
    animatorView.setDisplayedChildId(count == 0 //
        ? R.id.actions_empty //
        : R.id.actions_swipe_refresh);
    swipeRefreshView.setRefreshing(false);
  };

  public enum PresenterType {
    DEVICES, EVENTS
  }

  public ActionsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (!isInEditMode()) {
      Injector.obtain(context).inject(this);
    }

    setTag(TAG);

    filterSubject = PublishSubject.create();
    filterAdapter = new FilterAdapter(new ContextThemeWrapper(getContext(), R.style.Theme_U2020_TrendingTimespan));
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);

    AnimationDrawable ellipsis =
        (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.dancing_ellipsis);
    loadingMessageView.setCompoundDrawablesWithIntrinsicBounds(null, null, ellipsis, null);
    ellipsis.start();

    toolbarView.setNavigationIcon(R.drawable.menu_icon);
    toolbarView.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

    filterView.setAdapter(filterAdapter);
    filterView.setSelection(0);

    swipeRefreshView.setColorSchemeResources(R.color.accent);
    swipeRefreshView.setOnRefreshListener(this);

    itemsView.setLayoutManager(new LinearLayoutManager(getContext()));
    itemsView.addItemDecoration(
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, dividerPaddingStart, safeIsRtl()));
  }

  public void loadPresenter(PresenterType type) {
    subscriptions.dispose();
    subscriptions = new CompositeDisposable();

    switch (type) {
      case DEVICES:
        currentPresenter = devicePresenter;
        break;
      case EVENTS:
        currentPresenter = eventPresenter;
        break;
    }

    actionTitle.setText(currentPresenter.getActionTitle());
    itemsView.setAdapter(currentPresenter.getAdapter());

    subscriptions.add(currentPresenter.loadData(filterSubject.startWith("All"), updateViewAction));
    subscriptions.add(currentPresenter.getFilterOptions().subscribe(filterAdapter::update));
  }

  //private final Action1<Result<HSDevicesResponse>> trendingError = new Action1<Result<HSDevicesResponse>>() {
  //  @Override public void call(Result<HSDevicesResponse> result) {
  //    if (result.isError()) {
  //      Timber.e(result.error(), "Failed to get trending repositories");
  //    } else {
  //      Response<HSDevicesResponse> response = result.response();
  //      Timber.e("Failed to get trending repositories. Server returned %d", response.code());
  //    }
  //    swipeRefreshView.setRefreshing(false);
  //    animatorView.setDisplayedChildId(R.id.trending_error);
  //  }
  //};

  @Override public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    subscriptions.dispose();
  }

  @OnClick(R.id.actions_fab)
  void onFABTapped() {
    //floatingActionMenu.setVisibility(VISIBLE);
  }

  @OnItemSelected(R.id.action_filter)
  void onFilterSelected(final int position) {
    if (animatorView.getDisplayedChildId() != R.id.actions_swipe_refresh) {
      animatorView.setDisplayedChildId(R.id.actions_loading);
    }

    // For whatever reason, the SRL's spinner does not draw itself when we call setRefreshing(true)
    // unless it is posted.
    post(() -> {
      swipeRefreshView.setRefreshing(true);
      dataFetcher.refresh();
      filterSubject.onNext(filterAdapter.getItem(position));
    });
  }

  @Override public void onRefresh() {
    onFilterSelected(filterView.getSelectedItemPosition());
  }

  private boolean safeIsRtl() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isRtl();
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) private boolean isRtl() {
    return getLayoutDirection() == LAYOUT_DIRECTION_RTL;
  }
}
