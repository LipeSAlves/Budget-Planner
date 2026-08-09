package com.santander.bootcamp.budget_planner.presentation.dto;

import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateExpenseRequest(
        @NotNull ExpenseCategory category,
        @NotNull @DecimalMin(value = "0.01", message = "amount must be bigger than 0") BigDecimal amount,
        // MVP: Currently API only accepts BRL.
        @Pattern(regexp = "BRL", message = "currency must be BRL") String currency,
        Instant occurredAt,
        String description
) {
}
