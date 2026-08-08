package com.santander.bootcamp.budget_planner.infrastructure.ai;

import com.santander.bootcamp.budget_planner.application.ExpenseQueryService;
import com.santander.bootcamp.budget_planner.application.ExpenseQueryToolResult;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ExpenseAiTools {

    private final ExpenseQueryService expenseQueryService;

    public ExpenseAiTools(ExpenseQueryService expenseQueryService) {
        this.expenseQueryService = expenseQueryService;
    }

    @Tool(description = "List personal expenses filtered by calendar month and optional category")
    public ExpenseQueryToolResult listExpenses(
            @ToolParam(description = "Calendar year, e.g. 2026") int year,
            @ToolParam(description = "Calendar month from 1 to 12") int month,
            @ToolParam(required = false) ExpenseCategory category
    ) {
        return ExpenseQueryToolResult.from(
                expenseQueryService.findByMonthAndCategory(year, month, category)
        );
    }
}
