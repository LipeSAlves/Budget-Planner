package com.santander.bootcamp.budget_planner.domain.model;

public class Expense {
    private final ExpenseId id;

    public Expense(ExpenseId id) {
        this.id = id;
    }

    public ExpenseId getId() {
        return id;
    }
}
