package com.platform.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name("order.created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderDlqTopic() {
        return TopicBuilder.name("order.dlq")
                .partitions(1)
                .replicas(1)
                .build();
    }
}