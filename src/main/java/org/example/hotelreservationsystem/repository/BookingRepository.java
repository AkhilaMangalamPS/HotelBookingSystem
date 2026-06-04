package org.example.hotelreservationsystem.repository;

import org.example.hotelreservationsystem.model.Booking;
import org.example.hotelreservationsystem.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.hotel = :hotel AND ((b.checkInDate < :checkOut AND b.checkOutDate > :checkIn))")
    List<Booking> findOverlappingBookings(
            @Param("hotel") Hotel hotel,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}
