package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;

public record ExpenseRegistrationResult(
        String transcription,
        Expense expense,
        String confirmationMessage,
        byte[] confirmationAudio
) {
}
