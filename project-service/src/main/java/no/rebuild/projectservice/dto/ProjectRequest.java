package no.rebuild.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import no.rebuild.projectservice.model.ProjectStatus;

import java.time.LocalDate;

public record ProjectRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name can be at most 255 characters")
        String name,

        @Size(max = 1000, message = "Description can be at most 1000 characters")
        String description,

        String address,

        @NotNull(message = "Owner id is required")
        Long ownerId,

        ProjectStatus status,

        LocalDate startDate,

        LocalDate endDate
) {
}
