package no.rebuild.projectservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BudgetInfo(
        Long id,
        BigDecimal totalAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal percentUsed,
        String status,
        String currency
) {
}