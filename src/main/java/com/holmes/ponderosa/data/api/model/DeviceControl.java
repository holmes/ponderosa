package com.holmes.ponderosa.data.api.model;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Information to control a device.
 */
public class DeviceControl {
  @NonNull public final String ref;
  @NonNull public final String name;
  @NonNull public final String location;
  @NonNull public final List<Option> ControlPairs;

  public DeviceControl(
      @NonNull String ref, @NonNull String name, @NonNull String location, @NonNull List<Option> controlPairs) {
    this.ref = ref;
    this.name = name;
    this.location = location;
    ControlPairs = controlPairs;
  }

  public static class Option {
    @NonNull public final String label;
    @NonNull public final int ControlType;
    @NonNull public final int ControlUse;

    public Option(@NonNull String label, @NonNull int controlType, @NonNull int controlUse) {
      this.label = label;
      ControlType = controlType;
      ControlUse = controlUse;
    }
  }
}
