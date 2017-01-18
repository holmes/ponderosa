package com.holmes.ponderosa.ui.action;

import com.holmes.ponderosa.data.DataFetcher;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import com.holmes.ponderosa.data.sql.model.Event;
import com.holmes.ponderosa.data.sql.model.QuickAction;
import com.squareup.moshi.Moshi;
import com.squareup.sqlbrite.BriteDatabase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ActionPerformer {
  private final BriteDatabase db;
  private final DataFetcher dataFetcher;
  private final HomeSeerService homeSeerService;
  private final Moshi moshi;

  @Inject public ActionPerformer(BriteDatabase db, DataFetcher dataFetcher, HomeSeerService homeSeerService,
      Moshi moshi) {
    this.db = db;
    this.dataFetcher = dataFetcher;
    this.homeSeerService = homeSeerService;
    this.moshi = moshi;
  }

  public void runEvent(Event event) {
    homeSeerService.runEvent(event.id())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(hsDevicesResponseResult -> dataFetcher.refresh());

    updateQuickAction(QuickAction.createFrom(moshi, event));
  }

  public void runDeviceControl(Device device, DeviceControl deviceControl) {
    // TODO move this somewhere else and look at the actual controls for values.
    int newValue = device.value() > 0 ? 0 : 255;
    homeSeerService.controlDevice(device.ref(), newValue)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(hsDevicesResponseResult -> dataFetcher.refresh());

    updateQuickAction(QuickAction.createFrom(moshi, device, deviceControl, newValue));
  }

  private void updateQuickAction(QuickAction quickAction) {
    QuickAction.Insert_row insertRow = new QuickAction.Insert_row(db.getWritableDatabase());
    insertRow.bind(quickAction.last_ran(), quickAction.action_key(), quickAction.action_blob());
    db.executeInsert(QuickAction.TABLE_NAME, insertRow.program);
  }
}
