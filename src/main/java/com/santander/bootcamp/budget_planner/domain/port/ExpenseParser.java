package com.santander.bootcamp.budget_planner.domain.port;

import com.santander.bootcamp.budget_planner.domain.model.ParsedExpense;

public interface ExpenseParser {

    ParsedExpense parse(String transcription);
}
