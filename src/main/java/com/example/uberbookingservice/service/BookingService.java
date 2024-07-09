package com.example.uberbookingservice.service;

import com.example.uberbookingservice.dtos.BookingReqDto;
import com.example.uberbookingservice.dtos.BookingReqResDto;
import com.example.uberbookingservice.dtos.UpdateBookingReqDto;
import com.example.uberbookingservice.dtos.UpdateBookingResDto;

public interface BookingService {
    BookingReqResDto createBooking(BookingReqDto bookingReqDto);
    void cancelBooking(Long bookingId);
    UpdateBookingResDto updateBooking(UpdateBookingReqDto reqDto, Long bookingId);
}
