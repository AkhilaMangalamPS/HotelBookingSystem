package org.example.inventoryservice.controller;

import org.example.inventoryservice.dto.Room;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.inventoryservice.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/rooms")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping(value = "/test-connection")
    public String testConnection() {
        return "The controller is working perfectly!";
    }

    @GetMapping("/hotel/{hotelId}")
    public List<Room> getRoomsByHotelId(@PathVariable String hotelId,
                                        @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate checkIn,
                                        @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate checkOut ) {
        try {
            // Trim any hidden carriage returns or whitespace characters
            Long cleanId = Long.parseLong(hotelId.trim());

            if(checkIn !=null && checkOut !=null) {
                return roomService.getAvailableRoomsbyHotelsAndDates(cleanId, checkIn, checkOut);
            }
            return roomService.getRoomsByHotelId(cleanId);
        } catch (NumberFormatException e) {
            // If it's truly a bad format, return an empty list instead of crashing the server with a 400
            System.err.println("Invalid hotel ID format received: " + hotelId);
            return java.util.Collections.emptyList();
        }
    }

    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Long id){
        return roomService.getRoomById(id);
    }

//    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HOTEL_MANAGER')")
    @PostMapping("/{id}")
    public Room addRoomToHotel(@PathVariable Long id, @RequestBody Room room){
        return roomService.addRoomToHotel(id, room);
    }

    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable Long id, @RequestBody Room room){
        return roomService.updateRoom(id, room);
    }

    @PatchMapping("/{id}/availability")
    public void updateRoomAvailability(@PathVariable Long id, @RequestParam boolean isAvailable){
        roomService.updateRoomAvailability(id, isAvailable);
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<String> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // This forces the full error stack trace into your IDE console
        return org.springframework.http.ResponseEntity
                .status(org.springframework.http.HttpStatus.BAD_REQUEST)
                .body("DIAGNOSTIC CRASH REPORT: " + ex.getClass().getName() + " -> " + ex.getMessage());
    }







}
