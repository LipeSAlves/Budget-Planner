package com.santander.bootcamp.budget_planner.presentation.dto;

import com.santander.bootcamp.budget_planner.application.ExpenseQueryResult;

import java.math.BigDecimal;
import java.util.List;

public record ExpenseQueryResponse(
        int year,
        int month,
        String categoryFilter,
        List<ExpenseResponse> expenses,
        BigDecimal totalAmount
) {

    public static ExpenseQueryResponse from(ExpenseQueryResult result) {
        return new ExpenseQueryResponse(
                result.year(),
                result.month(),
                result.categoryFilter() != null ? result.categoryFilter().name() : null,
                result.expenses().stream().map(ExpenseResponse::from).toList(),
                result.totalAmount()
        );
    }
}
