package com.santander.bootcamp.budget_planner.application;

import java.util.List;

public record ExpenseQueryTextResult(
        String question,
        String answerText,
        List<ExpenseQueryResult> queries
) {
}
