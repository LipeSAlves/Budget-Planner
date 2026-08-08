package com.santander.bootcamp.budget_planner.application;

public record ExpenseQueryAgentResult(
        String transcription,
        String answerText,
        byte[] answerAudio
) {
}
