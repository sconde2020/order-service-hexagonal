package com.example.order.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class KafkaAdminClientConfig {

    KafkaAdmin kafkaAdmin;

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> config = kafkaAdmin.getConfigurationProperties();
        return AdminClient.create(config);
    }
}