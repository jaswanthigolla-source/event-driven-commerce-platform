package com.platform.orderservice.service;

import com.platform.orderservice.dto.OrderRequest;
import com.platform.orderservice.entity.Order;
import com.platform.orderservice.event.OrderEvent;
import com.platform.orderservice.kafka.OrderProducer;
import com.platform.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Transactional
    public Order createOrder(OrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());

        // Save order to database
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalAmount(request.getTotalAmount())
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved with id: {}", savedOrder.getId());

        // Publish event to Kafka
        OrderEvent event = OrderEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(savedOrder.getId())
                .customerId(savedOrder.getCustomerId())
                .productId(savedOrder.getProductId())
                .quantity(savedOrder.getQuantity())
                .totalAmount(savedOrder.getTotalAmount())
                .status("CREATED")
                .timestamp(LocalDateTime.now())
                .build();

        orderProducer.publishOrderCreated(event);

        return savedOrder;
    }
}