package com.santander.bootcamp.budget_planner.presentation.dto;

import com.santander.bootcamp.budget_planner.application.ExpenseRegistrationResult;
import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseRegistrationResponseTest {

    @Test
    void shouldMapExpenseWithOccurredAt() {
        Instant occurredAt = Instant.parse("2026-08-05T14:00:00Z");
        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                occurredAt,
                "Gastei 45 reais no restaurante"
        );
        ExpenseRegistrationResult result = new ExpenseRegistrationResult(
                "Gastei 45 reais no restaurante",
                expense
        );

        ExpenseRegistrationResponse response = ExpenseRegistrationResponse.from(result);

        assertThat(response.id()).isEqualTo(expense.getId().value());
        assertThat(response.transcription()).isEqualTo("Gastei 45 reais no restaurante");
        assertThat(response.amount()).isEqualByComparingTo("45.90");
        assertThat(response.currency()).isEqualTo("BRL");
        assertThat(response.category()).isEqualTo(ExpenseCategory.RESTAURANT);
        assertThat(response.occurredAt()).isEqualTo(occurredAt);
        assertThat(response.occurredAtInformed()).isTrue();
        assertThat(response.createdAt()).isEqualTo(expense.getCreatedAt());
    }

    @Test
    void shouldMapExpenseWithoutOccurredAt() {
        Expense expense = Expense.create(
                ExpenseCategory.PHARMACY,
                Money.brl(new BigDecimal("80.00")),
                null,
                "Passei na farmácia e deixei R$80,00"
        );
        ExpenseRegistrationResult result = new ExpenseRegistrationResult(
                "Passei na farmácia e deixei R$80,00",
                expense
        );

        ExpenseRegistrationResponse response = ExpenseRegistrationResponse.from(result);

        assertThat(response.occurredAt()).isNull();
        assertThat(response.occurredAtInformed()).isFalse();
        assertThat(response.category()).isEqualTo(ExpenseCategory.PHARMACY);
    }
}
