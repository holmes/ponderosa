package com.holmes.ponderosa.data.api.model;

import java.util.List;

public class HSDevicesResponse {
  public final List<HSDevice> Devices;

  public HSDevicesResponse(List<HSDevice> devices) {
    this.Devices = devices;
  }
}
