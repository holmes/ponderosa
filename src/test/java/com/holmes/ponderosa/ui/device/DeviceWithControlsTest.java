package com.holmes.ponderosa.ui.device;

import android.support.annotation.NonNull;
import com.holmes.ponderosa.data.sql.model.Device;
import com.holmes.ponderosa.data.sql.model.DeviceControl;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeviceWithControlsTest {
  Device device;
  List<DeviceControl> controls;

  @Before public void setUp() throws Exception {
    device = mock(Device.class);
    controls = new ArrayList<>();
  }

  @Test public void testNoControlsAndDeviceIsOn() {
    when(device.value()).thenReturn(99.0);
    DeviceWithControls deviceController = new DeviceWithControls(device, controls);
    assertThat(deviceController.toggleDevice().value).isEqualTo(DeviceWithControls.DEFAULT_OFF);
  }

  @Test public void testNoControlsAndDeviceIsOff() {
    when(device.value()).thenReturn(0.0);
    DeviceWithControls deviceController = new DeviceWithControls(device, controls);
    assertThat(deviceController.toggleDevice().value).isEqualTo(DeviceWithControls.DEFAULT_FULL_ON);
  }

  @Test public void testOnOffControlsWhenDeviceIsOff() {
    when(device.value()).thenReturn(0.0);
    controls.add(onControl());
    controls.add(offControl());

    DeviceWithControls deviceController = new DeviceWithControls(device, controls);
    assertThat(deviceController.toggleDevice().value).isEqualTo(DeviceWithControls.DEFAULT_FULL_ON);
  }

  @Test public void testOnOffControlsWhenDeviceIsOn() {
    when(device.value()).thenReturn(99.0);
    controls.add(onControl());
    controls.add(offControl());

    DeviceWithControls deviceController = new DeviceWithControls(device, controls);
    assertThat(deviceController.toggleDevice().value).isEqualTo(DeviceWithControls.DEFAULT_OFF);
  }

  @Test public void testOnOffDimControlsWhenDeviceIsOff() {
    when(device.value()).thenReturn(0.0);
    controls.add(onControl());
    controls.add(offControl());
    controls.add(dimControl());
    controls.add(lastLevelControl());

    DeviceWithControls deviceController = new DeviceWithControls(device, controls);
    assertThat(deviceController.toggleDevice().value).isEqualTo(lastLevelControl().value());
  }

  @Test public void testOnOffDimControlsWhenDeviceIsOn() {
    when(device.value()).thenReturn(99.0);
    controls.add(onControl());
    controls.add(offControl());
    controls.add(dimControl());
    controls.add(lastLevelControl());

    DeviceWithControls deviceController = new DeviceWithControls(device, controls);
    assertThat(deviceController.toggleDevice().value).isEqualTo(offControl().value());
  }

  @Test public void testOnOffDimControlsWhenDeviceIsDimmed() {
    when(device.value()).thenReturn(25.0);
    controls.add(onControl());
    controls.add(offControl());
    controls.add(dimControl());
    controls.add(lastLevelControl());

    DeviceWithControls deviceController = new DeviceWithControls(device, controls);
    assertThat(deviceController.toggleDevice().value).isEqualTo(offControl().value());
  }

  @NonNull private DeviceControl onControl() {
    DeviceControl control = mock(DeviceControl.class);
    when(control.use()).thenReturn(DeviceControl.Use.ON);
    when(control.type()).thenReturn(DeviceControl.Type.BUTTON);
    when(control.value()).thenReturn(99L);
    return control;
  }

  @NonNull private DeviceControl offControl() {
    DeviceControl control = mock(DeviceControl.class);
    when(control.use()).thenReturn(DeviceControl.Use.OFF);
    when(control.type()).thenReturn(DeviceControl.Type.BUTTON);
    when(control.value()).thenReturn(0L);
    return control;
  }

  @NonNull private DeviceControl lastLevelControl() {
    DeviceControl control = mock(DeviceControl.class);
    when(control.use()).thenReturn(DeviceControl.Use.ON_ALTERNATE);
    when(control.type()).thenReturn(DeviceControl.Type.BUTTON);
    when(control.value()).thenReturn(255L);
    return control;
  }

  @NonNull private DeviceControl dimControl() {
    DeviceControl control = mock(DeviceControl.class);
    when(control.use()).thenReturn(DeviceControl.Use.DIM);
    when(control.type()).thenReturn(DeviceControl.Type.VALUES_RANGE);
    when(control.value()).thenReturn(1L);
    return control;
  }
}
