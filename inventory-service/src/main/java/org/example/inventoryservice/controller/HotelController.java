package org.example.inventoryservice.controller;


import org.example.inventoryservice.dto.Hotel;
import org.apache.kafka.common.requests.AbstractRequest;
import org.example.inventoryservice.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.inventoryservice.service.HotelService;

import java.util.List;

@RestController
@RequestMapping("/hotels")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class HotelController {
    private final HotelService hotelService;


    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping("/search")
    public ResponseEntity<List<Hotel>> searchHotelsByLocation(@RequestParam String location){
        List<Hotel> hotels = hotelRepository.findByLocationIgnoreCase(location);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{id}")
    public Hotel getHotel(@PathVariable Long id) {
        return hotelService.getHotelById(id);
    }

    @PostMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HOTEL_MANAGER')")
    public Hotel createHotel(@RequestBody Hotel hotel){
        return hotelService.createHotel(hotel);
    }

    @PutMapping("/{id}")
    public Hotel updateHotel(@PathVariable Long id,@RequestBody Hotel hotelDetails) {
        return hotelService.updateHotel(id, hotelDetails);
    }

    @DeleteMapping("/{id}")
    public void DeleteHotel(@PathVariable Long id){
        hotelService.deleteHotel(id);
    }


}
