package com.example.notification_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class BudgetEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(BudgetEventListener.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public BudgetEventListener(
            ObjectMapper objectMapper,
            NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${budget.messaging.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleBudgetEvent(String message) {
        try {
            BudgetEvent event =
                    objectMapper.readValue(message, BudgetEvent.class);

            Notification notification = new Notification();
            notification.setProjectId(event.projectId());
            notification.setMessage(event.message());

            notificationService.createNotification(notification);

            log.info("Received budget event: {}", event.eventType());

        } catch (Exception e) {
            log.error("Could not process budget event: {}", e.getMessage());
        }
    }
}