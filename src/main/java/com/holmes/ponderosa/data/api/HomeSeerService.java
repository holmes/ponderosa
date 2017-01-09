package com.holmes.ponderosa.data.api;

import com.holmes.ponderosa.data.api.model.HSDeviceControlResponse;
import com.holmes.ponderosa.data.api.model.HSDevicesResponse;
import com.holmes.ponderosa.data.api.model.HSEventsResponse;
import com.jakewharton.retrofit2.adapter.rxjava2.Result;
import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HomeSeerService {
  @GET("JSON?request=getstatus") //
  Observable<Result<HSDevicesResponse>> devices();

  @GET("JSON?request=getcontrol") //
  Observable<Result<HSDeviceControlResponse>> deviceControls(@Query("ref") List<String> references);

  @GET("JSON?request=controldevicebyvalue") //
  Observable<Result<HSDevicesResponse>> controlDevice(@Query("ref") String reference, @Query("value") Integer value);

  @GET("JSON?request=getevents") //
  Observable<Result<HSEventsResponse>> events();

  @GET("JSON?request=runevent") //
  Observable<Result<HSEventsResponse>> runEvent(@Query("id") Long eventId);
}


