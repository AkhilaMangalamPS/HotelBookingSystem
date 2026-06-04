package org.example.hotelreservationsystem.dto;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
public class SearchRequest {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String customerType;

}
