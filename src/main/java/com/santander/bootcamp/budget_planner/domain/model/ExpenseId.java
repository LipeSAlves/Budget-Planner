package com.santander.bootcamp.budget_planner.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record ExpenseId(@Column(name = "id", nullable = false, updatable = false) UUID value) {

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