package org.example.hotelreservationsystem.dto;

import lombok.Data;

@Data
public class HotelDto {
    private String name;
    private int rating;
    private int totalRooms;

    //Regular Customer Prices
    private double regularWeekdayRate;
    private double regularWeekendRate;

    //Reward Customer Prices
    private double rewardWeekdayRate;
    private double rewardWeekendRate;
}
