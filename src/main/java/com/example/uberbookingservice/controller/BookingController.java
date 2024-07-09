package com.example.uberbookingservice.controller;

import com.example.uberbookingservice.dtos.BookingReqDto;
import com.example.uberbookingservice.dtos.BookingReqResDto;
import com.example.uberbookingservice.dtos.UpdateBookingReqDto;
import com.example.uberbookingservice.dtos.UpdateBookingResDto;
import com.example.uberbookingservice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/")
    public ResponseEntity<BookingReqResDto> rideBooking(@RequestBody BookingReqDto bookingReqDto){
        BookingReqResDto booking = bookingService.createBooking(bookingReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<UpdateBookingResDto> updateBooking(@RequestBody UpdateBookingReqDto request,
                                                             @PathVariable Long bookingId){
        return ResponseEntity.status(HttpStatus.OK).body(bookingService.updateBooking(request,bookingId));
    }
}
