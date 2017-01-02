package com.holmes.ponderosa.ui.device;

import android.support.v7.widget.RecyclerView;
import com.holmes.ponderosa.DeviceControlModel;
import com.holmes.ponderosa.DeviceModel;
import com.holmes.ponderosa.data.DataFetcher;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import com.holmes.ponderosa.ui.action.ActionPresenter;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

@Singleton
public final class DevicePresenter implements ActionPresenter, DeviceAdapter.DeviceClickListener {
  private final BriteDatabase db;
  private final DataFetcher dataFetcher;
  private final Picasso picasso;
  private final HomeSeerService homeSeerService;
  private final DeviceAdapter deviceAdapter;

  @Inject
  public DevicePresenter(BriteDatabase db, DataFetcher dataFetcher, Picasso picasso, HomeSeerService homeSeerService) {
    this.db = db;
    this.dataFetcher = dataFetcher;
    this.picasso = picasso;
    this.homeSeerService = homeSeerService;
    this.deviceAdapter = new DeviceAdapter(this.picasso, this);
  }

  @Override public Subscription loadData(Action1<Integer> countAction) {
    Observable<List<Device>> devices =
        db.createQuery(DeviceModel.TABLE_NAME, DeviceModel.SELECT_ALL).mapToList(Device.SELECT_ALL_MAPPER::map) //
            .observeOn(AndroidSchedulers.mainThread());

    Observable<List<DeviceControl>> controls =
        db.createQuery(DeviceControlModel.TABLE_NAME, DeviceControlModel.SELECT_ALL)
            .mapToList(DeviceControl.SELECT_ALL_MAPPER::map) //
            .observeOn(AndroidSchedulers.mainThread());

    CompositeSubscription subscriptions = new CompositeSubscription();
    subscriptions.add(devices.subscribe(deviceAdapter::updateDevices));
    subscriptions.add(controls.subscribe(deviceAdapter::updateControls));
    subscriptions.add(devices.map(List::size).subscribe(countAction));

    return subscriptions;
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
