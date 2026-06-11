package org.example.hotelreservationsystem.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.web.server.ServerSecurityMarker;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
    @Async("hotelThreadPool")
    public void sendBookingConfirmationEmail(String username, String hotelName, double amount){
        try{
            String currentThread = Thread.currentThread().getName();
            System.out.println("[" + currentThread + "] Starting connection to email service for username: "+ username);
            //Simulate a 4-second network delay ( connecting to SMTP server nad transferring bytes)
            Thread.sleep(4000);

            System.out.println("[" + currentThread +"] SUCCESS : Email receipt dispatched successfully for $"+ amount + "stay at" + hotelName);

        }
        catch (InterruptedException e){
            System.out.println("Notification thread was interrupted: "+ e.getMessage());
        }
    }
}

