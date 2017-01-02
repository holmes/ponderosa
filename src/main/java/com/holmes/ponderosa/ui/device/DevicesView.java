package com.holmes.ponderosa.ui.device;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.holmes.ponderosa.DeviceControlModel;
import com.holmes.ponderosa.DeviceModel;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.DataFetcher;
import com.holmes.ponderosa.data.Injector;
import com.holmes.ponderosa.data.IntentFactory;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import com.holmes.ponderosa.ui.misc.BetterViewAnimator;
import com.holmes.ponderosa.ui.misc.DividerItemDecoration;
import com.holmes.ponderosa.ui.misc.EnumAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public final class DevicesView extends LinearLayout
    implements SwipeRefreshLayout.OnRefreshListener, DeviceAdapter.DeviceClickListener {
  @BindView(R.id.trending_toolbar) Toolbar toolbarView;
  @BindView(R.id.trending_timespan) Spinner timespanView;
  @BindView(R.id.trending_animator) BetterViewAnimator animatorView;
  @BindView(R.id.trending_swipe_refresh) SwipeRefreshLayout swipeRefreshView;
  @BindView(R.id.trending_list) RecyclerView trendingView;
  @BindView(R.id.trending_loading_message) TextView loadingMessageView;

  @BindDimen(R.dimen.trending_divider_padding_start) float dividerPaddingStart;

  @Inject BriteDatabase db;
  @Inject DataFetcher dataFetcher;
  @Inject Picasso picasso;
  @Inject HomeSeerService homeSeerService;
  @Inject IntentFactory intentFactory;
  @Inject DrawerLayout drawerLayout;

  private final PublishSubject<TrendingTimespan> timespanSubject;
  private final EnumAdapter<TrendingTimespan> timespanAdapter;
  private final DeviceAdapter deviceAdapter;
  private final CompositeSubscription subscriptions = new CompositeSubscription();

  private Action1<List<Device>> updateViewAction = devices -> {
    Timber.d("why the fuxk doesn't this work?");
    animatorView.setDisplayedChildId(devices.isEmpty() //
        ? R.id.trending_empty //
        : R.id.trending_swipe_refresh);
    swipeRefreshView.setRefreshing(false);
  };

  public DevicesView(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (!isInEditMode()) {
      Injector.obtain(context).inject(this);
    }

    timespanSubject = PublishSubject.create();
    timespanAdapter =
        new TrendingTimespanAdapter(new ContextThemeWrapper(getContext(), R.style.Theme_U2020_TrendingTimespan));
    deviceAdapter = new DeviceAdapter(picasso, this);
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

    timespanView.setAdapter(timespanAdapter);
    timespanView.setSelection(TrendingTimespan.WEEK.ordinal());

    swipeRefreshView.setColorSchemeResources(R.color.accent);
    swipeRefreshView.setOnRefreshListener(this);

    trendingView.setLayoutManager(new LinearLayoutManager(getContext()));
    trendingView.addItemDecoration(
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, dividerPaddingStart, safeIsRtl()));
    trendingView.setAdapter(deviceAdapter);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    Observable<List<Device>> devices =
        db.createQuery(DeviceModel.TABLE_NAME, DeviceModel.SELECT_ALL).mapToList(Device.SELECT_ALL_MAPPER::map) //
            .observeOn(AndroidSchedulers.mainThread());

    Observable<List<DeviceControl>> controls =
        db.createQuery(DeviceControlModel.TABLE_NAME, DeviceControlModel.SELECT_ALL)
            .mapToList(DeviceControl.SELECT_ALL_MAPPER::map) //
            .observeOn(AndroidSchedulers.mainThread());

    subscriptions.add(devices.subscribe(updateViewAction));
    subscriptions.add(devices.subscribe(deviceAdapter::updateDevices));
    subscriptions.add(controls.subscribe(deviceAdapter::updateControls));

    // Load the default selection.
    //onRefresh();
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

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    subscriptions.unsubscribe();
  }

  //@OnItemSelected(R.id.trending_timespan)
  void timespanSelected(final int position) {
    if (animatorView.getDisplayedChildId() != R.id.trending_swipe_refresh) {
      animatorView.setDisplayedChildId(R.id.trending_loading);
    }

    // For whatever reason, the SRL's spinner does not draw itself when we call setRefreshing(true)
    // unless it is posted.
    post(() -> {
      Timber.d("Setting refresh to true");
      swipeRefreshView.setRefreshing(true);
      dataFetcher.refresh();
      //timespanSubject.onNext(timespanAdapter.getItem(position));
    });
  }

  @Override public void onRefresh() {
    timespanSelected(timespanView.getSelectedItemPosition());
  }

  @Override public void onDeviceTapped(Device device) {
    int newValue = device.value() > 0 ? 0 : 255;
    homeSeerService.controlDevice(device.ref(), newValue)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(hsDevicesResponseResult -> dataFetcher.refresh());
  }

  private boolean safeIsRtl() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isRtl();
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) private boolean isRtl() {
    return getLayoutDirection() == LAYOUT_DIRECTION_RTL;
  }
}
