package org.example.hotelreservationsystem.service;


import org.example.hotelreservationsystem.dto.AuthRequest;
import org.example.hotelreservationsystem.dto.AuthResponse;
import org.example.hotelreservationsystem.model.CustomerType;
import org.example.hotelreservationsystem.model.Role;
import org.example.hotelreservationsystem.model.User;
import org.example.hotelreservationsystem.repository.UserReposiroty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserReposiroty userRepository;
    private final PasswordEncoder passwordEncoder;



    public AuthService(UserReposiroty userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;


    }

    public String register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!!");
        }

        Role userRole = (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN"))
                ? Role.ROLE_ADMIN : Role.ROLE_USER;
        CustomerType custType = (request.getCustomerType() != null && request.getCustomerType().equalsIgnoreCase("REWARDS"))
                ? CustomerType.REWARDS : CustomerType.REGULAR;

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .customerType(custType)
                .build();

        userRepository.save(user);
        return "User Registered successfully";
    }


}
