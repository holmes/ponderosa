package com.holmes.ponderosa.data.api.model;

import android.support.annotation.NonNull;

public class HSDevice {
  @NonNull public final String ref;
  @NonNull public final String name;
  @NonNull public final String location;
  @NonNull public final Integer value;
  @NonNull public final HSDeviceInfo device_type;
  @NonNull public final String status;
  @NonNull public final String status_image;

  public HSDevice(@NonNull String name, @NonNull String ref, @NonNull String location, @NonNull Integer value,
      @NonNull HSDeviceInfo device_type, @NonNull String status, @NonNull  String status_image) {
    this.name = name;
    this.ref = ref;
    this.location = location;
    this.value = value;
    this.device_type = device_type;
    this.status = status;
    this.status_image = status_image;
  }

  public static class HSDeviceInfo {
    public final Integer Device_API;
    public final Integer Device_SubType;

    public HSDeviceInfo(Integer device_api, Integer device_subType) {
      Device_API = device_api;
      Device_SubType = device_subType;
    }
  }
}
