package com.example.uberbookingservice.service;

import com.example.uberbookingservice.api.LocationServiceApi;
import com.example.uberbookingservice.dtos.BookingReqDto;
import com.example.uberbookingservice.dtos.BookingReqResDto;
import com.example.uberbookingservice.dtos.DriverLocationDto;
import com.example.uberbookingservice.dtos.NearByDriveRequestDto;
import com.example.uberbookingservice.repository.BookingRepo;
import com.example.uberbookingservice.repository.PassengerRepo;
import com.example.uberprojectentityservice.models.Booking;
import com.example.uberprojectentityservice.models.BookingStatus;
import com.example.uberprojectentityservice.models.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService{

    private final BookingRepo bookingRepo;
    private final PassengerRepo passengerRepo;
    @Autowired
    private RestTemplate restTemplate;
    private final static String BASE_URL = "http://localhost:7478/api/v1/location";
    private final LocationServiceApi locationServiceApi;

    public BookingServiceImpl(BookingRepo bookingRepo, PassengerRepo passengerRepo,
                              LocationServiceApi locationServiceApi) {
        this.bookingRepo = bookingRepo;
        this.passengerRepo = passengerRepo;
        this.locationServiceApi=locationServiceApi;
    }

    @Override
    public BookingReqResDto createBooking(BookingReqDto bookingReqDto) {
        Optional<Passenger> passenger = passengerRepo.findById(bookingReqDto.getPassengerId());

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                .startLocation(bookingReqDto.getStartLocation())
                .endLocation(bookingReqDto.getEndLocation())
                .passenger(passenger.orElse(null))
                .build();
        Booking savedBooking = bookingRepo.save(booking);

        NearByDriveRequestDto request = NearByDriveRequestDto.builder()
                .latitude(bookingReqDto.getStartLocation().getLatitude())
                .longitude(bookingReqDto.getStartLocation().getLongitude())
                .build();
/*
        ResponseEntity<DriverLocationDto[]> responseEntity = restTemplate.postForEntity(BASE_URL + "/nearest/drivers", nearByDriveRequestDto,
                DriverLocationDto[].class);

        if(responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null){
            List<DriverLocationDto> driverList = Arrays.asList(responseEntity.getBody());

            driverList.forEach(driverLocationDto -> System.out.println(driverLocationDto.toString()));
        }
 */

        processGetNearByDriverAsync(request);

        return BookingReqResDto.builder()
                .bookingId(savedBooking.getId())
                .bookingStatus(savedBooking.getBookingStatus())
                .build();
    }


    private void processGetNearByDriverAsync(NearByDriveRequestDto request){
        locationServiceApi.getNearByDriver(request).enqueue(
                new Callback<DriverLocationDto[]>() {
                    @Override
                    public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
                        if(response.isSuccessful() &&  response.body()!=null){
                            List<DriverLocationDto> drivers = Arrays.asList(response.body());
                            drivers.forEach(driver -> System.out.println(driver.toString()));
                        }
                        else{
                            System.out.println("************ request body is null ************");
                        }
                    }

                    @Override
                    public void onFailure(Call<DriverLocationDto[]> call, Throwable t) {
                        t.printStackTrace();
                    }
                }
        );
    }

    @Override
    public void cancelBooking(Long bookingId) {

    }
}
