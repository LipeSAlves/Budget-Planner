package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;

import java.math.BigDecimal;
import java.util.List;

public record ExpenseQueryResult(
        int year,
        int month,
        ExpenseCategory categoryFilter,
        List<Expense> expenses,
        BigDecimal totalAmount
) {
}
