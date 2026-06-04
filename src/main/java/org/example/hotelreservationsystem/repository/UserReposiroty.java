package org.example.hotelreservationsystem.repository;

import org.example.hotelreservationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserReposiroty extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
