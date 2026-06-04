package org.example.hotelreservationsystem.service;

import jakarta.transaction.Transactional;
import org.example.hotelreservationsystem.dto.BookingRequest;
import org.example.hotelreservationsystem.dto.SearchRequest;
import org.example.hotelreservationsystem.dto.SearchResponse;
import org.example.hotelreservationsystem.model.*;
import org.example.hotelreservationsystem.repository.BookingRepository;
import org.example.hotelreservationsystem.repository.HotelRepository;
import org.example.hotelreservationsystem.repository.RateRepository;
import org.example.hotelreservationsystem.repository.UserReposiroty;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {
    private final HotelRepository hotelRepository;
    private final UserReposiroty userReposiroty;
    private final RateRepository rateRepository;
    private final BookingRepository bookingRepository;

    public ReservationService(HotelRepository hotelRepository, UserReposiroty userReposiroty, RateRepository rateRepository, BookingRepository bookingRepository){
        this.hotelRepository = hotelRepository;
        this.userReposiroty = userReposiroty;
        this.rateRepository = rateRepository;
        this.bookingRepository = bookingRepository;
    }

    //To get the cheapest hotel
    public SearchResponse findCheapestHotel(SearchRequest request){
        CustomerType customerType = CustomerType.valueOf(request.getCustomerType().toUpperCase());
        List<Hotel> allHotels = hotelRepository.findAll();

        Hotel cheapestHotel = null;
        double lowestCost = Double.MAX_VALUE;

        for(Hotel hotel : allHotels) {
            //Verify whether the hotel has available rooms for the given dates
            List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(hotel, request.getCheckInDate(), request.getCheckOutDate());
            if (overlappingBookings.size() >= hotel.getTotalRooms()) {
                continue;
            }

            //Fetch appropriate rate row
            Rate rate = rateRepository.findByHotelAndCustomerType(hotel, customerType)
                    .orElseThrow(() -> new RuntimeException("Pricing rates not configured"));

            //Compute cost using dates
            double currentHotelCost = calculateTotalCost(request.getCheckInDate(), request.getCheckOutDate(), rate);

            if (currentHotelCost < lowestCost) {
                lowestCost = currentHotelCost;
                cheapestHotel = hotel;
            } else if (Math.abs(currentHotelCost - lowestCost) < 0.001) {
                if (cheapestHotel != null && hotel.getRating() > cheapestHotel.getRating()) {
                    cheapestHotel = hotel;
                }
            }
        }

        if(cheapestHotel == null){
            return SearchResponse.builder()
                    .message("No hotels available with vacant rooms for the selected dates")
                    .build();

        }
        return SearchResponse.builder()
                .hotelName(cheapestHotel.getName())
                .rating(cheapestHotel.getRating())
                .totalCost(lowestCost)
                .message("Cheapest hotel found successfully!!")
                .build();
    }


    @Transactional
    public String bookHotel(BookingRequest request, String username){
        User user = userReposiroty.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User session context not found."));

        Hotel hotel = hotelRepository.findByName(request.getHotelName())
                .orElseThrow(() -> new RuntimeException("Hotel does not exist"));

        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(hotel, request.getCheckInDate(), request.getCheckOutDate());
        if(overlappingBookings.size() >+ hotel.getTotalRooms()){
            throw new RuntimeException("Transaction Aborted: Hotel just become fully booked!!");
        }

        Rate rate = rateRepository.findByHotelAndCustomerType(hotel, user.getCustomerType())
                .orElseThrow(() -> new RuntimeException("Rates not found"));

        double totalAmount = calculateTotalCost(request.getCheckInDate(), request.getCheckOutDate(), rate);

        Booking booking = Booking.builder()
                .user(user)
                .hotel(hotel)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalAmount(totalAmount)
                .build();

        bookingRepository.save(booking);

        return "Successfully Booked! Confirmed Booking at " + hotel.getName() + ". Total Amount Charged: $" + totalAmount;

    }

    //Helper method to calculate total amount based on check in date , check out date and rating
    private double calculateTotalCost(LocalDate start, LocalDate end, Rate rate){
        double total =0;
        for(LocalDate date = start; date.isBefore(end); date = date.plusDays(1)){
            DayOfWeek day = date.getDayOfWeek();
            if( day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY){
                total = total + rate.getWeekdayRate();
            }
            else{
                total = total + rate.getWeekdayRate();
            }
        }
        return total;
    }
}
