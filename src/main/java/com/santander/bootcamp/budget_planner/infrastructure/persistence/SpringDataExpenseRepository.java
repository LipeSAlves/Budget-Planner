package com.santander.bootcamp.budget_planner.infrastructure.persistence;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseId;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataExpenseRepository extends JpaRepository<Expense, ExpenseId> {
}
