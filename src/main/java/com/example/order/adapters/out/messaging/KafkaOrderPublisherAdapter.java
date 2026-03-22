package com.example.order.adapters.out.messaging;

import com.example.order.application.port.out.OrderEventPublisherPort;
import com.example.order.domain.model.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class KafkaOrderPublisherAdapter implements OrderEventPublisherPort {

    KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public KafkaOrderPublisherAdapter(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishOrderCreated(Order order) {
        String currentTimestamp = LocalDateTime.now().toString();
        OrderEvent event = new OrderEvent(currentTimestamp, order.getId());
        kafkaTemplate.send("orders", event);
    }
}
