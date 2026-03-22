package com.example.order.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    AdminClient adminClient;

    @Override
    public Health health() {
        try {
            DescribeClusterResult dc = adminClient.describeCluster();
            String clusterId = dc.clusterId().get(2, TimeUnit.SECONDS);
            int nodeCount = dc.nodes().get(2, TimeUnit.SECONDS).size();
            return Health.up()
                    .withDetail("clusterId", clusterId)
                    .withDetail("nodes", nodeCount)
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}