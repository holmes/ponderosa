package com.holmes.ponderosa.data.sql.model;

import com.google.auto.value.AutoValue;
import com.holmes.ponderosa.DeviceModel;
import com.squareup.sqldelight.RowMapper;

@AutoValue
public abstract class Device implements DeviceModel {
  public static final Factory<Device> FACTORY = new Factory<>(AutoValue_Device::new);

  public static final RowMapper<Device> SELECT_ALL_MAPPER = FACTORY.select_allMapper();
}
