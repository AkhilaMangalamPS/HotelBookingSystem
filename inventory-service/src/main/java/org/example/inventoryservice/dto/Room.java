package org.example.inventoryservice.dto;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms") // Maps explicitly to your MySQL 'rooms' table
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // Changed to EAGER so the mapping initializes cleanly for JSON delivery
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnoreProperties("rooms")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Hotel hotel;

    @Column(name = "room_number", nullable = false) //  Added missing mapping
    private String roomNumber;

    @Column(name = "type", nullable = false) // Maps 'roomType' variable to 'type' column
    private String roomType;

    @Column(name = "price", nullable = false) // Maps 'pricePerNight' variable to 'price' column
    private double pricePerNight;

    @Column(name = "max_adults")
    private Integer maxAdults;

    @Column(name = "max_children")
    private Integer maxChildren;

    @Column(name = "available", nullable = false) //  Maps 'isAvailable' variable to 'available' column
    private boolean isAvailable;
}