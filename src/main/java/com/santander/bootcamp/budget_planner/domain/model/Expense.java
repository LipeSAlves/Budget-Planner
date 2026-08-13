package com.santander.bootcamp.budget_planner.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "expenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expense {

    @EmbeddedId
    private ExpenseId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ExpenseCategory category;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false))
    })
    private Money money;

    private Instant occurredAt;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public static Expense create(ExpenseCategory category, Money money, Instant occurredAt, String description) {
        Expense expense = new Expense();
        expense.id = ExpenseId.newId();
        expense.category = category;
        expense.money = money;
        expense.occurredAt = occurredAt;
        expense.description = description;
        expense.createdAt = Instant.now();
        return expense;
    }

    public void update(ExpenseCategory category, Money money, Instant occurredAt, String description) {
        this.category = category;
        this.money = money;
        this.occurredAt = occurredAt;
        this.description = description;
    }
}
