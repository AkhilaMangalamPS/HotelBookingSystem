package org.example.hotelreservationsystem.repository;

import org.example.hotelreservationsystem.model.CustomerType;
import org.example.hotelreservationsystem.model.Hotel;
import org.example.hotelreservationsystem.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findByHotelAndCustomerType(Hotel hotel, CustomerType customerType);
}
