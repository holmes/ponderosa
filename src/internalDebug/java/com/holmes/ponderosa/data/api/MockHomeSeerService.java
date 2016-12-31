package com.holmes.ponderosa.data.api;

import com.holmes.ponderosa.data.api.model.HSDeviceControlResponse;
import com.holmes.ponderosa.data.api.model.HSDevicesResponse;
import java.util.List;
import javax.inject.Inject;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Query;
import rx.Observable;

public class MockHomeSeerService implements HomeSeerService {
  @Inject
  public MockHomeSeerService() {
  }

  @Override public Observable<Result<HSDevicesResponse>> devices() {
    throw new UnsupportedOperationException("implement me!");
  }

  @Override public Observable<Result<HSDeviceControlResponse>> deviceControls(
      @Query("ref") List<String> references) {
    throw new UnsupportedOperationException("implement me!");
  }
}
