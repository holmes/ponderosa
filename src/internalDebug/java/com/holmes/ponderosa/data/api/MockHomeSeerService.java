package com.holmes.ponderosa.data.api;

import com.holmes.ponderosa.data.api.model.HSDeviceControlResponse;
import com.holmes.ponderosa.data.api.model.HSDevicesResponse;
import com.holmes.ponderosa.data.api.model.HSEventsResponse;
import com.jakewharton.retrofit2.adapter.rxjava2.Result;
import io.reactivex.Observable;
import java.util.List;
import javax.inject.Inject;
import retrofit2.http.Query;

public class MockHomeSeerService implements HomeSeerService {
  @Inject public MockHomeSeerService() {
  }

  @Override public Observable<Result<HSDevicesResponse>> devices() {
    throw new UnsupportedOperationException("implement me!");
  }

  @Override public Observable<Result<HSDeviceControlResponse>> deviceControls(@Query("ref") List<String> references) {
    throw new UnsupportedOperationException("implement me!");
  }

  @Override
  public Observable<Result<HSDevicesResponse>> controlDevice(@Query("ref") String reference, @Query("value") Long value) {
    throw new UnsupportedOperationException("implement me!");
  }

  @Override public Observable<Result<HSEventsResponse>> events() {
    throw new UnsupportedOperationException("implement me!");
  }

  @Override public Observable<Result<HSEventsResponse>> runEvent(@Query("id") Long eventId) {
    throw new UnsupportedOperationException("implement me!");
  }
}
