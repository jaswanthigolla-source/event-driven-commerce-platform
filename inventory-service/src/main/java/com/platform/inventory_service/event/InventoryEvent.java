package com.platform.inventoryservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    private String eventId;
    private String orderId;
    private String customerId;
    private String productId;
    private Integer quantity;
    private String status;
    private String reason;
    private LocalDateTime timestamp;
}