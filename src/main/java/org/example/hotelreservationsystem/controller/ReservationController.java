package org.example.hotelreservationsystem.controller;

import org.apache.coyote.Response;
import org.example.hotelreservationsystem.dto.BookingRequest;
import org.example.hotelreservationsystem.dto.SearchRequest;
import org.example.hotelreservationsystem.dto.SearchResponse;
import org.example.hotelreservationsystem.model.Booking;
import org.example.hotelreservationsystem.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/search-cheapest")
    public ResponseEntity<SearchResponse> findCheapestHotel(@RequestBody SearchRequest request){
        return ResponseEntity.ok(reservationService.findCheapestHotel(request));
    }

    @PostMapping("/book")
    public ResponseEntity<String> bookHotel(@RequestBody BookingRequest request,
                                            @AuthenticationPrincipal UserDetails userDetails){
        String resultMessage = reservationService.bookHotel(request, userDetails.getUsername());
        return ResponseEntity.ok(resultMessage);

    }
}
