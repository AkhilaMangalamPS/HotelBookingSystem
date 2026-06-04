package org.example.hotelreservationsystem.controller;

import org.apache.coyote.Response;
import org.example.hotelreservationsystem.dto.HotelDto;
import org.example.hotelreservationsystem.model.Hotel;
import org.example.hotelreservationsystem.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hotels")
public class AdminHotelController {
    private final HotelService hotelService;

    public AdminHotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping("/add")
    public ResponseEntity<Hotel> addHotel(@RequestBody HotelDto hotelDto){
        return ResponseEntity.ok(hotelService.addHotel(hotelDto));
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<Hotel> editHotel(@PathVariable Long hotelId, @RequestBody HotelDto hotelDto){
        return ResponseEntity.ok(hotelService.editHotelDetails(hotelId, hotelDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Hotel>> getAllHotels(){
        return ResponseEntity.ok(hotelService.getAllHotels());
    }
}
