package com.santander.bootcamp.budget_planner.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseTest {

    @Test
    void shouldSetCreatedAtWhenExpenseIsCreated() {
        Instant before = Instant.now();

        Expense expense = Expense.create(
                ExpenseCategory.GROCERIES,
                Money.brl(new BigDecimal("120.50")),
                null,
                "Compras no mercado"
        );

        Instant after = Instant.now();

        assertThat(expense.getCreatedAt()).isNotNull();
        assertThat(expense.getCreatedAt()).isBetween(before, after);
    }

    @Test
    void shouldKeepOccurredAtNullWhenNotInformed() {
        Expense expense = Expense.create(
                ExpenseCategory.PHARMACY,
                Money.brl(new BigDecimal("32.00")),
                null,
                "Remédio na farmácia"
        );

        assertThat(expense.getOccurredAt()).isNull();
    }

    @Test
    void shouldKeepOccurredAtWhenInformed() {
        Instant exampleTime = Instant.now();

        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                exampleTime,
                "Almoço"
        );

        assertThat(expense.getOccurredAt()).isEqualTo(exampleTime);
    }

    @Test
    void shouldGenerateUniqueIds() {
        Expense firstExpense = Expense.create(
                ExpenseCategory.OTHER,
                Money.brl(new BigDecimal("10.00")),
                null,
                "First expense (test)"
        );
        Expense secondExpense = Expense.create(
                ExpenseCategory.OTHER,
                Money.brl(new BigDecimal("20.00")),
                null,
                "Second expense (test)"
        );

        assertThat(firstExpense.getId()).isNotNull();
        assertThat(secondExpense.getId()).isNotNull();
        assertThat(firstExpense.getId()).isNotEqualTo(secondExpense.getId());
    }
}
