package org.example.hotelreservationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // Maintain web sessions for Thymeleaf UI pages
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .authorizeHttpRequests(auth -> auth
                        // Public Assets and Spring OAuth2 internal endpoints MUST be permitAll
                        .requestMatchers("/api/auth/**", "/login", "/register", "/css/**", "/js/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                        // Admin-restricted areas
                        .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                        // Thymeleaf Protected UI views
                        .requestMatchers("/dashboard", "/search", "/book").authenticated()
                        // Rest API protected endpoints
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )

                //  1. THYMELEAF BROWSER WORKFLOW: Maps Keycloak login responses to your UI sessions
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/dashboard", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(this.userAuthoritiesMapper())
                        )
                )

                //  2. BACKEND REST API WORKFLOW: Parses standalone bearer tokens for native JSON endpoints
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakRoleConverter()))
                );

        return http.build();
    }

    /**
     * Extracts Keycloak roles for the Thymeleaf Browser Login Session
     */
    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Collection<GrantedAuthority> mappedAuthorities = Collections.emptyList();

            for (GrantedAuthority authority : authorities) {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    Map<String, Object> realmAccess = (Map<String, Object>) oidcUserAuthority.getIdToken().getClaims().get("realm_access");
                    if (realmAccess != null && realmAccess.containsKey("roles")) {
                        List<String> roles = (List<String>) realmAccess.get("roles");
                        mappedAuthorities = roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());
                    }
                }
            }
            return mappedAuthorities;
        };
    }

    /**
     *  Extracts Keycloak roles for standalone Backend REST API requests (Bearer Tokens)
     */
    static class KeycloakRoleConverter implements org.springframework.core.convert.converter.Converter<org.springframework.security.oauth2.jwt.Jwt, org.springframework.security.authentication.AbstractAuthenticationToken> {
        @Override
        public org.springframework.security.authentication.AbstractAuthenticationToken convert(org.springframework.security.oauth2.jwt.Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.isEmpty()) {
                return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt, Collections.emptyList());
            }

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                    .collect(Collectors.toList());

            return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt, authorities, jwt.getClaim("preferred_username"));
        }
    }
}