package com.santander.bootcamp.budget_planner.infrastructure.persistence;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    void shouldPersistAndLoadExpense() {
        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                null,
                "Gastei 45 reais no almoço"
        );

        Expense saved = expenseRepository.save(expense);

        assertThat(saved.getId()).isNotNull();
        assertThat(expenseRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getCategory()).isEqualTo(ExpenseCategory.RESTAURANT);
                    assertThat(found.getMoney().amount()).isEqualByComparingTo("45.90");
                    assertThat(found.getMoney().currency()).isEqualTo("BRL");
                    assertThat(found.getOccurredAt()).isNull();
                    assertThat(found.getNote()).isEqualTo("Gastei 45 reais no almoço");
                    assertThat(found.getCreatedAt()).isNotNull();
                });
    }
}
