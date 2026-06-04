package org.example.hotelreservationsystem.service;

import jakarta.transaction.Transactional;
import org.example.hotelreservationsystem.dto.HotelDto;
import org.example.hotelreservationsystem.model.CustomerType;
import org.example.hotelreservationsystem.model.Hotel;
import org.example.hotelreservationsystem.model.Rate;
import org.example.hotelreservationsystem.repository.HotelRepository;
import org.example.hotelreservationsystem.repository.RateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final RateRepository rateRepository;

    public HotelService(HotelRepository hotelRepository, RateRepository rateRepository){
        this.hotelRepository = hotelRepository;
        this.rateRepository = rateRepository;
    }

    @Transactional
    public Hotel addHotel(HotelDto hotelDto){
        if(hotelRepository.findByName(hotelDto.getName()).isPresent()){
            throw new RuntimeException("Hotel with this name already exists");
        }

        Hotel hotel = Hotel.builder()
                .name(hotelDto.getName())
                .rating(hotelDto.getRating())
                .totalRooms(hotelDto.getTotalRooms())
                .build();

        Hotel savedHotel = hotelRepository.save(hotel);

        Rate regularRate = Rate.builder()
                .hotel(savedHotel)
                .customerType(CustomerType.REGULAR)
                .weekdayRate(hotelDto.getRegularWeekdayRate())
                .weekendRate(hotelDto.getRegularWeekendRate())
                .build();

        Rate rewardRate = Rate.builder()
                .hotel(savedHotel)
                .customerType(CustomerType.REWARDS)
                .weekdayRate(hotelDto.getRewardWeekdayRate())
                .weekendRate(hotelDto.getRewardWeekendRate())
                .build();

        rateRepository.save(regularRate);
        rateRepository.save(rewardRate);

        return savedHotel;
    }

    @Transactional
    public Hotel editHotelDetails(Long hotelId, HotelDto hotelDto){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: "+ hotelId));
        hotel.setName(hotel.getName());
        hotel.setRating(hotelDto.getRating());
        hotel.setTotalRooms(hotelDto.getTotalRooms());
        Hotel updatedHotel = hotelRepository.save(hotel);

        //Update Regular rates
        Rate regularRate = rateRepository.findByHotelAndCustomerType(updatedHotel, CustomerType.REGULAR)
                .orElse(new Rate(null, updatedHotel, CustomerType.REGULAR, 0, 0));
        regularRate.setWeekdayRate(hotelDto.getRegularWeekdayRate());
        regularRate.setWeekendRate(hotelDto.getRegularWeekendRate());

        //Update Reward rates
        Rate rewardRate = rateRepository.findByHotelAndCustomerType(updatedHotel, CustomerType.REWARDS)
                .orElse(new Rate(null, updatedHotel, CustomerType.REWARDS, 0 , 0));
        rewardRate.setWeekdayRate(hotelDto.getRewardWeekdayRate());
        rewardRate.setWeekendRate(hotelDto.getRewardWeekendRate());

        return updatedHotel;
    }

    public List<Hotel> getAllHotels(){
        return hotelRepository.findAll();
    }




}
