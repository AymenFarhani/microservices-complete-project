package com.booking.client;

import com.booking.dto.InventoryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryServiceClient {

    @Value("${inventory.service.url}")
    private String inventoryUrl;

    public InventoryResponse getInventory(final Long eventId){
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(inventoryUrl+"/event/"+eventId, InventoryResponse.class);
    }
}
