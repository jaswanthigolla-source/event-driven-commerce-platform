package com.platform.paymentservice.service;

import com.platform.paymentservice.event.InventoryEvent;
import com.platform.paymentservice.event.PaymentEvent;
import com.platform.paymentservice.kafka.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProducer paymentProducer;

    public void processPayment(InventoryEvent inventoryEvent) {
        log.info("Processing payment for orderId: {}", inventoryEvent.getOrderId());

        // Simulate payment processing — 90% success rate
        boolean paymentSuccess = Math.random() > 0.1;

        if (paymentSuccess) {
            log.info("Payment successful for orderId: {}", inventoryEvent.getOrderId());

            PaymentEvent event = PaymentEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(inventoryEvent.getOrderId())
                    .customerId(inventoryEvent.getCustomerId())
                    .productId(inventoryEvent.getProductId())
                    .amount(BigDecimal.valueOf(99.99))
                    .status("SUCCESS")
                    .reason("Payment processed successfully")
                    .timestamp(LocalDateTime.now())
                    .build();

            paymentProducer.publishPaymentSuccess(event);

        } else {
            log.warn("Payment failed for orderId: {}", inventoryEvent.getOrderId());

            PaymentEvent event = PaymentEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .orderId(inventoryEvent.getOrderId())
                    .customerId(inventoryEvent.getCustomerId())
                    .productId(inventoryEvent.getProductId())
                    .amount(BigDecimal.ZERO)
                    .status("FAILED")
                    .reason("Payment declined by bank")
                    .timestamp(LocalDateTime.now())
                    .build();

            paymentProducer.publishPaymentFailed(event);
        }
    }
}