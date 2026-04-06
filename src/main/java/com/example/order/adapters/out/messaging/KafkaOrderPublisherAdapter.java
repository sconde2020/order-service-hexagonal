package com.example.order.adapters.out.messaging;

import com.example.order.application.port.out.OrderEventPublisherPort;
import com.example.order.domain.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class KafkaOrderPublisherAdapter implements OrderEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderPublisherAdapter.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public KafkaOrderPublisherAdapter(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishOrderCreated(Order order, String correlationId) {
        String currentTimestamp = LocalDateTime.now().toString();
        OrderEvent event = new OrderEvent(currentTimestamp, order.getId());
        try {
            kafkaTemplate.send("orders", event);
            log.info("Sent order event: {} successfully", event);
        } catch (Exception e) {
            log.error("Error while sending order event", e);
        }
    }
}
