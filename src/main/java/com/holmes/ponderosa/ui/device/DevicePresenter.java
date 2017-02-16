package com.holmes.ponderosa.ui.device;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import com.holmes.ponderosa.DeviceControlModel;
import com.holmes.ponderosa.DeviceModel;
import com.holmes.ponderosa.R;
import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import com.holmes.ponderosa.ui.action.ActionPerformer;
import com.holmes.ponderosa.ui.action.ActionPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

import static java.util.stream.Collectors.toList;

@Singleton public final class DevicePresenter implements ActionPresenter, DeviceAdapter.DeviceClickListener {
  private final ActionPerformer actionPerformer;
  private final Picasso picasso;
  private final Resources resources;
  private final DeviceAdapter deviceAdapter;

  private final Observable<List<Device>> devices;
  private final Observable<List<DeviceControl>> controls;
  private final Observable<List<String>> filters;

  @Inject
  public DevicePresenter(ActionPerformer actionPerformer, BriteDatabase db, Picasso picasso, Resources resources) {
    this.actionPerformer = actionPerformer;
    this.picasso = picasso;
    this.resources = resources;
    this.deviceAdapter = new DeviceAdapter(this.picasso, this);

    devices = RxJavaInterop.toV2Observable( //
        db.createQuery(DeviceModel.TABLE_NAME, DeviceModel.SELECT_ALL) //
            .mapToList(Device.SELECT_ALL_MAPPER::map)) //
        .subscribeOn(Schedulers.io()) //
        .observeOn(AndroidSchedulers.mainThread());

    controls = RxJavaInterop.toV2Observable( //
        db.createQuery(DeviceControlModel.TABLE_NAME, DeviceControlModel.SELECT_ALL) //
            .mapToList(DeviceControl.SELECT_ALL_MAPPER::map)) //
        .subscribeOn(Schedulers.io()) //
        .observeOn(AndroidSchedulers.mainThread());

    filters = devices.map((foundDevices) -> foundDevices.stream() //
        .filter(DeviceFilters.allowableDevices()).map(Device::location) //
        .distinct() //
        .collect(toList())) //
        .map(rooms -> {
          rooms.add(0, resources.getString(R.string.devices_filter_all));
          return rooms;
        });
  }

  @Override public Disposable loadData(Observable<String> selectedFilter, Consumer<Integer> countAction) {
    devices.subscribe(devices1 -> Timber.d("Found %d devices", devices1.size()));

    Disposable adapterSubscription = Observable.combineLatest(devices, selectedFilter, //
        (devices1, location) -> devices1.stream() //
            .filter(device -> { //
              String allTitle = resources.getString(R.string.devices_filter_all); //
              return Objects.equals(location, allTitle) || device.location().equals(location);
            }) //
            .collect(Collectors.toList())) //
        .subscribe(deviceAdapter::updateDevices);

    CompositeDisposable subscriptions = new CompositeDisposable();
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

  @Override public void accept(DeviceWithControls deviceWithControls) {
    DeviceWithControls.ControlResult controlResult = deviceWithControls.toggleDevice();
    actionPerformer.runDeviceControl(controlResult);
  }
}
