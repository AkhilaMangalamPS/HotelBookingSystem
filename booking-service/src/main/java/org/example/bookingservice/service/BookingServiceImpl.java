package org.example.bookingservice.service;


import org.example.bookingservice.client.InventoryClient;
import org.example.bookingservice.dto.Booking;
import org.example.bookingservice.dto.BookingStatus;
import org.example.bookingservice.dto.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.bookingservice.repository.BookingRepository;



import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service

public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;

    @Autowired
    private InventoryClient inventoryClient;


    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;

    }

    @Override
    public Booking createBooking(String userEmail, Long roomId, LocalDate checkInDate, LocalDate checkOutdate) {
        Room bookingRoom = inventoryClient.getRoomById(roomId);
        if(!bookingRoom.isAvailable()){
            throw new RuntimeException("Room is currently occupied during selected days");
        }

        List<Booking> activeBooking = bookingRepository.findByRoomIdAndStatusNot(roomId, BookingStatus.CANCELLED);
        for( Booking b : activeBooking){
            if(checkInDate.isBefore(b.getCheckOutDate()) && checkOutdate.isAfter(b.getCheckInDate())){
                throw new RuntimeException(" Room is already occupied");
            }
        }
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutdate);
        double totalAmount = days * bookingRoom.getPricePerNight();

        Booking booking = Booking.builder()
                .userEmail(userEmail)
                .roomId(roomId)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutdate)
                .totalAmount(totalAmount)
                .status(BookingStatus.CONFIRMED)
                .build();
        return bookingRepository.save(booking);


    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new RuntimeException("There is no such booking with Id: "+id));
    }

    @Override
    public List<Booking> getMyBookings(String userEmail) {
        return bookingRepository.findByUserEmail(userEmail);
    }

    @Override
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new RuntimeException("There is no such booking with Id:"+id));
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

    }
}
