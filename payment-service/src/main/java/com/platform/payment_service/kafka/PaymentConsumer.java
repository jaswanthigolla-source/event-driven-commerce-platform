package com.platform.paymentservice.kafka;

import com.platform.paymentservice.event.InventoryEvent;
import com.platform.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${kafka.topics.inventory-reserved}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeInventoryReserved(InventoryEvent inventoryEvent) {
        log.info("Received inventory.reserved for orderId: {}", inventoryEvent.getOrderId());
        paymentService.processPayment(inventoryEvent);
    }
}