package com.booking.service;

import com.booking.client.InventoryServiceClient;
import com.booking.dto.BookingRequest;
import com.booking.dto.BookingResponse;
import com.booking.dto.InventoryResponse;
import com.booking.entity.Customer;
import com.booking.event.BookingEvent;
import com.booking.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
@Slf4j
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public BookingService(CustomerRepository customerRepository, InventoryServiceClient inventoryServiceClient, KafkaTemplate<String, BookingEvent> kafkaTemplate) {
        this.customerRepository = customerRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest) {
        //Check if user exists
        final Customer customer = customerRepository.findById(bookingRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        //Check if there is enough inventory
        final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(bookingRequest.getUserId());
        log.info("Inventory Response: {}", inventoryResponse);
        if(inventoryResponse.getCapacity() < bookingRequest.getTicketCount()) {
            throw new  RuntimeException("Capacity Exceeded");
        }
        //create booking
        final BookingEvent bookingEvent = createBookingEvent(bookingRequest, customer, inventoryResponse);
        //send booking to Order Service on a kafka topic
        kafkaTemplate.send("booking", bookingEvent);
        log.info("Booking sent to Kafka: {}", bookingEvent);

        return BookingResponse.builder()
                .userId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }

    private BookingEvent createBookingEvent(final BookingRequest bookingRequest, final Customer customer, final InventoryResponse inventoryResponse) {
        return BookingEvent.builder()
                .userId(customer.getId())
                .eventId(bookingRequest.getEventId())
                .ticketCount(bookingRequest.getTicketCount() + inventoryResponse.getCapacity())
                .totalPrice(inventoryResponse.getTicketPrice().multiply(BigDecimal.valueOf(bookingRequest.getTicketCount())))
                .build();
    }
}
