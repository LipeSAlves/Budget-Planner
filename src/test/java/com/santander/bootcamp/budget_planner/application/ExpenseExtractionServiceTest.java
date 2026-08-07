package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.model.ParsedExpense;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseExtractionServiceTest {

    @Mock
    private ExpenseParser expenseParser;

    @InjectMocks
    private ExpenseExtractionService expenseExtractionService;

    @Test
    void shouldDelegateParsingToExpenseParser() {
        ParsedExpense parsedExpense = new ParsedExpense(
                Money.brl(new BigDecimal("45.90")),
                ExpenseCategory.RESTAURANT,
                null
        );
        when(expenseParser.parse("Gastei 45 reais no almoço")).thenReturn(parsedExpense);

        ParsedExpense result = expenseExtractionService.extractFromTranscription("  Gastei 45 reais no almoço  ");

        assertThat(result).isEqualTo(parsedExpense);
        verify(expenseParser).parse("Gastei 45 reais no almoço");
    }

    @Test
    void shouldRejectBlankTranscription() {
        assertThatThrownBy(() -> expenseExtractionService.extractFromTranscription("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("transcription");
    }
}
