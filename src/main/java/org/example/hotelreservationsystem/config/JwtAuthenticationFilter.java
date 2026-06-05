package org.example.hotelreservationsystem.config;

import ch.qos.logback.core.util.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication filter
 *
 * this filter intercepts each HTTP incoming request once per request
 *
 * Extract JWT token from Authorization header
 * Validate the token
 * Retrieve user details from teh token
 * Set authentication in Spirng security context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService){
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    /**
     * This method is executed once per request and responsible for :
     * - Checking for JWT token in request header
     * - Validating token integrity and expiry
     * Setting authentication in securityContext if valid
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            //Extract JWT token from authorization header
            String jwt = parseJwt(request);

            //Proceed only if token id valid
            if(jwt != null && jwtUtils.validateToken(jwt)){

                //Extract username from token
                String username = jwtUtils.getUsernameFromToken(jwt);

                //Load username from database or from userservice
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                //Create authentication token with user authorities
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Set authentication in Spring securoty
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        } catch (Exception e) {
            logger.error("Cannot set authentication: {} ", e);
        }
        filterChain.doFilter(request, response);

    }

    /**
     * Extract JWT token from Authorization header
     *
     * @param request incoming HTTP request
     * @return jWT token if present, otherwise null
     */
    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }
        return null;
    }
}
