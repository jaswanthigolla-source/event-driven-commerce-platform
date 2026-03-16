package com.platform.inventoryservice.kafka;

import com.platform.inventoryservice.event.InventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @Value("${kafka.topics.inventory-reserved}")
    private String inventoryReservedTopic;

    @Value("${kafka.topics.inventory-failed}")
    private String inventoryFailedTopic;

    public void publishInventoryReserved(InventoryEvent event) {
        log.info("Publishing inventory.reserved for orderId: {}", event.getOrderId());
        kafkaTemplate.send(inventoryReservedTopic, event.getOrderId(), event);
    }

    public void publishInventoryFailed(InventoryEvent event) {
        log.warn("Publishing inventory.failed for orderId: {}", event.getOrderId());
        kafkaTemplate.send(inventoryFailedTopic, event.getOrderId(), event);
    }
}