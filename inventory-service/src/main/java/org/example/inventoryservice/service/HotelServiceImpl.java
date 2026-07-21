package org.example.inventoryservice.service;



import org.example.inventoryservice.dto.Hotel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.example.inventoryservice.repository.HotelRepository;
import org.example.inventoryservice.Specification.HotelSpecification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    @Override
    public List<Hotel> searchHotels(String city, Integer adults, Integer children, List<String> amenities) {
        Specification<Hotel> filteredHotels = HotelSpecification.filterHotels(city, adults,children, amenities);
        return hotelRepository.findAll(filteredHotels);

    }

    @Override
    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id).orElseThrow( () ->
                new RuntimeException("Hotel Not Found with id: "+ id));
    }

    @Override
    public Hotel createHotel(Hotel hotel) {
        return  hotelRepository.save(hotel);
    }

    @Override
    public Hotel updateHotel(Long id, Hotel hotelDetails) {
        Hotel existingHotel = hotelRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Hotel not found with Id: "+ id));
        existingHotel.setName(hotelDetails.getName());
        existingHotel.setAmenities(hotelDetails.getAmenities());
        existingHotel.setRating(hotelDetails.getRating());
        existingHotel.setDescription(hotelDetails.getDescription());
        existingHotel.setRooms(hotelDetails.getRooms());
        existingHotel.setAddress(hotelDetails.getAddress());

        return hotelRepository.save(existingHotel);
    }

    @Override
    public void deleteHotel(Long id) {
        Hotel removeHotel = hotelRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Hotel not found with id:"+ id));
        hotelRepository.deleteById(id);
    }
}

