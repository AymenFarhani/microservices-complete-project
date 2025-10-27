package com.ordering.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryServiceClient {
    @Value("${inventory.service.url}")
    private String inventoryUrl;

    public ResponseEntity<Void> updateInventory(Long eventId, Long ticketCount) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(inventoryUrl+"/event/"+eventId+"/capacity/" + ticketCount, null);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
