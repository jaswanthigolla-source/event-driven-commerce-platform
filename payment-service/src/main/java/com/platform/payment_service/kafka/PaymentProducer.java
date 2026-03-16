package com.platform.paymentservice.kafka;

import com.platform.paymentservice.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Value("${kafka.topics.payment-success}")
    private String paymentSuccessTopic;

    @Value("${kafka.topics.payment-failed}")
    private String paymentFailedTopic;

    public void publishPaymentSuccess(PaymentEvent event) {
        log.info("Publishing payment.success for orderId: {}", event.getOrderId());
        kafkaTemplate.send(paymentSuccessTopic, event.getOrderId(), event);
    }

    public void publishPaymentFailed(PaymentEvent event) {
        log.warn("Publishing payment.failed for orderId: {}", event.getOrderId());
        kafkaTemplate.send(paymentFailedTopic, event.getOrderId(), event);
    }
}