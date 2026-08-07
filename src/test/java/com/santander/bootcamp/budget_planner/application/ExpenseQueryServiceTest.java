package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseQueryServiceTest {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseQueryService expenseQueryService;

    @Test
    void shouldQueryExpensesForMonthWithoutCategoryFilter() {
        YearMonth yearMonth = YearMonth.of(2026, 7);
        Instant start = yearMonth.atDay(1).atStartOfDay(BRAZIL_ZONE).toInstant();
        Instant end = yearMonth.plusMonths(1).atDay(1).atStartOfDay(BRAZIL_ZONE).toInstant();

        Expense restaurantExpense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                Instant.parse("2026-07-15T15:00:00Z"),
                "Almoço"
        );
        Expense pharmacyExpense = Expense.create(
                ExpenseCategory.PHARMACY,
                Money.brl(new BigDecimal("32.00")),
                Instant.parse("2026-07-20T18:00:00Z"),
                "Remédio"
        );

        when(expenseRepository.findByEffectiveDateBetween(start, end, null))
                .thenReturn(List.of(restaurantExpense, pharmacyExpense));

        ExpenseQueryResult result = expenseQueryService.findByMonth(2026, 7);

        assertThat(result.year()).isEqualTo(2026);
        assertThat(result.month()).isEqualTo(7);
        assertThat(result.categoryFilter()).isNull();
        assertThat(result.expenses()).containsExactly(restaurantExpense, pharmacyExpense);
        assertThat(result.totalAmount()).isEqualByComparingTo("77.90");

        verify(expenseRepository).findByEffectiveDateBetween(start, end, null);
    }

    @Test
    void shouldQueryExpensesForMonthAndCategory() {
        YearMonth yearMonth = YearMonth.of(2026, 7);
        Instant start = yearMonth.atDay(1).atStartOfDay(BRAZIL_ZONE).toInstant();
        Instant end = yearMonth.plusMonths(1).atDay(1).atStartOfDay(BRAZIL_ZONE).toInstant();

        Expense restaurantExpense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                Instant.parse("2026-07-15T15:00:00Z"),
                "Almoço"
        );

        when(expenseRepository.findByEffectiveDateBetween(start, end, ExpenseCategory.RESTAURANT))
                .thenReturn(List.of(restaurantExpense));

        ExpenseQueryResult result = expenseQueryService.findByMonthAndCategory(2026, 7, ExpenseCategory.RESTAURANT);

        assertThat(result.categoryFilter()).isEqualTo(ExpenseCategory.RESTAURANT);
        assertThat(result.expenses()).containsExactly(restaurantExpense);
        assertThat(result.totalAmount()).isEqualByComparingTo("45.90");

        verify(expenseRepository).findByEffectiveDateBetween(start, end, ExpenseCategory.RESTAURANT);
    }

    @Test
    void shouldReturnZeroTotalWhenNoExpensesAreFound() {
        YearMonth yearMonth = YearMonth.of(2026, 1);
        Instant start = yearMonth.atDay(1).atStartOfDay(BRAZIL_ZONE).toInstant();
        Instant end = yearMonth.plusMonths(1).atDay(1).atStartOfDay(BRAZIL_ZONE).toInstant();

        when(expenseRepository.findByEffectiveDateBetween(eq(start), eq(end), isNull()))
                .thenReturn(List.of());

        ExpenseQueryResult result = expenseQueryService.findByMonth(2026, 1);

        assertThat(result.expenses()).isEmpty();
        assertThat(result.totalAmount()).isEqualByComparingTo("0");
    }

    @Test
    void shouldRejectInvalidMonth() {
        assertThatThrownBy(() -> expenseQueryService.findByMonth(2026, 13))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("month must be between 1 and 12");
    }

    @Test
    void shouldRejectInvalidYear() {
        assertThatThrownBy(() -> expenseQueryService.findByMonth(1999, 7))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("year must be between 2000 and 2100");
    }
}
