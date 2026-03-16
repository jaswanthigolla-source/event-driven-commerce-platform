package com.platform.notificationservice.kafka;

import com.platform.notificationservice.event.PaymentEvent;
import com.platform.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topics.payment-success}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumePaymentSuccess(PaymentEvent event) {
        log.info("Received payment.success for orderId: {}", event.getOrderId());
        notificationService.sendPaymentSuccessNotification(event);
    }

    @KafkaListener(
            topics = "${kafka.topics.payment-failed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumePaymentFailed(PaymentEvent event) {
        log.warn("Received payment.failed for orderId: {}", event.getOrderId());
        notificationService.sendPaymentFailedNotification(event);
    }
}