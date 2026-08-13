package com.santander.bootcamp.budget_planner.infrastructure.ai;

import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringAiExpenseParserTest {

    @Mock
    private ChatModel chatModel;

    private SpringAiExpenseParser expenseParser;

    @BeforeEach
    void setUp() {
        expenseParser = new SpringAiExpenseParser(chatModel);
    }

    @Test
    void shouldParseExpenseWithOccurredAt() {
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse("""
                {"amount": 45.90, "category": "RESTAURANT", "occurredAt": "2026-08-05T14:00:00Z"}
                """));

        var parsed = expenseParser.parse("Gastei 45 reais no restaurante ontem às 14h");

        assertThat(parsed.money().amount()).isEqualByComparingTo("45.90");
        assertThat(parsed.money().currency()).isEqualTo("BRL");
        assertThat(parsed.category()).isEqualTo(ExpenseCategory.RESTAURANT);
        assertThat(parsed.occurredAt()).isEqualTo(Instant.parse("2026-08-05T14:00:00Z"));
    }

    @Test
    void shouldParseExpenseWithoutOccurredAt() {
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse("""
                {"amount": 32.00, "category": "PHARMACY", "occurredAt": null}
                """));

        var parsed = expenseParser.parse("Comprei remédio por 32 reais na farmácia");

        assertThat(parsed.money().amount()).isEqualByComparingTo("32.00");
        assertThat(parsed.category()).isEqualTo(ExpenseCategory.PHARMACY);
        assertThat(parsed.occurredAt()).isNull();
    }

    @Test
    void shouldParseJsonWrappedInMarkdown() {
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse("""
                ```json
                {"amount": 80.00, "category": "GROCERIES", "occurredAt": null}
                ```
                """));

        var parsed = expenseParser.parse("Gastei 80 reais no mercado");

        assertThat(parsed.category()).isEqualTo(ExpenseCategory.GROCERIES);
        assertThat(parsed.occurredAt()).isNull();
    }

    @Test
    void shouldUseOtherCategoryWhenModelReturnsUnknownValue() {
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse("""
                {"amount": 15.00, "category": "UNKNOWN", "occurredAt": null}
                """));

        var parsed = expenseParser.parse("Gastei 15 reais");

        assertThat(parsed.category()).isEqualTo(ExpenseCategory.OTHER);
    }

    @Test
    void shouldRejectInvalidAmount() {
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse("""
                {"amount": 0, "category": "OTHER", "occurredAt": null}
                """));

        assertThatThrownBy(() -> expenseParser.parse("Não informei valor"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("amount");
    }

    @Test
    void shouldSendPromptWithTranscription() {
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse("""
                {"amount": 10.00, "category": "TRANSPORT", "occurredAt": null}
                """));

        expenseParser.parse("Gastei 10 reais no metrô");

        ArgumentCaptor<Prompt> promptCaptor = ArgumentCaptor.forClass(Prompt.class);
        verify(chatModel).call(promptCaptor.capture());

        assertThat(promptCaptor.getValue().getContents().toString()).contains("Gastei 10 reais no metrô");
    }

    private ChatResponse chatResponse(String content) {
        return ChatResponse.builder()
                .generations(java.util.List.of(new Generation(new AssistantMessage(content))))
                .build();
    }
}
