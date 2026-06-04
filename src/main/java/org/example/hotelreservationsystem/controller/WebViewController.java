package org.example.hotelreservationsystem.controller;

import org.example.hotelreservationsystem.dto.BookingRequest;
import org.example.hotelreservationsystem.dto.SearchRequest;
import org.example.hotelreservationsystem.dto.SearchResponse;
import org.example.hotelreservationsystem.service.ReservationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebViewController {

    private final ReservationService reservationService;

    public WebViewController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Resolves straight to template views login.html
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("searchRequest", new SearchRequest());
        return "dashboard";
    }

    @PostMapping("/search")
    public String processSearch(@ModelAttribute SearchRequest searchRequest, Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        SearchResponse result = reservationService.findCheapestHotel(searchRequest);

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("result", result);

        // Map elements transparently into transactional context storage parameters
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setHotelName(result.getHotelName());
        bookingRequest.setCheckInDate(searchRequest.getCheckInDate());
        bookingRequest.setCheckOutDate(searchRequest.getCheckOutDate());
        model.addAttribute("bookingRequest", bookingRequest);

        return "dashboard";
    }

    @PostMapping("/book")
    public String processBooking(@ModelAttribute BookingRequest bookingRequest, Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String message = reservationService.bookHotel(bookingRequest, userDetails.getUsername());
            model.addAttribute("successMessage", message);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("searchRequest", new SearchRequest());
        return "dashboard";
    }
}