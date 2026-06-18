package org.example.hotelreservationsystem.controller;

import org.example.hotelreservationsystem.dto.AuthRequest;
import org.example.hotelreservationsystem.dto.AuthResponse;
import org.example.hotelreservationsystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthCotroller {
    private final AuthService authService;

    public AuthCotroller(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request){
//        return ResponseEntity.ok(authService.login(request));
//    }
}

