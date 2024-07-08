package com.example.uberbookingservice.dtos;

import com.example.uberprojectentityservice.models.ExactLocation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingReqDto {
    private Long passengerId;
    private ExactLocation startLocation;
    private ExactLocation endLocation;
}
