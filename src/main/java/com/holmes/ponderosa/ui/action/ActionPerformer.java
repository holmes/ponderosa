package com.holmes.ponderosa.ui.action;

import com.holmes.ponderosa.data.DataFetcher;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.sql.model.Event;
import com.holmes.ponderosa.data.sql.model.QuickAction;
import com.holmes.ponderosa.ui.device.DeviceWithControls;
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

  @Inject
  public ActionPerformer(BriteDatabase db, DataFetcher dataFetcher, HomeSeerService homeSeerService, Moshi moshi) {
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

  public void runDeviceControl(DeviceWithControls.ControlResult controlResult) {
    homeSeerService.controlDevice(controlResult.device.ref(), controlResult.value)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(hsDevicesResponseResult -> dataFetcher.refresh());

    updateQuickAction(QuickAction.createFrom(moshi, controlResult));
  }

  public void runQuickAction(QuickAction quickAction) {
    //noinspection unchecked
    quickAction.performAction(homeSeerService, moshi)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(result -> dataFetcher.refresh());

    updateQuickAction(quickAction);
  }

  private void updateQuickAction(QuickAction quickAction) {
    QuickAction.Insert_row insertRow = new QuickAction.Insert_row(db.getWritableDatabase());
    insertRow.bind(quickAction.name(), quickAction.last_ran(), quickAction.action_key(), quickAction.action_blob());
    db.executeInsert(QuickAction.TABLE_NAME, insertRow.program);
  }
}
