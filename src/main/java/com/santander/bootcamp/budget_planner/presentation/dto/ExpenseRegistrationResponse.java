package com.santander.bootcamp.budget_planner.presentation.dto;

import com.santander.bootcamp.budget_planner.application.ExpenseRegistrationResult;
import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExpenseRegistrationResponse(
        UUID id,
        String transcription,
        BigDecimal amount,
        String currency,
        ExpenseCategory category,
        Instant occurredAt,
        boolean occurredAtInformed,
        Instant createdAt
) {

    public static ExpenseRegistrationResponse from(ExpenseRegistrationResult result) {
        Expense expense = result.expense();

        return new ExpenseRegistrationResponse(
                expense.getId().value(),
                result.transcription(),
                expense.getMoney().amount(),
                expense.getMoney().currency(),
                expense.getCategory(),
                expense.getOccurredAt(),
                expense.getOccurredAt() != null,
                expense.getCreatedAt()
        );
    }
}
