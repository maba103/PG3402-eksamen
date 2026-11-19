package no.rebuild.projectservice.dto;

public record ProjectSummaryResponse(
        ProjectResponse project,
        BudgetAvailability budgetAvailability,
        BudgetInfo budget
) {
}
