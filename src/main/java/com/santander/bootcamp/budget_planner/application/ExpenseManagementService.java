package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseId;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExpenseManagementService {

    private final ExpenseRepository expenseRepository;

    public ExpenseManagementService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Optional<Expense> update(
            UUID id,
            ExpenseCategory category,
            BigDecimal amount,
            String currency,
            Instant occurredAt,
            String description
    ) {
        return expenseRepository.findById(ExpenseId.of(id))
                .map(expense -> {
                    Money money = buildMoney(amount, currency);
                    expense.update(category, money, occurredAt, description);
                    return expenseRepository.save(expense);
                });
    }

    public boolean delete(UUID id) {
        ExpenseId expenseId = ExpenseId.of(id);

        if (expenseRepository.findById(expenseId).isEmpty()) {
            return false;
        }

        expenseRepository.deleteById(expenseId);
        return true;
    }

    private Money buildMoney(BigDecimal amount, String currency) {
        if (currency == null || currency.isBlank()) {
            return Money.brl(amount);
        }

        return new Money(amount, currency.strip().toUpperCase());
    }
}
