package com.holmes.ponderosa.data.api.model;

import android.support.annotation.NonNull;

/**
 * Information to control events.
 */
public class HSEvent {
  @NonNull public final String Group;
  @NonNull public final String Name;
  @NonNull public final Integer id;

  public HSEvent(@NonNull String group, @NonNull String name, @NonNull Integer id) {
    this.Group = group;
    this.Name = name;
    this.id = id;
  }
}
