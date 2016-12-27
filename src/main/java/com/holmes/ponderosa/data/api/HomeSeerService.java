package com.holmes.ponderosa.data.api;

import com.holmes.ponderosa.data.api.model.DeviceControl;
import com.holmes.ponderosa.data.api.model.DeviceControlResponse;
import com.holmes.ponderosa.data.api.model.DevicesResponse;
import java.util.List;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface HomeSeerService {
  @GET("JSON?request=getstatus") //
  Observable<Result<DevicesResponse>> devices();

  @GET("JSON?request=getcontrol") //
  Observable<Result<DeviceControlResponse>> deviceControls(@Query("ref") List<String> references);
}


