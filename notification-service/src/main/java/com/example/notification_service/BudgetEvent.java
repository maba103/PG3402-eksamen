package com.example.notification_service;

import java.math.BigDecimal;
import java.time.Instant;

public record BudgetEvent(
        String eventType,
        Long budgetId,
        Long projectId,
        BigDecimal totalAmount,
        BigDecimal spentAmount,
        BigDecimal percentUsed,
        String currency,
        String status,
        String message,
        Instant occurredAt
) {
}
