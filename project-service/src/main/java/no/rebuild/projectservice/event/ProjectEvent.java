package no.rebuild.projectservice.event;

import java.time.Instant;

public record ProjectEvent(
        String eventType,
        Long projectId,
        String projectName,
        Long ownerId,
        Instant occurredAt
) {
}