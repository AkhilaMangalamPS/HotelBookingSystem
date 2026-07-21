package org.example.inventoryservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF since we are working with stateless REST APIs
                .csrf(csrf -> csrf.disable())

                // 2. Open up public access rules
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Let anyone check the connection test loop without a token
                        .requestMatchers("/rooms/test-connection").permitAll()
                        // 🔓 Let anyone fetch room details without needing a token
                        .requestMatchers(HttpMethod.GET, "/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/hotels/**").permitAll()
                        // All other mutating actions (like POST/PUT) remain locked down
                        .anyRequest().authenticated()
                )

                // 3. Configure it as a standard Resource Server to accept Keycloak JWTs
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}