package com.orderhub.orders.infrastructure.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.ConfigResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.List;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaTopicConfig.class);

    @Value("${app.kafka.topics.order-created}")
    private String orderCreatedTopic;

    private final KafkaAdmin kafkaAdmin;

    public KafkaTopicConfig(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder
                .name(orderCreatedTopic)
                .partitions(2)
                .replicas(1)
                .config("retention.ms", "300000")
                .config("cleanup.policy", "delete")
                .build();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void configureTopics() {
        log.info("Updating topic configurations...");

        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, orderCreatedTopic);

            String retention_ms = "30000";
            String cleanup_policy = "delete";

            List<AlterConfigOp> configOps = List.of(
                    new AlterConfigOp(new ConfigEntry("retention.ms", retention_ms), AlterConfigOp.OpType.SET),
                    new AlterConfigOp(new ConfigEntry("cleanup.policy", cleanup_policy), AlterConfigOp.OpType.SET)
            );

            admin.incrementalAlterConfigs(Map.of(resource, configOps)).all().get();

            log.info("Topic kafka updated: retention.ms={}, cleanup.policy={}", retention_ms, cleanup_policy);
        } catch (Exception e) {
            log.error("Failed to update topic kafka configs: {}", e.getMessage());
        }
    }
}
