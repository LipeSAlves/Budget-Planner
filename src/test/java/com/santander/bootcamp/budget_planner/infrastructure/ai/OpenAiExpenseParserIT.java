package com.santander.bootcamp.budget_planner.infrastructure.ai;

import com.santander.bootcamp.budget_planner.application.ExpenseExtractionService;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiExpenseParserIT {

    @Autowired
    private ExpenseExtractionService expenseExtractionService;

    @Test
    void shouldExtractExpenseFromNaturalLanguage() {
        var parsed = expenseExtractionService.extractFromTranscription(
                "Gastei 45 reais no restaurante"
        );

        assertThat(parsed.money().amount()).isEqualByComparingTo(new BigDecimal("45.00"));
        assertThat(parsed.category()).isEqualTo(ExpenseCategory.RESTAURANT);
        assertThat(parsed.occurredAt()).isNull();
    }

    @Test
    void shouldExtractPharmacyExpenseWithoutDate() {
        var parsed = expenseExtractionService.extractFromTranscription(
                "Comprei remédio por 32 reais na farmácia"
        );

        assertThat(parsed.money().amount()).isEqualByComparingTo(new BigDecimal("32.00"));
        assertThat(parsed.category()).isEqualTo(ExpenseCategory.PHARMACY);
        assertThat(parsed.occurredAt()).isNull();
    }
}
