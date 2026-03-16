package com.platform.notificationservice.service;

import com.platform.notificationservice.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void sendPaymentSuccessNotification(PaymentEvent event) {
        // In production: integrate with SendGrid, Twilio, or AWS SES
        log.info("==============================================");
        log.info("NOTIFICATION SENT TO CUSTOMER: {}", event.getCustomerId());
        log.info("Subject: Order Confirmed!");
        log.info("Message: Your order {} has been confirmed.", event.getOrderId());
        log.info("Amount charged: ${}", event.getAmount());
        log.info("==============================================");
    }

    public void sendPaymentFailedNotification(PaymentEvent event) {
        log.warn("==============================================");
        log.warn("NOTIFICATION SENT TO CUSTOMER: {}", event.getCustomerId());
        log.warn("Subject: Payment Failed");
        log.warn("Message: Your payment for order {} failed.", event.getOrderId());
        log.warn("Reason: {}", event.getReason());
        log.warn("==============================================");
    }
}