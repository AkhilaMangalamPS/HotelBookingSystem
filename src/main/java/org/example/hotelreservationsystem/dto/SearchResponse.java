package org.example.hotelreservationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String hotelName;
    private int rating;
    private double totalCost;
    private String message;
}
