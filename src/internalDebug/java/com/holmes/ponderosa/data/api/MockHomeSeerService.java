package com.holmes.ponderosa.data.api;

import com.holmes.ponderosa.data.api.model.DevicesResponse;
import javax.inject.Inject;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;

public class MockHomeSeerService implements HomeSeerService {
  @Inject
  public MockHomeSeerService() {
  }

  @Override public Observable<Result<DevicesResponse>> devices() {
    throw new UnsupportedOperationException("implement me!");
  }
}
