package com.platform.inventoryservice.kafka;

import com.platform.inventoryservice.event.OrderEvent;
import com.platform.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "${kafka.topics.order-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeOrderCreated(OrderEvent orderEvent) {
        log.info("Received order.created event for orderId: {}", orderEvent.getOrderId());
        inventoryService.processOrder(orderEvent);
    }
}