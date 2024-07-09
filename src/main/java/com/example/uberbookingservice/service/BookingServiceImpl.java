package com.example.uberbookingservice.service;

import com.example.uberbookingservice.api.LocationServiceApi;
import com.example.uberbookingservice.api.SocketServiceApi;
import com.example.uberbookingservice.dtos.*;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class BookingServiceImpl implements BookingService{

    private final BookingRepo bookingRepo;
    private final PassengerRepo passengerRepo;
    @Autowired
    private RestTemplate restTemplate;
    private final static String BASE_URL = "http://localhost:7478/api/v1/location";
    private final LocationServiceApi locationServiceApi;
    private final SocketServiceApi socketServiceApi;

    public BookingServiceImpl(BookingRepo bookingRepo, PassengerRepo passengerRepo,
                              LocationServiceApi locationServiceApi, SocketServiceApi socketServiceApi) {
        this.bookingRepo = bookingRepo;
        this.passengerRepo = passengerRepo;
        this.locationServiceApi=locationServiceApi;
        this.socketServiceApi = socketServiceApi;
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

        processGetNearByDriverAsync(request, bookingReqDto.getPassengerId(), savedBooking.getId());

        return BookingReqResDto.builder()
                .bookingId(savedBooking.getId())
                .bookingStatus(savedBooking.getBookingStatus())
                .diver(Optional.ofNullable(savedBooking.getDriver()))
                .build();
    }


    private void processGetNearByDriverAsync(NearByDriveRequestDto request, Long passengerId, Long bookingId){
        locationServiceApi.getNearByDriver(request).enqueue(
                new Callback<DriverLocationDto[]>() {
                    @Override
                    public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
                        if(response.isSuccessful() &&  response.body()!=null){
                            List<DriverLocationDto> drivers = Arrays.asList(response.body());
                            drivers.forEach(driver -> System.out.println(driver.toString()));
                            List<Long> driver_list= drivers.stream().map(driver -> Long.parseLong(driver.getDriverId())).toList();

                            RideReqDto reqDto = RideReqDto.builder()
                                    .driverId(driver_list)
                                    .passengerId(passengerId)
                                    .bookingId(bookingId)
                                    .build();
                            processRideRequestToDriver(reqDto);
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

    private void processRideRequestToDriver(RideReqDto reqDto){
        socketServiceApi.sendRideRequest(reqDto).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        System.out.println(response.body());
                    }
                }
                else{
                    System.out.println("-------------- response is unsuccessful ----------------");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void cancelBooking(Long bookingId) {

    }

    @Override
    public UpdateBookingResDto updateBooking(UpdateBookingReqDto reqDto, Long bookingId) {
        switch (reqDto.getStatus()) {
            case "SUCCESSES" -> {
                 bookingRepo.updateBookingStatus(BookingStatus.SUCCESSES, bookingId);
            }
            case "PENDING" ->{
                 bookingRepo.updateBookingStatus(BookingStatus.PENDING, bookingId);
            }
            default -> System.out.println("hi");
        }
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        return UpdateBookingResDto.builder()
                .bookingId(booking.get().getId())
                .bookingStatus(booking.get().getBookingStatus())
                .driver(Optional.ofNullable(booking.get().getDriver()))
                .build();
    }
}
