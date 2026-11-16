package com.example.budgetservice.messaging;

import com.example.budgetservice.event.BudgetEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class BudgetEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BudgetEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public BudgetEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper,
                                @Value("${budget.messaging.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    public void publish(BudgetEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            String key = String.valueOf(event.projectId());

            kafkaTemplate.send(topic, key, json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent {} for project {} to topic {}", event.eventType(), event.projectId(), topic);
                        } else {
                            log.error("Could not send {} to Kafka: {}", event.eventType(), ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Could not send {} to Kafka: {}", event.eventType(), e.getMessage());
        }
    }
}