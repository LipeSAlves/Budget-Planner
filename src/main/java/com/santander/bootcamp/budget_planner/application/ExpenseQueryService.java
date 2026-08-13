package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
public class ExpenseQueryService {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    private final ExpenseRepository expenseRepository;

    public ExpenseQueryService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ExpenseQueryResult findByMonth(int year, int month) {
        return findByMonthAndCategory(year, month, null);
    }

    public ExpenseQueryResult findByMonthAndCategory(int year, int month, ExpenseCategory category) {
        validateYearMonth(year, month);

        YearMonth yearMonth = YearMonth.of(year, month);
        Instant start = yearMonth.atDay(1).atStartOfDay(BRAZIL_ZONE).toInstant();
        Instant end = yearMonth.plusMonths(1).atDay(1).atStartOfDay(BRAZIL_ZONE).toInstant();

        List<Expense> expenses = expenseRepository.findByEffectiveDateBetween(start, end, category);
        BigDecimal totalAmount = expenses.stream()
                .map(expense -> expense.getMoney().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ExpenseQueryResult(year, month, category, expenses, totalAmount);
    }

    private void validateYearMonth(int year, int month) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("year must be between 2000 and 2100");
        }

        try {
            LocalDate.of(year, month, 1);
        } catch (Exception exception) {
            throw new IllegalArgumentException("month must be between 1 and 12");
        }
    }
}
