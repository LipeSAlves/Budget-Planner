package com.santander.bootcamp.budget_planner.presentation.dto;

import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateExpenseRequest(
        @NotNull ExpenseCategory category,
        @NotNull @DecimalMin(value = "0.01", message = "amount must be bigger than 0") BigDecimal amount,
        String currency,
        Instant occurredAt,
        String description
) {
}
