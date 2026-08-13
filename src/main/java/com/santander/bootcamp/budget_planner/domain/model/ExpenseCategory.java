package com.santander.bootcamp.budget_planner.domain.model;

import java.util.Arrays;
import java.util.stream.Collectors;

//spokenLabel determines brazilian portuguese text-to-speech translation.
public enum ExpenseCategory {
    GROCERIES("mercado"),
    PHARMACY("farmácia"),
    AUTO("automóvel"),
    TRANSPORT("transporte"),
    RESTAURANT("restaurante"),
    HEALTH("saúde"),
    OTHER("outros");

    private final String spokenLabel;

    ExpenseCategory(String spokenLabel) {
        this.spokenLabel = spokenLabel;
    }

    public String spokenLabel() {
        return spokenLabel;
    }

    public static String joinedNames() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
