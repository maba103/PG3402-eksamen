package no.rebuild.projectservice.dto;

import no.rebuild.projectservice.model.Project;
import no.rebuild.projectservice.model.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        String address,
        Long ownerId,
        BigDecimal budgetLimit,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt
) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getAddress(),
                project.getOwnerId(),
                project.getBudgetLimit(),
                project.getStatus(),
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedAt()
        );
    }
}
