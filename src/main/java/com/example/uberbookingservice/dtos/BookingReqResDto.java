package com.example.uberbookingservice.dtos;

import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Driver;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingReqResDto {
    private Long bookingId;
    private BookingStatus bookingStatus;
    private Optional<Driver> diver;
}
