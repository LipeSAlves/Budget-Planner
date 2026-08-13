package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ExpenseQueryServiceIT {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    @Autowired
    private ExpenseQueryService expenseQueryService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldFindExpensesByMonthUsingOccurredAt() {
        expenseRepository.save(Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("40.00")),
                Instant.parse("2026-07-10T12:00:00Z"),
                "Almoço em julho"
        ));
        expenseRepository.save(Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("60.00")),
                Instant.parse("2026-08-10T12:00:00Z"),
                "Almoço em agosto"
        ));

        flushAndClear();

        ExpenseQueryResult result = expenseQueryService.findByMonthAndCategory(2026, 7, ExpenseCategory.RESTAURANT);

        assertThat(result.expenses()).hasSize(1);
        assertThat(result.expenses().getFirst().getDescription()).isEqualTo("Almoço em julho");
        assertThat(result.totalAmount()).isEqualByComparingTo("40.00");
    }

    @Test
    void shouldUseCreatedAtWhenOccurredAtIsNull() {
        expenseRepository.save(Expense.create(
                ExpenseCategory.PHARMACY,
                Money.brl(new BigDecimal("25.00")),
                null,
                "Compra sem data informada"
        ));

        flushAndClear();

        YearMonth currentMonth = YearMonth.now(BRAZIL_ZONE);
        ExpenseQueryResult result = expenseQueryService.findByMonthAndCategory(
                currentMonth.getYear(),
                currentMonth.getMonthValue(),
                ExpenseCategory.PHARMACY
        );

        assertThat(result.expenses()).hasSize(1);
        assertThat(result.totalAmount()).isEqualByComparingTo("25.00");
    }

    @Test
    void shouldReturnAllCategoriesWhenCategoryFilterIsNull() {
        expenseRepository.save(Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("30.00")),
                Instant.parse("2026-06-05T12:00:00Z"),
                "Restaurante"
        ));
        expenseRepository.save(Expense.create(
                ExpenseCategory.GROCERIES,
                Money.brl(new BigDecimal("70.00")),
                Instant.parse("2026-06-15T12:00:00Z"),
                "Mercado"
        ));

        flushAndClear();

        ExpenseQueryResult result = expenseQueryService.findByMonth(2026, 6);

        assertThat(result.expenses()).hasSize(2);
        assertThat(result.totalAmount()).isEqualByComparingTo("100.00");
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
