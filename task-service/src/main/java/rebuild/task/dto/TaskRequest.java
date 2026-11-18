package rebuild.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequest(
        @NotNull Long projectId,
        @NotBlank String title,
        String description,
        LocalDate deadline
) {
}