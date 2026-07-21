package org.example.bookingservice.client;


import org.example.bookingservice.dto.Room;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient( name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/rooms/{id}")
    Room getRoomById(@PathVariable Long id);
}
