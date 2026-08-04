package com.santander.bootcamp.budget_planner.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

////@Entity
//@Table(name = "expenses")
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense {

    @Id
    private ExpenseId id;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    @Embedded
    private Money money;

    private Instant occurredAt;

    private String note;

    private Instant createdAt;

    public static Expense create(ExpenseCategory category, Money money, Instant occurredAt, String note) {
        Expense expense = new Expense();
        expense.id = ExpenseId.newId();
        expense.category = category;
        expense.money = money;
        expense.occurredAt = occurredAt != null ? occurredAt : Instant.now();
        expense.note = note;
        expense.createdAt = Instant.now();
        return expense;
    }
}
