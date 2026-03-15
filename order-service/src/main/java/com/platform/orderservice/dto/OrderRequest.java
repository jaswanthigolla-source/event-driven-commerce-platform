package com.platform.orderservice.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    private String customerId;
    private String productId;
    private Integer quantity;
    private BigDecimal totalAmount;
}