package com.holmes.ponderosa.data.api.model;

import android.support.annotation.NonNull;

/**
 * This is a weird API. If you request only one reference, you won't get this wrapper. If you
 * request more than one, this wrapper exists in the response.
 */
public class DeviceControlResponse {
  @NonNull public final DeviceControl Devices;

  public DeviceControlResponse(@NonNull DeviceControl devices) {
    Devices = devices;
  }
}
