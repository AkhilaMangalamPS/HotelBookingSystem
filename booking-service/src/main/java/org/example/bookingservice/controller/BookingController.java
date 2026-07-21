package org.example.bookingservice.controller;



import org.example.bookingservice.dto.Booking;
import org.springframework.web.bind.annotation.*;
import org.example.bookingservice.service.BookingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bookings")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }




    @PostMapping()
    public Booking createBooking(@RequestParam String userEmail,
                                 @RequestParam Long roomId,
                                 @RequestParam LocalDate checkInDate,
                                 @RequestParam LocalDate checkOutDate) {


        return bookingService.createBooking(userEmail, roomId, checkInDate, checkOutDate);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@PathVariable Long id){
        return bookingService.getBookingById(id);
    }

    @GetMapping("/my-booking")
    public List<Booking> getMyBooking(@RequestParam String userEmail){
        return bookingService.getMyBookings(userEmail);
    }

    @PostMapping("/{id}/cancel")
    public void cancelBooking(@PathVariable Long id){
        bookingService.cancelBooking(id);
    }

}
