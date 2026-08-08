package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;

import java.math.BigDecimal;

public record ExpenseQueryToolResult(
        int year,
        int month,
        String categoryFilter,
        int expenseCount,
        BigDecimal totalAmount
) {

    public static ExpenseQueryToolResult from(ExpenseQueryResult result) {
        return new ExpenseQueryToolResult(
                result.year(),
                result.month(),
                result.categoryFilter() != null ? result.categoryFilter().name() : null,
                result.expenses().size(),
                result.totalAmount()
        );
    }
}
