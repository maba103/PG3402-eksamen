package com.example.budgetservice.messaging;

import com.example.budgetservice.event.ProjectEvent;
import com.example.budgetservice.service.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProjectEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProjectEventListener.class);

    private final BudgetService budgetService;
    private final ObjectMapper objectMapper;

    public ProjectEventListener(BudgetService budgetService, ObjectMapper objectMapper) {
        this.budgetService = budgetService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${budget.messaging.project-topic}")
    public void onProjectEvent(String message) {
        try {
            ProjectEvent event = objectMapper.readValue(message, ProjectEvent.class);

            if (!isProjectDeleted(event.eventType())) {
                return;
            }
            if (event.projectId() == null) {
                log.warn("Received {} without projectId, ignoring it", event.eventType());
                return;
            }

            boolean deleted = budgetService.deleteBudgetForProject(event.projectId());
            if (deleted) {
                log.info("Project {} was deleted, so its budget was deleted too", event.projectId());
            } else {
                log.info("Project {} was deleted, but it had no budget", event.projectId());
            }
        } catch (Exception e) {
            log.error("Could not handle project event: {}", e.getMessage());
        }
    }

    private boolean isProjectDeleted(String eventType) {
        return "PROJECT_DELETED".equalsIgnoreCase(eventType)
                || "project.deleted".equalsIgnoreCase(eventType);
    }
}