package com.example.uberbookingservice.dtos;

import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Driver;
import lombok.*;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBookingResDto {
    private Long bookingId;
    private Optional<Driver>driver;
    private BookingStatus bookingStatus;
}
