package com.santander.bootcamp.budget_planner.presentation.dto;

import com.santander.bootcamp.budget_planner.domain.model.Expense;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        String category,
        BigDecimal amount,
        String currency,
        Instant occurredAt,
        String description,
        Instant createdAt
) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId().value(),
                expense.getCategory().name(),
                expense.getMoney().amount(),
                expense.getMoney().currency(),
                expense.getOccurredAt(),
                expense.getDescription(),
                expense.getCreatedAt()
        );
    }
}
