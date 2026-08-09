package com.santander.bootcamp.budget_planner.presentation.dto;

import com.santander.bootcamp.budget_planner.application.ExpenseQueryTextResult;

import java.util.List;

public record ExpenseNaturalLanguageQueryResponse(
        String question,
        String answerText,
        List<ExpenseQueryResponse> queries
) {

    public static ExpenseNaturalLanguageQueryResponse from(ExpenseQueryTextResult result) {
        return new ExpenseNaturalLanguageQueryResponse(
                result.question(),
                result.answerText(),
                result.queries().stream().map(ExpenseQueryResponse::from).toList()
        );
    }
}
