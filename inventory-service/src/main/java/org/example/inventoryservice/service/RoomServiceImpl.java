package org.example.inventoryservice.service;


import org.example.inventoryservice.dto.Hotel;
import org.example.inventoryservice.dto.Room;
import org.springframework.stereotype.Service;
import org.example.inventoryservice.repository.HotelRepository;
import org.example.inventoryservice.repository.RoomRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomServiceImpl(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public List<Room> getRoomsByHotelId(Long id) {

        return roomRepository.findByHotelId(id);
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Room not found with Id: "+id));
    }

    @Override
    public Room addRoomToHotel(Long id, Room room) {
        Hotel hotel = hotelRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Hotel not found with Id: "+ id));
        room.setHotel(hotel);
        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Long id, Room room) {
        Room updatingRoom = roomRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Room not found with Id:"+ id));
        updatingRoom.setRoomType(room.getRoomType());
        updatingRoom.setMaxAdults(room.getMaxAdults());
        updatingRoom.setMaxChildren(room.getMaxChildren());
        updatingRoom.setHotel(room.getHotel());
        updatingRoom.setPricePerNight(room.getPricePerNight());
        return roomRepository.save(updatingRoom);

    }


    @Override
    public void updateRoomAvailability(Long id, boolean isAvailable) {
        Room updateRoom = roomRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Room not found with Id: "+id));
        updateRoom.setAvailable(isAvailable);
        roomRepository.save(updateRoom);

    }

    @Override
    public List<Room> getAvailableRoomsbyHotelsAndDates(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRoomsbyHotelAndDates(hotelId, checkIn, checkOut)
                .stream()
                .map(roomEntity -> {
                    Room dto = new Room();
                    dto.setId(roomEntity.getId());
                    dto.setRoomNumber(roomEntity.getRoomNumber());
                    dto.setRoomType(roomEntity.getRoomType());
                    dto.setPricePerNight(roomEntity.getPricePerNight());
                    dto.setMaxAdults(roomEntity.getMaxAdults());
                    dto.setMaxChildren(roomEntity.getMaxChildren());
                    dto.setAvailable(roomEntity.isAvailable());
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

}
