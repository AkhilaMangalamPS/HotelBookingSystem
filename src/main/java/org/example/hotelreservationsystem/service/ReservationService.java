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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {
    private final HotelRepository hotelRepository;
    private final UserReposiroty userReposiroty;
    private final RateRepository rateRepository;
    private final BookingRepository bookingRepository;
    private final EmailNotificationService emailNotificationService;

    public ReservationService(HotelRepository hotelRepository, UserReposiroty userReposiroty, RateRepository rateRepository, BookingRepository bookingRepository,EmailNotificationService emailNotificationService){
        this.hotelRepository = hotelRepository;
        this.userReposiroty = userReposiroty;
        this.rateRepository = rateRepository;
        this.bookingRepository = bookingRepository;
        this.emailNotificationService = emailNotificationService;
    }

    //To get the cheapest hotel
    @Cacheable(
            value = "cheapest_hotel_searches",
            key = "#request.customerType.toLowerCase() + '-' + #request.checkInDate + '-' + #request.checkOutDate"
    )
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
    @CacheEvict(value = "cheapest_hotel_searches", allEntries = true)
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
        emailNotificationService.sendBookingConfirmationEmail(user.getUsername(), hotel.getName(), totalAmount);

        return "Successfully Booked! Confirmed Booking at " + hotel.getName() + ". Total Amount Charged: $" + totalAmount;

    }

    //Helper method to calculate total amount based on check in date , check out date and rating
    private double calculateTotalCost(LocalDate start, LocalDate end, Rate rate) {
        //To get total number of days
        long totalDays = ChronoUnit.DAYS.between(start, end);
        if (totalDays <= 0) {
            return 0;
        }

        //full week between start and end date
        long fullWeeks = totalDays / 7;

        //Remaining leftover days
        long remainderDays = totalDays % 7;

        //Amount for total week day and weekend from fullweeks
        double total = (fullWeeks * 5 * rate.getWeekdayRate()) + (fullWeeks * 2 * rate.getWeekendRate());

        if (remainderDays > 0) {
            //Value of day 1=Monday, 2=Tuesday...
            int startDay = start.getDayOfWeek().getValue();
            long leftOverWeekends = 0;

            long endDay = startDay + remainderDays - 1;

            //Does the rmeiander week cross or touch saturday
            if (startDay <=6 && endDay >=6){
                leftOverWeekends++;
            }

            //Does the remainder week cross or touch sunday
            if(startDay <=7 && endDay>= 7){
                leftOverWeekends++;
            }

            if(endDay >=13){
                leftOverWeekends++;
            }

            if(endDay >=14){
                leftOverWeekends++;
            }

            long leftOverWeekdays = remainderDays - leftOverWeekends;

            total += (leftOverWeekends * rate.getWeekendRate()) + (leftOverWeekdays * rate.getWeekdayRate());
        }
        return total;
    }
}
