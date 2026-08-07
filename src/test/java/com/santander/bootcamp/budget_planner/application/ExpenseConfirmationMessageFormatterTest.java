package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseConfirmationMessageFormatterTest {

    @Test
    void shouldFormatWholeAmountInReais() {
        Expense expense = Expense.create(
                ExpenseCategory.PHARMACY,
                Money.brl(new BigDecimal("80.00")),
                null,
                "Passei na farmácia"
        );

        assertThat(ExpenseConfirmationMessageFormatter.format(expense))
                .isEqualTo("Gasto registrado: 80 reais na categoria farmácia.");
    }

    @Test
    void shouldFormatAmountWithCents() {
        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                null,
                "Almoço no restaurante"
        );

        assertThat(ExpenseConfirmationMessageFormatter.format(expense))
                .isEqualTo("Gasto registrado: 45 reais e 90 centavos na categoria restaurante.");
    }

    @Test
    void shouldFormatSingleRealAndSingleCentavo() {
        Expense expense = Expense.create(
                ExpenseCategory.TRANSPORT,
                Money.brl(new BigDecimal("1.01")),
                null,
                "Corrida curta"
        );

        assertThat(ExpenseConfirmationMessageFormatter.format(expense))
                .isEqualTo("Gasto registrado: 1 real e 1 centavo na categoria transporte.");
    }
}
