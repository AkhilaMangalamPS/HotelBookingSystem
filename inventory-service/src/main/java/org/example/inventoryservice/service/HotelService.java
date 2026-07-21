package org.example.inventoryservice.service;


import org.example.inventoryservice.dto.Hotel;

import java.util.List;

public interface HotelService {
    List<Hotel> searchHotels(String city, Integer adults, Integer children, List<String> amenities);
    Hotel getHotelById(Long id);
    Hotel createHotel(Hotel hotel);
    Hotel updateHotel(Long id, Hotel hotelDetails);
    void deleteHotel(Long id);

}
