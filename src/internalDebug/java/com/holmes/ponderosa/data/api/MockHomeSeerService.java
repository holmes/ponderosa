package com.holmes.ponderosa.data.api;

import com.holmes.ponderosa.data.api.model.DeviceControl;
import com.holmes.ponderosa.data.api.model.DeviceControlResponse;
import com.holmes.ponderosa.data.api.model.DevicesResponse;
import java.util.List;
import javax.inject.Inject;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Query;
import rx.Observable;

public class MockHomeSeerService implements HomeSeerService {
  @Inject
  public MockHomeSeerService() {
  }

  @Override public Observable<Result<DevicesResponse>> devices() {
    throw new UnsupportedOperationException("implement me!");
  }

  @Override public Observable<Result<DeviceControlResponse>> deviceControls(
      @Query("ref") List<String> references) {
    throw new UnsupportedOperationException("implement me!");
  }
}
