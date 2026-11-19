package rebuild.task.dto;

import rebuild.task.model.Task;
import rebuild.task.model.TaskStatus;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        TaskStatus status,
        LocalDate deadline,
        Long contractorId
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProjectId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDeadline(),
                task.getContractorId()
        );
    }
}