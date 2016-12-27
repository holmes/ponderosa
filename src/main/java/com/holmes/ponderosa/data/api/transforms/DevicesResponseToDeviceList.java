package com.holmes.ponderosa.data.api.transforms;

import com.holmes.ponderosa.data.api.model.Device;
import com.holmes.ponderosa.data.api.model.DevicesResponse;
import java.util.Collections;
import java.util.List;
import retrofit2.adapter.rxjava.Result;
import rx.functions.Func1;

public final class DevicesResponseToDeviceList implements Func1<Result<DevicesResponse>, List<Device>> {
  private static volatile DevicesResponseToDeviceList instance;

  public static DevicesResponseToDeviceList instance() {
    if (instance == null) {
      instance = new DevicesResponseToDeviceList();
    }
    return instance;
  }

  @Override public List<Device> call(Result<DevicesResponse> result) {
    DevicesResponse repositoriesResponse = result.response().body();
    return repositoriesResponse.Devices == null //
        ? Collections.emptyList() //
        : repositoriesResponse.Devices;
  }
}
