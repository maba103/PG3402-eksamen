package com.example.budgetservice.dto;

import java.math.BigDecimal;

public record UpdateBudgetRequest(
        BigDecimal totalAmount,
        Integer warningThresholdPercent,
        String currency
) {
}
