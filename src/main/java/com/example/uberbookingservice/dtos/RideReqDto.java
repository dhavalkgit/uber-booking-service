package com.example.uberbookingservice.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RideReqDto {
    private Long passengerId;
    private List<Long> driverId;
    private Long bookingId;
}
