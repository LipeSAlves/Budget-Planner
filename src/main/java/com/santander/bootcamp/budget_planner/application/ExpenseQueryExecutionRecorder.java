package com.santander.bootcamp.budget_planner.application;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExpenseQueryExecutionRecorder {

    private final List<ExpenseQueryResult> executions = new ArrayList<>();

    public void record(ExpenseQueryResult result) {
        executions.add(result);
    }

    public List<ExpenseQueryResult> getExecutions() {
        return List.copyOf(executions);
    }

    public void clear() {
        executions.clear();
    }
}
