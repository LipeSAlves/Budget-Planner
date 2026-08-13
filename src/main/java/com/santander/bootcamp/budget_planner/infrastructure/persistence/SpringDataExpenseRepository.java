package com.santander.bootcamp.budget_planner.infrastructure.persistence;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

interface SpringDataExpenseRepository extends JpaRepository<Expense, ExpenseId> {

    @Query("""
            SELECT e FROM Expense e
            WHERE COALESCE(e.occurredAt, e.createdAt) >= :start
              AND COALESCE(e.occurredAt, e.createdAt) < :end
              AND (:category IS NULL OR e.category = :category)
            ORDER BY COALESCE(e.occurredAt, e.createdAt) DESC
            """)
    List<Expense> findByEffectiveDateBetween(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("category") ExpenseCategory category
    );
}
