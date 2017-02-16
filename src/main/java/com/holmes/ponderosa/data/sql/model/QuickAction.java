package com.holmes.ponderosa.data.sql.model;

import android.annotation.SuppressLint;
import com.google.auto.value.AutoValue;
import com.holmes.ponderosa.QuickActionModel;
import com.holmes.ponderosa.data.api.HomeSeerService;
import com.holmes.ponderosa.data.api.model.HSDevicesResponse;
import com.holmes.ponderosa.data.api.model.HSEventsResponse;
import com.holmes.ponderosa.ui.device.DeviceWithControls.ControlResult;
import com.jakewharton.retrofit2.adapter.rxjava2.Result;
import com.squareup.moshi.Moshi;
import com.squareup.sqldelight.RowMapper;
import io.reactivex.Observable;
import java.io.IOException;
import timber.log.Timber;

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
  public static QuickAction createFrom(Moshi moshi, ControlResult controlResult) {
    Device device = controlResult.device;
    String key = String.format(DEVICE_KEY, device.ref(), controlResult.selectedUse.ordinal());

    QuickActionDevice quickActionDevice = new QuickActionDevice(device.name(), device.ref(), controlResult.value);
    String blob = moshi.adapter(QuickActionDevice.class).toJson(quickActionDevice);

    return new AutoValue_QuickAction(-1, quickActionDevice.name, System.currentTimeMillis(), key, blob);
  }

  public Observable performAction(HomeSeerService homeSeerService, Moshi moshi) {
    return asBlob(moshi).perform(homeSeerService);
  }

  private QuickActionBlob asBlob(Moshi moshi) {
    try {
      if (action_key().startsWith("Event")) {
        return moshi.adapter(QuickActionEvent.class).fromJson(action_blob());
      } else {
        return moshi.adapter(QuickActionDevice.class).fromJson(action_blob());
      }
    } catch (IOException e) {
      Timber.wtf("Couldn't convert the blob: %s", action_blob());
      throw new RuntimeException(e);
    }
  }

  private interface QuickActionBlob<T> {
    Observable<Result<T>> perform(HomeSeerService homeSeerService);
  }

  private static class QuickActionEvent implements QuickActionBlob<HSEventsResponse> {
    final String name;
    final long id;

    private QuickActionEvent(String name, long id) {
      this.name = name;
      this.id = id;
    }

    @Override public Observable<Result<HSEventsResponse>> perform(HomeSeerService homeSeerService) {
      return homeSeerService.runEvent(id);
    }
  }

  private static class QuickActionDevice implements QuickActionBlob<HSDevicesResponse> {
    final String name;
    final String id;
    final long value;

    private QuickActionDevice(String name, String id, long value) {
      this.name = name;
      this.id = id;
      this.value = value;
    }

    @Override public Observable<Result<HSDevicesResponse>> perform(HomeSeerService homeSeerService) {
      return homeSeerService.controlDevice(id, value);
    }
  }
}
