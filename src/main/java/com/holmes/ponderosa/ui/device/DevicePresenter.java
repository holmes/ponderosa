package com.holmes.ponderosa.ui.device;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import com.holmes.ponderosa.DeviceControlModel;
import com.holmes.ponderosa.DeviceModel;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.DataFetcher;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import com.holmes.ponderosa.ui.action.ActionPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static java.util.stream.Collectors.toList;

@Singleton public final class DevicePresenter implements ActionPresenter, DeviceAdapter.DeviceClickListener {
  private final DataFetcher dataFetcher;
  private final Picasso picasso;
  private final Resources resources;
  private final HomeSeerService homeSeerService;
  private final DeviceAdapter deviceAdapter;

  private final Observable<List<Device>> devices;
  private final Observable<List<DeviceControl>> controls;
  private final Observable<List<String>> filters;

  @Inject public DevicePresenter(BriteDatabase db, DataFetcher dataFetcher, Picasso picasso, Resources resources,
      HomeSeerService homeSeerService) {
    this.dataFetcher = dataFetcher;
    this.picasso = picasso;
    this.resources = resources;
    this.homeSeerService = homeSeerService;
    this.deviceAdapter = new DeviceAdapter(this.picasso, this);

    devices = db.createQuery(DeviceModel.TABLE_NAME, DeviceModel.SELECT_ALL) //
        .mapToList(Device.SELECT_ALL_MAPPER::map) //
        .subscribeOn(Schedulers.io()) //
        .observeOn(AndroidSchedulers.mainThread());

    controls = db.createQuery(DeviceControlModel.TABLE_NAME, DeviceControlModel.SELECT_ALL) //
        .mapToList(DeviceControl.SELECT_ALL_MAPPER::map) //
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    filters = devices.map((foundDevices) -> foundDevices.stream() //
        .filter(DeviceFilters.allowableDevices()).map(Device::location) //
        .distinct() //
        .collect(toList())) //
        .map(rooms -> {
          rooms.add(0, resources.getString(R.string.devices_filter_all));
          return rooms;
        });
  }

  @Override public Subscription loadData(Observable<String> selectedFilter, Action1<Integer> countAction) {
    Subscription adapterSubscription = Observable.combineLatest(devices, selectedFilter, //
        (devices1, location) -> devices1.stream() //
            .filter(device -> { //
              String allTitle = resources.getString(R.string.devices_filter_all); //
              return Objects.equals(location, allTitle) || device.location().equals(location);
            }) //
            .collect(Collectors.toList())) //
        .subscribe(deviceAdapter::updateDevices);

    CompositeSubscription subscriptions = new CompositeSubscription();
    subscriptions.add(adapterSubscription);
    subscriptions.add(controls.subscribe(deviceAdapter::updateControls));
    subscriptions.add(devices.map(List::size).subscribe(countAction));

    return subscriptions;
  }

  @Override public Observable<List<String>> getFilterOptions() {
    return filters;
  }

  @Override public int getActionTitle() {
    return R.string.action_title_device;
  }

  @Override public RecyclerView.Adapter getAdapter() {
    return deviceAdapter;
  }

  @Override public void onDeviceTapped(Device device) {
    // TODO move this somewhere else and look at the actual controls for values.
    int newValue = device.value() > 0 ? 0 : 255;
    homeSeerService.controlDevice(device.ref(), newValue)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(hsDevicesResponseResult -> dataFetcher.refresh());
  }
}
