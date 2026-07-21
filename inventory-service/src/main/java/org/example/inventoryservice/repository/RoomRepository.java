package org.example.inventoryservice.repository;

import org.example.inventoryservice.dto.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId")
    List<Room> findByHotelId(@Param("hotelId") Long hotelId);

    @Query(value = "SELECT r.* FROM rooms r WHERE r.hotel_id = :hotelId " +
            "AND r.id NOT IN (" +
            " SELECT b.room_id FROM booking_db.booking b " +
            " WHERE (:checkIn < b.check_out_date AND :checkOut > b.check_in_date)" +
            ")", nativeQuery = true)
    List<Room> findAvailableRoomsbyHotelAndDates(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}