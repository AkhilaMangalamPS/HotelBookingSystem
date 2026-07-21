package org.example.inventoryservice.service;


import org.example.inventoryservice.dto.Room;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    List<Room> getRoomsByHotelId(Long id);
    Room getRoomById(Long id);
    Room addRoomToHotel(Long id, Room room);
    Room updateRoom(Long id, Room room);
    void updateRoomAvailability(Long id, boolean isAvailable);
    List<Room> getAvailableRoomsbyHotelsAndDates(Long hotelId, LocalDate checkIn, LocalDate checkOut);

}
