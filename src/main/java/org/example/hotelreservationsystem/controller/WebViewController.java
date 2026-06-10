package org.example.hotelreservationsystem.controller;

import org.example.hotelreservationsystem.dto.BookingRequest;
import org.example.hotelreservationsystem.dto.SearchRequest;
import org.example.hotelreservationsystem.dto.SearchResponse;
import org.example.hotelreservationsystem.service.AuthService;
import org.example.hotelreservationsystem.service.ReservationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebViewController {

    private final ReservationService reservationService;
    private final AuthService authService;

    public WebViewController(ReservationService reservationService, AuthService authService) {
        this.reservationService = reservationService;
        this.authService = authService;
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("authRequest", new org.example.hotelreservationsystem.dto.AuthRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute org.example.hotelreservationsystem.dto.AuthRequest authRequest, Model model){
        try{
            authService.register(authRequest);
            return "redirect:/login?success";
        }
        catch(Exception e){
            model.addAttribute("error","Registration failes: "+ e.getMessage());
            model.addAttribute("authRequest",authRequest);
            return "register";
        }

    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Resolves straight to template views login.html
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("username", userDetails.getUsername());
        if (!model.containsAttribute("searchRequest")) {
            model.addAttribute("searchRequest", new SearchRequest());
        }
        return "dashboard";
    }

    @PostMapping("/search")
    public String processSearch(@ModelAttribute SearchRequest searchRequest, RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal UserDetails userDetails) {
        SearchResponse result = reservationService.findCheapestHotel(searchRequest);

        // Save the data inside RedirectAttributes (Flash Attributes)
        // These survive the redirection step and disappear immediately after page rendering
        redirectAttributes.addFlashAttribute("searchRequest", searchRequest);
        redirectAttributes.addFlashAttribute("result", result);

        BookingRequest bookingRequest = new BookingRequest();
        if (result != null && result.getHotelName() != null) {
            bookingRequest.setHotelName(result.getHotelName());
            bookingRequest.setCheckInDate(searchRequest.getCheckInDate());
            bookingRequest.setCheckOutDate(searchRequest.getCheckOutDate());
        }
        redirectAttributes.addFlashAttribute("bookingRequest", bookingRequest);

        // Hard redirect back to dashboard GET route to keep the URL clean
        return "redirect:/dashboard";
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