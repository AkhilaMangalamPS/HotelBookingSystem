package org.example.hotelreservationsystem.service;

import org.example.hotelreservationsystem.model.User;
import org.example.hotelreservationsystem.repository.UserReposiroty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserReposiroty userReposiroty;

    public CustomUserDetailsService(UserReposiroty userReposiroty) {
        this.userReposiroty = userReposiroty;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userReposiroty.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with this name"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );

    }
}
