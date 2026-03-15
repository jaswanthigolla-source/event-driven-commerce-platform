package com.platform.orderservice.entity;

public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    INVENTORY_FAILED,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED
}