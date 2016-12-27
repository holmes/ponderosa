package com.holmes.ponderosa.data.api;

import com.holmes.ponderosa.data.api.model.DevicesResponse;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import rx.Observable;

public interface HomeSeerService {
  @GET("JSON?request=getstatus") //
  Observable<Result<DevicesResponse>> devices();
}
