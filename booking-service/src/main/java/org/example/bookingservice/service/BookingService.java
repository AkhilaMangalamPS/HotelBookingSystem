package org.example.bookingservice.service;



import org.example.bookingservice.dto.Booking;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface BookingService {
    Booking createBooking(String userEmail, Long roomId, LocalDate checkInDate, LocalDate checkOutdate);
    Booking getBookingById(Long id);
    List<Booking> getMyBookings(String userEmail);
    void cancelBooking(Long id);

}

