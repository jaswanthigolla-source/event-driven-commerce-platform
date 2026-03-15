package com.platform.orderservice.kafka;

import com.platform.orderservice.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;

    public void publishOrderCreated(OrderEvent event) {
        log.info("Publishing order.created event for orderId: {}", event.getOrderId());
        kafkaTemplate.send(orderCreatedTopic, event.getOrderId(), event);
        log.info("Successfully published order.created event: {}", event.getEventId());
    }
}