package com.example.uberbookingservice.dtos;

import com.example.uberprojectentityservice.models.BookingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookingReqDto {
    private String status;
}
