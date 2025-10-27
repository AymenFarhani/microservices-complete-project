package com.inventory.web;

import com.inventory.dto.EventInventoryResponse;
import com.inventory.dto.VenueInventoryResponse;
import com.inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/inventory/events")
    public @ResponseBody ResponseEntity<List<EventInventoryResponse>> getInventoryEvents() {
        List<EventInventoryResponse> events =inventoryService.getAllEvents();
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    @GetMapping("/inventory/venue/{venueId}")
    public @ResponseBody ResponseEntity<VenueInventoryResponse> getInventoryVenue(@PathVariable("venueId") Long venueId) {
        VenueInventoryResponse venueInventoryResponse = inventoryService.getVenueInformation(venueId);
        return ResponseEntity.status(HttpStatus.OK).body(venueInventoryResponse);
    }

    @GetMapping("/inventory/event/{eventId}")
    public ResponseEntity<EventInventoryResponse> getInventoryEvent(@PathVariable("eventId") Long eventId) {
        EventInventoryResponse eventInventoryResponse = inventoryService.getEventInventory(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(eventInventoryResponse);
    }

    @PutMapping("/inventory/event/{eventId}/capacity/{capacity}")
    public ResponseEntity<Void> updateEventCapacity(@PathVariable("eventId") Long eventId, @PathVariable("capacity") Long ticketsBooked) {
        inventoryService.updateEventCapacity(eventId, ticketsBooked);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
