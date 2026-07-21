package org.example.bookingservice.repository;



import org.example.bookingservice.dto.Booking;
import org.example.bookingservice.dto.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserEmail(String email);
    List<Booking> findByRoomIdAndStatusNot(Long roomId, BookingStatus status);
}

