package com.example.uberbookingservice.service;

import com.example.uberbookingservice.dtos.BookingReqDto;
import com.example.uberbookingservice.dtos.BookingReqResDto;

public interface BookingService {
    BookingReqResDto createBooking(BookingReqDto bookingReqDto);
    void cancelBooking(Long bookingId);
}
