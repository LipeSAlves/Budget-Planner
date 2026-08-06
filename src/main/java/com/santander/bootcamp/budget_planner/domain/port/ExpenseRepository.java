package com.santander.bootcamp.budget_planner.domain.port;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseId;

import java.util.Optional;

public interface ExpenseRepository {

    Expense save(Expense expense);

    Optional<Expense> findById(ExpenseId id);
}
