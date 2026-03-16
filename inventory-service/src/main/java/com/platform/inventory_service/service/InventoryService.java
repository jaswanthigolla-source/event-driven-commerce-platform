package com.platform.inventoryservice.service;

import com.platform.inventoryservice.event.InventoryEvent;
import com.platform.inventoryservice.event.OrderEvent;
import com.platform.inventoryservice.kafka.InventoryProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryProducer inventoryProducer;

    // Simulated stock — in real system this would be a database
    private int availableStock = 100;

    public void processOrder(OrderEvent orderEvent) {
        log.info("Processing inventory for orderId: {}", orderEvent.getOrderId());

        if (availableStock >= orderEvent.getQuantity()) {
            // Reserve stock
            availableStock -= orderEvent.getQuantity();
            log.info("Stock reserved. Remaining stock: {}", availableStock);

            InventoryEvent event = InventoryEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(orderEvent.getOrderId())
                    .customerId(orderEvent.getCustomerId())
                    .productId(orderEvent.getProductId())
                    .quantity(orderEvent.getQuantity())
                    .status("RESERVED")
                    .reason("Stock successfully reserved")
                    .timestamp(LocalDateTime.now())
                    .build();

            inventoryProducer.publishInventoryReserved(event);

        } else {
            log.warn("Insufficient stock for orderId: {}", orderEvent.getOrderId());

            InventoryEvent event = InventoryEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(orderEvent.getOrderId())
                    .customerId(orderEvent.getCustomerId())
                    .productId(orderEvent.getProductId())
                    .quantity(orderEvent.getQuantity())
                    .status("FAILED")
                    .reason("Insufficient stock available")
                    .timestamp(LocalDateTime.now())
                    .build();

            inventoryProducer.publishInventoryFailed(event);
        }
    }
}