package com.santander.bootcamp.budget_planner.infrastructure.ai;

import com.santander.bootcamp.budget_planner.application.ExpenseQueryResult;
import com.santander.bootcamp.budget_planner.application.ExpenseQueryService;
import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseAiToolsTest {

    @Mock
    private ExpenseQueryService expenseQueryService;

    @InjectMocks
    private ExpenseAiTools expenseAiTools;

    @Test
    void shouldDelegateListExpensesToQueryService() {
        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                Instant.parse("2026-07-15T15:00:00Z"),
                "Almoço"
        );
        ExpenseQueryResult queryResult = new ExpenseQueryResult(
                2026,
                7,
                ExpenseCategory.RESTAURANT,
                List.of(expense),
                new BigDecimal("45.90")
        );

        when(expenseQueryService.findByMonthAndCategory(2026, 7, ExpenseCategory.RESTAURANT))
                .thenReturn(queryResult);

        var toolResult = expenseAiTools.listExpenses(2026, 7, ExpenseCategory.RESTAURANT);

        assertThat(toolResult.year()).isEqualTo(2026);
        assertThat(toolResult.month()).isEqualTo(7);
        assertThat(toolResult.categoryFilter()).isEqualTo("RESTAURANT");
        assertThat(toolResult.expenseCount()).isEqualTo(1);
        assertThat(toolResult.totalAmount()).isEqualByComparingTo("45.90");

        verify(expenseQueryService).findByMonthAndCategory(2026, 7, ExpenseCategory.RESTAURANT);
    }

    @Test
    void shouldListAllCategoriesWhenCategoryIsNull() {
        ExpenseQueryResult queryResult = new ExpenseQueryResult(
                2026,
                7,
                null,
                List.of(),
                BigDecimal.ZERO
        );

        when(expenseQueryService.findByMonthAndCategory(2026, 7, null))
                .thenReturn(queryResult);

        var toolResult = expenseAiTools.listExpenses(2026, 7, null);

        assertThat(toolResult.categoryFilter()).isNull();
        assertThat(toolResult.expenseCount()).isZero();
        assertThat(toolResult.totalAmount()).isEqualByComparingTo("0");
    }
}
