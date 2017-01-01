package com.holmes.ponderosa.data.api.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class HSDevice {
  @NonNull public final String ref;
  @NonNull public final String name;
  @NonNull public final String location;
  @NonNull public final Integer value;
  @NonNull public final String status;
  @Nullable public final String status_image;

  public HSDevice(@NonNull String name, @NonNull String ref, @NonNull String location,
      @NonNull Integer value, @NonNull String status, String status_image) {
    this.name = name;
    this.ref = ref;
    this.location = location;
    this.value = value;
    this.status = status;
    this.status_image = status_image;
  }
}
