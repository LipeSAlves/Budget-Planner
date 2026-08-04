package com.santander.bootcamp.budget_planner.domain.model;

import jakarta.persistence.Embedded;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {

    public Money {
        Objects.requireNonNull(amount, "a specified amount is required");
        Objects.requireNonNull(currency, "a specified currency is required");

        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be bigger than 0");
        }
    }

    public static Money brl(BigDecimal amount) {
        return new Money(amount, "BRL");
    }
}
