package com.holmes.ponderosa.data.api.model;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Information to control a device.
 */
public class HSDeviceControl {
  @NonNull public final String ref;
  @NonNull public final String name;
  @NonNull public final String location;
  @NonNull public final List<Option> ControlPairs;

  public HSDeviceControl(
      @NonNull String ref, @NonNull String name, @NonNull String location, @NonNull List<Option> controlPairs) {
    this.ref = ref;
    this.name = name;
    this.location = location;
    this.ControlPairs = controlPairs;
  }

  public static class Option {
    @NonNull public final String Label;
    @NonNull public final int ControlType;
    @NonNull public final int ControlUse;

    public Option(@NonNull String label, @NonNull int controlType, @NonNull int controlUse) {
      Label = label;
      ControlType = controlType;
      ControlUse = controlUse;
    }
  }
}
