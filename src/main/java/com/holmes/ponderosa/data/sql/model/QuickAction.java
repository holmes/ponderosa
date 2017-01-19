package com.holmes.ponderosa.data.sql.model;

import android.annotation.SuppressLint;
import com.google.auto.value.AutoValue;
import com.holmes.ponderosa.QuickActionModel;
import com.squareup.moshi.Moshi;
import com.squareup.sqldelight.RowMapper;

@AutoValue public abstract class QuickAction implements QuickActionModel {
  public static final Factory<QuickAction> FACTORY = new Factory<>(AutoValue_QuickAction::new);
  public static final RowMapper<QuickAction> SELECT_ALL_MAPPER = FACTORY.select_allMapper();

  private static final String EVENT_KEY = "Event:%d";
  private static final String DEVICE_KEY = "Device:%s:%d";

  @SuppressLint("DefaultLocale") //
  public static QuickAction createFrom(Moshi moshi, Event event) {
    String key = String.format(EVENT_KEY, event.id());
    QuickActionEvent quickActionEvent = new QuickActionEvent(event.name(), event.id());
    String blob = moshi.adapter(QuickActionEvent.class).toJson(quickActionEvent);
    return new AutoValue_QuickAction(-1, quickActionEvent.name, System.currentTimeMillis(), key, blob);
  }

  @SuppressLint("DefaultLocale") //
  public static QuickAction createFrom(Moshi moshi, Device device, DeviceControl deviceControl, Integer value) {
    String key = String.format(DEVICE_KEY, device.ref(), deviceControl._id());
    QuickActionDevice quickActionDevice = new QuickActionDevice(device.name(), device.ref(), value);
    String blob = moshi.adapter(QuickActionDevice.class).toJson(quickActionDevice);
    return new AutoValue_QuickAction(-1, quickActionDevice.name, System.currentTimeMillis(), key, blob);
  }

  private static class QuickActionEvent {
    final String name;
    final long id;

    private QuickActionEvent(String name, long id) {
      this.name = name;
      this.id = id;
    }
  }

  private static class QuickActionDevice {
    final String name;
    final String id;
    final long value;

    private QuickActionDevice(String name, String id, long value) {
      this.name = name;
      this.id = id;
      this.value = value;
    }
  }
}
