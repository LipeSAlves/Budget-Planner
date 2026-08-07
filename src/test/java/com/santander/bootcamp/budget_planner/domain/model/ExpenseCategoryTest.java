package com.santander.bootcamp.budget_planner.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseCategoryTest {

    @Test
    void shouldDefineSpokenLabelForEachCategory() {
        for (ExpenseCategory category : ExpenseCategory.values()) {
            assertThat(category.spokenLabel())
                    .isNotBlank()
                    .doesNotContain("_");
        }
    }
}
