package com.holmes.ponderosa.data.api.model;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * This is a weird API. If you request only one reference, you won't get this wrapper. If you
 * request more than one, this wrapper exists in the response.
 */
public class HSDeviceControlResponse {
  @NonNull public final List<HSDeviceControl> Devices;

  public HSDeviceControlResponse(@NonNull List<HSDeviceControl> devices) {
    Devices = devices;
  }
}
