package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseId;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseManagementServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseManagementService expenseManagementService;

    @Test
    void shouldUpdateExistingExpense() {
        UUID id = UUID.randomUUID();
        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                null,
                "Almoço"
        );
        Instant newOccurredAt = Instant.parse("2026-07-15T12:00:00Z");

        when(expenseRepository.findById(ExpenseId.of(id))).thenReturn(Optional.of(expense));
        when(expenseRepository.save(expense)).thenReturn(expense);

        Optional<Expense> updated = expenseManagementService.update(
                id,
                ExpenseCategory.GROCERIES,
                new BigDecimal("80.00"),
                "BRL",
                newOccurredAt,
                "Compras no mercado"
        );

        assertThat(updated).isPresent();
        assertThat(updated.orElseThrow().getCategory()).isEqualTo(ExpenseCategory.GROCERIES);
        assertThat(updated.orElseThrow().getMoney().amount()).isEqualByComparingTo("80.00");
        assertThat(updated.orElseThrow().getOccurredAt()).isEqualTo(newOccurredAt);
        assertThat(updated.orElseThrow().getDescription()).isEqualTo("Compras no mercado");

        verify(expenseRepository).save(expense);
    }

    @Test
    void shouldReturnEmptyWhenExpenseDoesNotExistForUpdate() {
        UUID id = UUID.randomUUID();

        when(expenseRepository.findById(ExpenseId.of(id))).thenReturn(Optional.empty());

        Optional<Expense> updated = expenseManagementService.update(
                id,
                ExpenseCategory.OTHER,
                new BigDecimal("10.00"),
                null,
                null,
                "Teste"
        );

        assertThat(updated).isEmpty();
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    @Test
    void shouldDeleteExistingExpense() {
        UUID id = UUID.randomUUID();
        Expense expense = Expense.create(
                ExpenseCategory.OTHER,
                Money.brl(new BigDecimal("10.00")),
                null,
                "Teste"
        );

        when(expenseRepository.findById(ExpenseId.of(id))).thenReturn(Optional.of(expense));

        boolean deleted = expenseManagementService.delete(id);

        assertThat(deleted).isTrue();
        verify(expenseRepository).deleteById(ExpenseId.of(id));
    }

    @Test
    void shouldReturnFalseWhenExpenseDoesNotExistForDelete() {
        UUID id = UUID.randomUUID();

        when(expenseRepository.findById(ExpenseId.of(id))).thenReturn(Optional.empty());

        boolean deleted = expenseManagementService.delete(id);

        assertThat(deleted).isFalse();
        verify(expenseRepository, never()).deleteById(any(ExpenseId.class));
    }
}
