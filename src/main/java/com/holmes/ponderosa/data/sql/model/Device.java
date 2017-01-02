package com.holmes.ponderosa.data.sql.model;

import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.holmes.ponderosa.DeviceModel;
import com.squareup.sqldelight.ColumnAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue public abstract class Device implements DeviceModel {
  private static final ColumnAdapter<Device.Type, Long> TYPE_ADAPTER = new ColumnAdapter<Device.Type, Long>() {
    @NonNull @Override public Device.Type decode(Long databaseValue) {
      return Device.Type.fromValue(databaseValue.intValue());
    }

    @Override public Long encode(@NonNull Device.Type value) {
      return (long) value.value;
    }
  };

  public static final Factory<Device> FACTORY = new Factory<>(AutoValue_Device::new, TYPE_ADAPTER);
  public static final RowMapper<Device> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

  public enum Type {
    NO_API(0), //	All other devices.
    PLUG_IN(4), // Device is owned/managed by a plug-in.
    SECURITY(8), //	Device is owned/managed by a plug-in and is a security device.
    THERMOSTAT(16), // Device is owned/managed by a plug-in and is a thermostat device.
    MEDIA(32), //	Device is owned/managed by a plug-in and is a media player device.
    SOURCE_SWITCH(64), // Device is owned/managed by a plug-in and is a matrix switch device.
    SCRIPT(128); // Device launches a script when the value and/or string changes.

    int value;

    Type(Integer value) {
      this.value = value;
    }

    public static Device.Type fromValue(int value) {
      for (Type type : values()) {
        if (type.value == value) {
          return type;
        }
      }

      throw new IllegalArgumentException("Unknown value: " + value);
    }
  }
}
