package com.holmes.ponderosa.data.api.model;

import java.util.List;

public class DevicesResponse {
  public final List<Device> Devices;

  public DevicesResponse(List<Device> devices) {
    this.Devices = devices;
  }
}
