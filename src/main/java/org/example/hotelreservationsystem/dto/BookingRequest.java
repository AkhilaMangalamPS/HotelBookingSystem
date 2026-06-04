package org.example.hotelreservationsystem.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
