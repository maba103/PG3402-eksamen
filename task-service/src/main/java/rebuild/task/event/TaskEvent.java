package rebuild.task.event;

import java.time.Instant;

public record TaskEvent(
        String eventType,
        Long taskId,
        Long projectId,
        String newStatus,
        Long contractorId,
        Instant timestamp) {
}