package com.santander.bootcamp.budget_planner.infrastructure.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.model.ParsedExpense;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseParser;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

@Component
public class SpringAiExpenseParser implements ExpenseParser {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ChatModel chatModel;

    public SpringAiExpenseParser(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public ParsedExpense parse(String transcription) {
        ChatResponse response = chatModel.call(new Prompt(buildPrompt(transcription)));

        ExpenseParserResponse payload = readPayload(response.getResult().getOutput().getText());
        return toParsedExpense(payload);
    }

    private String buildPrompt(String transcription) {
        LocalDate referenceDate = LocalDate.now(BRAZIL_ZONE);
        String categories = String.join(", ", java.util.Arrays.stream(ExpenseCategory.values())
                .map(Enum::name)
                .toList());

        return """
                You extract expense data from Brazilian Portuguese transcriptions.
                Reference date for relative expressions (hoje, ontem, anteontem): %s

                Valid categories: %s

                Reply ONLY with valid JSON, without markdown:
                {"amount": <number>, "category": "<CATEGORY>", "occurredAt": "<ISO-8601 instant in UTC or null>"}

                Rules:
                - amount: expense value in BRL as a decimal number without currency symbol
                - category: one of the valid categories; use OTHER when unsure
                - occurredAt: expense date/time in UTC using ISO-8601; null when not mentioned

                Transcription: %s
                """.formatted(referenceDate, categories, transcription);
    }

    private ExpenseParserResponse readPayload(String response) {
        try {
            return OBJECT_MAPPER.readValue(extractJson(response), ExpenseParserResponse.class);
        } catch (Exception exception) {
            throw new IllegalArgumentException("unable to parse expense data from model response", exception);
        }
    }

    private String extractJson(String response) {
        String trimmed = response.trim();

        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf('{');
            int end = trimmed.lastIndexOf('}');
            if (start >= 0 && end > start) {
                return trimmed.substring(start, end + 1);
            }
        }

        return trimmed;
    }

    private ParsedExpense toParsedExpense(ExpenseParserResponse payload) {
        if (payload.amount() == null || payload.amount().signum() <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        ExpenseCategory category = parseCategory(payload.category());
        Instant occurredAt = parseOccurredAt(payload.occurredAt());

        return new ParsedExpense(Money.brl(payload.amount()), category, occurredAt);
    }

    private ExpenseCategory parseCategory(String category) {
        if (category == null || category.isBlank()) {
            return ExpenseCategory.OTHER;
        }

        try {
            return ExpenseCategory.valueOf(category.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return ExpenseCategory.OTHER;
        }
    }

    private Instant parseOccurredAt(String occurredAt) {
        if (occurredAt == null || occurredAt.isBlank() || "null".equalsIgnoreCase(occurredAt.trim())) {
            return null;
        }

        try {
            return Instant.parse(occurredAt.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("occurredAt must be a valid ISO-8601 instant", exception);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ExpenseParserResponse(BigDecimal amount, String category, String occurredAt) {
    }
}
