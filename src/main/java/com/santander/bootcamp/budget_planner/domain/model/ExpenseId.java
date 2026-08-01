package com.santander.bootcamp.budget_planner.domain.model;

import java.util.UUID;

public record ExpenseId(UUID value) {
    public ExpenseId {
        if (value == null) {
            throw new IllegalArgumentException("ExpenseId cannot be null");
        }
    }

    public static ExpenseId of(UUID value) {
        return new ExpenseId(value);
    }

    public static ExpenseId newId() {
        return new ExpenseId(UUID.randomUUID());
    }
}