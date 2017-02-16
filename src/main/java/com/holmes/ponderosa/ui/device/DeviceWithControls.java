package com.holmes.ponderosa.ui.device;

import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import java.util.List;

import static com.holmes.ponderosa.data.sql.model.DeviceControl.Use.OFF;
import static com.holmes.ponderosa.data.sql.model.DeviceControl.Use.ON;
import static com.holmes.ponderosa.data.sql.model.DeviceControl.Use.ON_ALTERNATE;

public class DeviceWithControls {
  public static final Long DEFAULT_OFF = 0L;
  public static final Long DEFAULT_FULL_ON = 99L;

  public static class ControlResult {
    public final Device device;
    public final DeviceControl.Use selectedUse;
    public final Long value;

    public ControlResult(Device device, DeviceControl.Use selectedUse, Long value) {
      this.device = device;
      this.selectedUse = selectedUse;
      this.value = value;
    }
  }

  public final Device device;
  public final List<DeviceControl> controls;

  public DeviceWithControls(Device device, List<DeviceControl> controls) {
    this.device = device;
    this.controls = controls;
  }

  /**
   * If a device is on or dimmed, turn it off.
   * If it's off, turn it on to last level (if supported) or all the way on.
   *
   * @return a value mildly corresponding to a {@link DeviceControl} based on the current value of {@link
   * Device#value()}, or -1 if nothing should be changed.
   */
  public ControlResult toggleDevice() {
    if (controls.isEmpty()) {
      DeviceControl.Use selectedUse = device.value() == DEFAULT_OFF ? DeviceControl.Use.ON : DeviceControl.Use.OFF;
      return new ControlResult(device, selectedUse, selectedUse.defaultValue());
    }

    DeviceControl selectedControl = getOpposite();
    Long newValue = selectedControl.value();

    return new ControlResult(device, selectedControl.use(), newValue);
  }

  private DeviceControl getOpposite() {
    double currentValue = device.value();

    if (currentValue > 0) {
      return controls.stream() //
          .filter(deviceControl -> deviceControl.use().equals(OFF)) //
          .findFirst().get();
    } else {
      return controls.stream() //
          .filter(deviceControl -> deviceControl.use().equals(ON) || deviceControl.use().equals(ON_ALTERNATE)) //
          .sorted((o1, o2) -> o2.use().ordinal() - o1.use().ordinal())
          .findFirst().get();
    }
  }
}
