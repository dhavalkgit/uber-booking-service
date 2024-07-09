package com.example.uberbookingservice.api;

import com.example.uberbookingservice.dtos.RideReqDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SocketServiceApi {

    @POST("/api/socket/driver/newride")
    Call<Boolean> sendRideRequest(@Body RideReqDto reqDto);
}
