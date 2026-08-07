package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.Money;

import java.math.BigDecimal;

public final class ExpenseConfirmationMessageFormatter {

    private ExpenseConfirmationMessageFormatter() {
    }

    public static String format(Expense expense) {
        return "Gasto registrado: %s na categoria %s.".formatted(
                formatAmount(expense.getMoney()),
                expense.getCategory().spokenLabel()
        );
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
