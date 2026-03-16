package com.platform.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String eventId;
    private String orderId;
    private String customerId;
    private String productId;
    private BigDecimal amount;
    private String status;
    private String reason;
    private LocalDateTime timestamp;
}