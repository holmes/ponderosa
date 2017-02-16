package com.holmes.ponderosa.ui.device;

import android.support.annotation.NonNull;
import com.holmes.ponderosa.data.sql.model.Device;
import java.util.function.Predicate;

public class DeviceFilters {
  @NonNull public static Predicate<Device> allowableDevices() {
    // TODO all we handle are lights for now.
    return device -> (device.device_type() == Device.Type.PLUG_IN);
  }

}
