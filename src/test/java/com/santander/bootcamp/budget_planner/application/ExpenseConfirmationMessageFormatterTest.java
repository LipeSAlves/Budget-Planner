package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseConfirmationMessageFormatterTest {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    @Test
    void shouldFormatWholeAmountInReais() {
        Expense expense = Expense.create(
                ExpenseCategory.PHARMACY,
                Money.brl(new BigDecimal("80.00")),
                Instant.parse("2026-07-10T12:00:00Z"),
                "Passei na farmácia"
        );

        assertThat(ExpenseConfirmationMessageFormatter.format(expense))
                .isEqualTo("Gasto registrado: 80 reais na categoria farmácia, incluído no mês de julho.");
    }

    @Test
    void shouldFormatAmountWithCents() {
        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                Instant.parse("2026-07-15T15:00:00Z"),
                "Almoço no restaurante"
        );

        assertThat(ExpenseConfirmationMessageFormatter.format(expense))
                .isEqualTo("Gasto registrado: 45 reais e 90 centavos na categoria restaurante, incluído no mês de julho.");
    }

    @Test
    void shouldFormatSingleRealAndSingleCentavo() {
        Expense expense = Expense.create(
                ExpenseCategory.TRANSPORT,
                Money.brl(new BigDecimal("1.01")),
                Instant.parse("2026-07-01T03:00:00Z"),
                "Corrida curta"
        );

        assertThat(ExpenseConfirmationMessageFormatter.format(expense))
                .isEqualTo("Gasto registrado: 1 real e 1 centavo na categoria transporte, incluído no mês de julho.");
    }

    @Test
    void shouldUseCreatedAtMonthWhenOccurredAtIsNull() {
        Expense expense = Expense.create(
                ExpenseCategory.GROCERIES,
                Money.brl(new BigDecimal("50.00")),
                null,
                "Compras no mercado"
        );

        String expectedMonth = formatMonthName(
                YearMonth.from(expense.getCreatedAt().atZone(BRAZIL_ZONE)).getMonthValue()
        );

        assertThat(ExpenseConfirmationMessageFormatter.format(expense))
                .isEqualTo("Gasto registrado: 50 reais na categoria mercado, incluído no mês de " + expectedMonth + ".");
    }

    private static String formatMonthName(int month) {
        return switch (month) {
            case 1 -> "janeiro";
            case 2 -> "fevereiro";
            case 3 -> "março";
            case 4 -> "abril";
            case 5 -> "maio";
            case 6 -> "junho";
            case 7 -> "julho";
            case 8 -> "agosto";
            case 9 -> "setembro";
            case 10 -> "outubro";
            case 11 -> "novembro";
            case 12 -> "dezembro";
            default -> throw new IllegalArgumentException("month must be between 1 and 12");
        };
    }
}
