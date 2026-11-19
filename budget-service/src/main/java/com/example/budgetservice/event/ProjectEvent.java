package com.example.budgetservice.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProjectEvent(
        String eventType,
        Long projectId,
        String projectName,
        Long ownerId,
        Instant occurredAt
) {
}
