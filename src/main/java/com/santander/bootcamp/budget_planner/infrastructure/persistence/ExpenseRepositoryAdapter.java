package com.santander.bootcamp.budget_planner.infrastructure.persistence;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseId;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExpenseRepositoryAdapter implements ExpenseRepository {

    private final SpringDataExpenseRepository springDataExpenseRepository;

    public ExpenseRepositoryAdapter(SpringDataExpenseRepository springDataExpenseRepository) {
        this.springDataExpenseRepository = springDataExpenseRepository;
    }

    @Override
    public Expense save(Expense expense) {
        return springDataExpenseRepository.save(expense);
    }

    @Override
    public Optional<Expense> findById(ExpenseId id) {
        return springDataExpenseRepository.findById(id);
    }
}
