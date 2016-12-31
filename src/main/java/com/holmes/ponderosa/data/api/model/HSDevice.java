package com.holmes.ponderosa.data.api.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class HSDevice {
  @NonNull public final String ref;
  @NonNull public final String name;
  @NonNull public final String location;
  @NonNull public final String value;
  @NonNull public final String status;
  @Nullable public final String statusImage;

  public HSDevice(@NonNull String name, @NonNull String ref, @NonNull String location,
      @NonNull String value, @NonNull String status, String statusImage) {
    this.name = name;
    this.ref = ref;
    this.location = location;
    this.value = value;
    this.status = status;
    this.statusImage = statusImage;
  }
}
