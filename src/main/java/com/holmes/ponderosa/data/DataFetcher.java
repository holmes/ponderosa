package com.holmes.ponderosa.data;

import com.holmes.ponderosa.DeviceControlModel;
import com.holmes.ponderosa.DeviceModel;
import com.holmes.ponderosa.EventModel;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.api.Results;
import com.holmes.ponderosa.data.api.model.HSDevice;
import com.holmes.ponderosa.data.api.model.HSDeviceControl;
import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import com.holmes.ponderosa.data.sql.model.DeviceControl.Type;
import com.holmes.ponderosa.data.sql.model.DeviceControl.Use;
import com.squareup.sqlbrite.BriteDatabase;
import java.util.List;
import java.util.stream.Collectors;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class DataFetcher {
  private final BriteDatabase db;
  private final HomeSeerService homeSeerService;

  public DataFetcher(BriteDatabase db, HomeSeerService homeSeerService) {
    this.db = db;
    this.homeSeerService = homeSeerService;
  }

  public void refresh() {
    ConnectableObservable<List<HSDevice>> devices = homeSeerService.devices()
        .filter(Results.isSuccessful())
        .map(devicesResponseResult -> devicesResponseResult.response().body())
        .subscribeOn(Schedulers.io())
        .map(devicesResponse -> devicesResponse.Devices)
        .publish();

    devices.forEach(deviceList -> {
      try (BriteDatabase.Transaction transaction = db.newTransaction()) {
        deviceList.forEach(device -> {
          DeviceModel.Insert_row insertRow = new DeviceModel.Insert_row(db.getWritableDatabase(), Device.FACTORY);
          Device.Type type = Device.Type.fromValue(device.device_type.Device_API);
          insertRow.bind(device.ref, device.name, device.location, device.value, type,
              device.device_type.Device_SubType, device.status, device.status_image);
          db.executeInsert(DeviceModel.TABLE_NAME, insertRow.program);
        });
        transaction.markSuccessful();
      }
    });

    // Lookup device control information.
    Observable<List<HSDeviceControl>> deviceControlsResponse =
        devices.map(deviceList -> deviceList.stream().map(device -> device.ref).collect(Collectors.toList())) //
            .flatMap(homeSeerService::deviceControls)
            .filter(Results.isSuccessful())
            .map(deviceControlResponseResult -> deviceControlResponseResult.response().body().Devices);

    deviceControlsResponse.forEach(deviceControlResponse -> {
      try (BriteDatabase.Transaction transaction = db.newTransaction()) {
        deviceControlResponse.forEach(deviceControl -> deviceControl.ControlPairs.forEach(option -> {
          DeviceControlModel.Insert_row insertRow =
              new DeviceControlModel.Insert_row(db.getWritableDatabase(), DeviceControl.FACTORY);

          insertRow.bind(deviceControl.ref, option.Label, Type.fromValue(option.ControlType),
              Use.fromValue(option.ControlUse));

          db.executeInsert(DeviceControlModel.TABLE_NAME, insertRow.program);
        }));
        transaction.markSuccessful();
      }
    });

    devices.connect();

    homeSeerService.events()
        .subscribeOn(Schedulers.io())
        .filter(Results.isSuccessful())
        .map(hsEventsResponseResult -> hsEventsResponseResult.response().body().Events)
        .forEach(hsEvents -> {
          try (BriteDatabase.Transaction transaction = db.newTransaction()) {
            hsEvents.forEach(hsEvent -> {
              EventModel.Insert_row insertRow = new EventModel.Insert_row(db.getWritableDatabase());
              insertRow.bind(hsEvent.id, hsEvent.Group, hsEvent.Name);
              db.executeInsert(EventModel.TABLE_NAME, insertRow.program);
            });
            transaction.markSuccessful();
          }
        });
  }
}
