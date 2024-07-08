package com.example.uberbookingservice.api;

import com.example.uberbookingservice.dtos.DriverLocationDto;
import com.example.uberbookingservice.dtos.NearByDriveRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LocationServiceApi {
    @POST("api/v1/location/nearest/drivers")
    public Call<DriverLocationDto[]> getNearByDriver(@Body NearByDriveRequestDto nearByDriveRequestDto);
}
