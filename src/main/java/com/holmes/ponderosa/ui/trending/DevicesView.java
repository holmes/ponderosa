package com.holmes.ponderosa.ui.trending;

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
import android.widget.Toast;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.Funcs;
import com.holmes.ponderosa.data.Injector;
import com.holmes.ponderosa.data.IntentFactory;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.api.Results;
import com.holmes.ponderosa.data.api.model.Device;
import com.holmes.ponderosa.data.api.model.DevicesResponse;
import com.holmes.ponderosa.data.api.transforms.DevicesResponseToDeviceList;
import com.holmes.ponderosa.ui.misc.BetterViewAnimator;
import com.holmes.ponderosa.ui.misc.DividerItemDecoration;
import com.holmes.ponderosa.ui.misc.EnumAdapter;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;
import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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

  @Inject HomeSeerService homeSeerService;
  @Inject Picasso picasso;
  @Inject IntentFactory intentFactory;
  @Inject DrawerLayout drawerLayout;

  private final PublishSubject<TrendingTimespan> timespanSubject;
  private final EnumAdapter<TrendingTimespan> timespanAdapter;
  private final DeviceAdapter deviceAdapter;
  private final CompositeSubscription subscriptions = new CompositeSubscription();

  public DevicesView(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (!isInEditMode()) {
      Injector.obtain(context).inject(this);
    }

    timespanSubject = PublishSubject.create();
    timespanAdapter = new TrendingTimespanAdapter(
        new ContextThemeWrapper(getContext(), R.style.Theme_U2020_TrendingTimespan));
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

    deviceAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override public void onChanged() {
        animatorView.setDisplayedChildId(deviceAdapter.getItemCount() == 0 //
            ? R.id.trending_empty //
            : R.id.trending_swipe_refresh);
        swipeRefreshView.setRefreshing(false);
      }
    });

    trendingView.setLayoutManager(new LinearLayoutManager(getContext()));
    trendingView.addItemDecoration(
        new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST,
            dividerPaddingStart, safeIsRtl()));
    trendingView.setAdapter(deviceAdapter);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    Observable<Result<DevicesResponse>> result = timespanSubject //
        .flatMap(deviceSearch) //
        .observeOn(AndroidSchedulers.mainThread()) //
        .share();

    subscriptions.add(result //
        .filter(Results.isSuccessful()) //
        .map(DevicesResponseToDeviceList.instance()) //
        .subscribe(deviceAdapter));

    subscriptions.add(result //
        .filter(Funcs.not(Results.isSuccessful())) //
        .subscribe(trendingError));

    // Load the default selection.
    onRefresh();
  }

  private final Func1<TrendingTimespan, Observable<Result<DevicesResponse>>> deviceSearch =
      new Func1<TrendingTimespan, Observable<Result<DevicesResponse>>>() {
        @Override
        public Observable<Result<DevicesResponse>> call(TrendingTimespan trendingTimespan) {
          return homeSeerService
              .devices()
              .subscribeOn(Schedulers.io());
        }
      };

  private final Action1<Result<DevicesResponse>> trendingError =
      new Action1<Result<DevicesResponse>>() {
        @Override public void call(Result<DevicesResponse> result) {
          if (result.isError()) {
            Timber.e(result.error(), "Failed to get trending repositories");
          } else {
            Response<DevicesResponse> response = result.response();
            Timber.e("Failed to get trending repositories. Server returned %d", response.code());
          }
          swipeRefreshView.setRefreshing(false);
          animatorView.setDisplayedChildId(R.id.trending_error);
        }
      };

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    subscriptions.unsubscribe();
  }

  @OnItemSelected(R.id.trending_timespan) void timespanSelected(final int position) {
    if (animatorView.getDisplayedChildId() != R.id.trending_swipe_refresh) {
      animatorView.setDisplayedChildId(R.id.trending_loading);
    }

    // For whatever reason, the SRL's spinner does not draw itself when we call setRefreshing(true)
    // unless it is posted.
    post(() -> {
      swipeRefreshView.setRefreshing(true);
      timespanSubject.onNext(timespanAdapter.getItem(position));
    });
  }

  @Override public void onRefresh() {
    timespanSelected(timespanView.getSelectedItemPosition());
  }

  @Override public void onDeviceTapped(Device device) {
    Toast.makeText(getContext(), "tapped on " + device.name, Toast.LENGTH_LONG).show();
    //Intents.maybeStartActivity(getContext(), intentFactory.createUrlIntent(device.html_url));
  }

  private boolean safeIsRtl() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isRtl();
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) private boolean isRtl() {
    return getLayoutDirection() == LAYOUT_DIRECTION_RTL;
  }
}
