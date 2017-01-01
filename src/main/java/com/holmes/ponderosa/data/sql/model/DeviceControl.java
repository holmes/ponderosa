package com.holmes.ponderosa.data.sql.model;

import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.holmes.ponderosa.DeviceControlModel;
import com.squareup.sqldelight.ColumnAdapter;
import com.squareup.sqldelight.RowMapper;

@AutoValue public abstract class DeviceControl implements DeviceControlModel {
  private static final ColumnAdapter<Type, Long> TYPE_ADAPTER = new ColumnAdapter<Type, Long>() {
    @NonNull @Override public Type decode(Long databaseValue) {
      return Type.fromValue(databaseValue.intValue());
    }

    @Override public Long encode(@NonNull Type value) {
      return (long) value.value;
    }
  };

  private static final ColumnAdapter<Use, Long> USE_ADAPTER = new ColumnAdapter<Use, Long>() {
    @NonNull @Override public Use decode(Long databaseValue) {
      return Use.fromValue(databaseValue.intValue());
    }

    @Override public Long encode(@NonNull Use value) {
      return (long) value.value;
    }
  };

  public static final Factory<DeviceControl> FACTORY = new Factory<DeviceControl>(new Creator<DeviceControl>() {
    @Override
    public DeviceControl create(long _id, @NonNull String device_ref, @NonNull String label, @NonNull Type type,
        @NonNull Use use) {
      return new AutoValue_DeviceControl(_id, device_ref, label, type, use);
    }
  }, TYPE_ADAPTER, USE_ADAPTER);

  public static final RowMapper<DeviceControl> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

  public enum Type {
    NOT_SPECIFIED(1),
    VALUES(2), // This is the default to use if one of the others is not specified.
    SINGLE_TEXT_FROM_LIST(3),
    LIST_TEXT_FROM_LIST(4),
    BUTTON(5),
    VALUES_RANGE(6), // Rendered as a drop-list by default.
    VALUES_RANGE_SLIDER(7),
    TEXT_LIST(8),
    TEXTBOX_NUMBER(9),
    TEXTBOX_STRING(10),
    RADIO_OPTION(11),
    BUTTON_SCRIPT(12), // Rendered as a button, executes a script when activated.
    COLOR_PICKER(13);

    int value;

    Type(Integer value) {
      this.value = value;
    }

    public static Type fromValue(int value) {
      return values()[value - 1];
    }
  }

  public enum Use {
    NOT_SPECIFIED(0),
    ON(1),
    OFF(2),
    DIM(3),
    ON_ALTERNATE(4),

    // MEDIA CONTROL DEVICES
    PLAY(5),
    PAUSE(6),
    STOP(7),
    FORWARD(8),
    REWIND(9),
    REPEAT(10),
    SHUFFLE(11);

    int value;

    Use(Integer value) {
      this.value = value;
    }

    public static Use fromValue(int value) {
      return values()[value];
    }
  }
}
