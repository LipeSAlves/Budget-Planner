package com.santander.bootcamp.budget_planner.domain.model;

import java.time.Instant;

public record ParsedExpense(
        Money money,
        ExpenseCategory category,
        Instant occurredAt
) {
}
