package org.example.inventoryservice.Specification;


import org.example.inventoryservice.dto.Hotel;
import org.example.inventoryservice.dto.Room;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class HotelSpecification {
    public static Specification<Hotel> filterHotels(
            String city,
            Integer adults,
            Integer children,
            List<String> selectedAmenities) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (city != null && !city.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("city")),
                        city.toLowerCase().trim()
                ));
            }

            if (adults != null || children != null) {
                Join<Hotel, Room> roomJoin = root.join("rooms");

                if (adults != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(roomJoin.get("maxAdults"), adults));
                }
                if (children != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(roomJoin.get("maxChildren"), children));
                }
            }

            if(selectedAmenities != null && !selectedAmenities.isEmpty()){
                for (String amenity: selectedAmenities) {
                    predicates.add(criteriaBuilder.isMember(amenity, root.get("amenities")));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

