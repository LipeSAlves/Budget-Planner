package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;

public final class ExpenseConfirmationMessageFormatter {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    private ExpenseConfirmationMessageFormatter() {
    }

    public static String format(Expense expense) {
        return "Gasto registrado: %s na categoria %s, incluído no mês de %s.".formatted(
                formatAmount(expense.getMoney()),
                expense.getCategory().spokenLabel(),
                formatIncludedMonth(expense)
        );
    }

    private static String formatIncludedMonth(Expense expense) {
        Instant referenceDate = expense.getOccurredAt() != null
                ? expense.getOccurredAt()
                : expense.getCreatedAt();

        return formatMonthName(referenceDate.atZone(BRAZIL_ZONE).getMonthValue());
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

    private static String formatAmount(Money money) {
        BigDecimal amount = money.amount();
        long wholePart = amount.longValue();
        int cents = amount.remainder(BigDecimal.ONE).movePointRight(2).intValue();

        if (cents == 0) {
            return wholePart == 1 ? "1 real" : wholePart + " reais";
        }

        String wholeLabel = wholePart == 0
                ? ""
                : wholePart == 1 ? "1 real e " : wholePart + " reais e ";
        String centsLabel = cents == 1 ? "1 centavo" : cents + " centavos";

        return wholeLabel + centsLabel;
    }
}
