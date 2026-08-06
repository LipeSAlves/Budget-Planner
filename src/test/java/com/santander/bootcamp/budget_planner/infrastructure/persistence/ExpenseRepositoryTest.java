package com.santander.bootcamp.budget_planner.infrastructure.persistence;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseId;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
@Transactional
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistAndReloadAllFieldsWhenOccurredAtIsNotInformed() {
        Instant beforeCreate = Instant.now();

        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                null,
                "Gastei 45 reais no almoço"
        );

        Instant afterCreate = Instant.now();

        Expense saved = expenseRepository.save(expense);
        flushAndClear();

        Expense found = expenseRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getCategory()).isEqualTo(ExpenseCategory.RESTAURANT);
        assertThat(found.getMoney().amount()).isEqualByComparingTo("45.90");
        assertThat(found.getMoney().currency()).isEqualTo("BRL");
        assertThat(found.getOccurredAt()).isNull();
        assertThat(found.getDescription()).isEqualTo("Gastei 45 reais no almoço");
        assertThat(found.getCreatedAt()).isNotNull();
        assertThat(found.getCreatedAt()).isCloseTo(beforeCreate, within(1, ChronoUnit.SECONDS));
        assertThat(found.getCreatedAt()).isCloseTo(afterCreate, within(1, ChronoUnit.SECONDS));
    }

    @Test
    void shouldPersistAndReloadAllFieldsWhenOccurredAtIsInformed() {
        Instant occurredAt = Instant.parse("2026-08-05T18:45:00Z");

        Expense expense = Expense.create(
                ExpenseCategory.TRANSPORT,
                Money.brl(new BigDecimal("8.50")),
                occurredAt,
                "Uber para o trabalho"
        );

        Expense saved = expenseRepository.save(expense);
        flushAndClear();

        Expense found = expenseRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getCategory()).isEqualTo(ExpenseCategory.TRANSPORT);
        assertThat(found.getMoney().amount()).isEqualByComparingTo("8.50");
        assertThat(found.getOccurredAt()).isEqualTo(occurredAt);
        assertThat(found.getDescription()).isEqualTo("Uber para o trabalho");
        assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldKeepCreatedAtAfterDatabaseRoundTrip() {
        Expense expense = Expense.create(
                ExpenseCategory.HEALTH,
                Money.brl(new BigDecimal("150.00")),
                null,
                "Consulta médica"
        );

        Instant createdAtBeforeSave = expense.getCreatedAt();

        Expense saved = expenseRepository.save(expense);
        flushAndClear();

        Expense found = expenseRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getCreatedAt()).isCloseTo(createdAtBeforeSave, within(1, ChronoUnit.MILLIS));
        assertThat(found.getCreatedAt()).isCloseTo(saved.getCreatedAt(), within(1, ChronoUnit.MILLIS));
    }

    @Test
    void shouldPersistEachExpenseCategory() {
        for (ExpenseCategory category : ExpenseCategory.values()) {
            Expense expense = Expense.create(
                    category,
                    Money.brl(new BigDecimal("10.00")),
                    null,
                    "Gasto em " + category
            );

            Expense saved = expenseRepository.save(expense);
            flushAndClear();

            Expense found = expenseRepository.findById(saved.getId()).orElseThrow();

            assertThat(found.getCategory()).isEqualTo(category);
        }
    }

    @Test
    void shouldReturnEmptyWhenExpenseDoesNotExist() {
        ExpenseId unknownId = ExpenseId.newId();

        assertThat(expenseRepository.findById(unknownId)).isEmpty();
    }

    @Test
    void shouldPersistMonetaryAmountWithTwoDecimalPlaces() {
        Expense expense = Expense.create(
                ExpenseCategory.GROCERIES,
                Money.brl(new BigDecimal("99.99")),
                null,
                "Compras"
        );

        Expense saved = expenseRepository.save(expense);
        flushAndClear();

        Expense found = expenseRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getMoney().amount()).isEqualByComparingTo("99.99");
    }

    @Test
    void shouldSetCreatedAtWithinReasonableTimeOfPersistence() {
        Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);

        Expense expense = Expense.create(
                ExpenseCategory.AUTO,
                Money.brl(new BigDecimal("250.00")),
                null,
                "Combustível"
        );

        Expense saved = expenseRepository.save(expense);
        flushAndClear();

        Instant after = Instant.now().plus(1, ChronoUnit.SECONDS);

        Expense found = expenseRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getCreatedAt()).isBetween(before, after);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
