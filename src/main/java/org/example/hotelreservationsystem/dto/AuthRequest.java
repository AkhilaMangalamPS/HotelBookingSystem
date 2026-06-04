package org.example.hotelreservationsystem.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private String role;
    private String customerType;
}
