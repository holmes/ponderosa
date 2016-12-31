package com.holmes.ponderosa.data.api;

import com.holmes.ponderosa.data.api.model.HSDeviceControlResponse;
import com.holmes.ponderosa.data.api.model.HSDevicesResponse;
import java.util.List;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface HomeSeerService {
  @GET("JSON?request=getstatus") //
  Observable<Result<HSDevicesResponse>> devices();

  @GET("JSON?request=getcontrol") //
  Observable<Result<HSDeviceControlResponse>> deviceControls(@Query("ref") List<String> references);
}


